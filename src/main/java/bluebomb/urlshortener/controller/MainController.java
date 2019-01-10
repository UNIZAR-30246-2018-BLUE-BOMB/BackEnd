package bluebomb.urlshortener.controller;

import bluebomb.urlshortener.database.api.DatabaseApi;
import bluebomb.urlshortener.exceptions.DatabaseInternalException;
import bluebomb.urlshortener.exceptions.DownloadHTMLInternalException;
import bluebomb.urlshortener.exceptions.QrGeneratorBadParametersException;
import bluebomb.urlshortener.exceptions.QrGeneratorInternalException;
import bluebomb.urlshortener.model.ShortResponse;
import bluebomb.urlshortener.model.Size;
import bluebomb.urlshortener.services.AvailableURIChecker;
import bluebomb.urlshortener.services.QRCodeGenerator;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class MainController {

    /**
     * Logger instance
     */
    private static Logger logger = LoggerFactory.getLogger(RedirectController.class);

    private static final String EMPTY = "empty";
    /**
     * Front end base redirect page uri
     */
    @Value("${app.front-end-redirect-uri:}")
    private String frontEndRedirectURI;

    /**
     * Uri of the back end
     */
    @Value("${app.back-end-uri:}")
    private String backEndURI;

    /**
     * Uri of websocket endpoint in the back end
     */
    @Value("${app.back-end-ws-uri:}")
    private String backEndWsURI;

    /**
     * Database methods
     */
    @Autowired
    DatabaseApi databaseApi;

    /**
     * Uri checker service
     */
    @Autowired
    AvailableURIChecker availableURIChecker;

    /**
     * Create new shortened URL
     *
     * @param headURL           URL to be shortened
     * @param interstitialURL   Interstitial URL
     * @param secondsToRedirect Seconds to redirect to complete URL
     * @return Shortened URL and common related URLs
     */
    @CrossOrigin
    @RequestMapping(value = "/short", method = POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ShortResponse getShortURI(@RequestParam(value = "headURL") String headURL,
                                     @RequestParam(value = "interstitialURL", required = false) String interstitialURL,
                                     @RequestParam(value = "secondsToRedirect", required = false) Integer secondsToRedirect)
            throws DatabaseInternalException {
        // Original URL is not reachable
        if (!availableURIChecker.isURLAvailable(headURL)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Original URL is not reachable");
        }

        // Ad URL is not reachable
        if (interstitialURL != null && !availableURIChecker.isURLAvailable(interstitialURL)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ad URL is not reachable");
        }

        // Set a value on secondsToRedirect
        if (interstitialURL == null) {
            interstitialURL = EMPTY;
            secondsToRedirect = -1;
        } else if (secondsToRedirect == null) {
            secondsToRedirect = 10;
        }

        // Add URIs to DB
        String sequence = databaseApi.createShortURL(headURL, interstitialURL, secondsToRedirect);

        // Register the URIs in the available URI checker service
        availableURIChecker.registerURL(headURL);
        if (!interstitialURL.equals(EMPTY)) {
            availableURIChecker.registerURL(interstitialURL);
        }

        return new ShortResponse(sequence, interstitialURL.equals(EMPTY), frontEndRedirectURI, backEndURI, backEndWsURI);
    }

    /**
     * QR code generator service
     */
    @Autowired
    private QRCodeGenerator qrCodeGenerator;

    /**
     * Generates Qr for specific URL
     *
     * @param sequence          Shortened URL sequence code
     * @param size              Size of returned QR
     * @param errorCorrection   Error correction level (L = ~7% correction, M = ~15% correction, Q = ~25% correction, H = ~30% correction)
     * @param margin            Horizontal and vertical margin of the QR in pixels
     * @param qrColorIm         Color of the QR in hexadecimal
     * @param backgroundColorIm Color of the background in hexadecimal
     * @param logo              URL of the logo to personalize QR
     * @param acceptHeader      Accepted return types
     * @return Qr image
     */
    @CrossOrigin
    @RequestMapping(value = "/{sequence}/qr", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public byte[] getQr(@PathVariable(value = "sequence") String sequence,
                        @RequestParam(value = "size", required = false) Size size,
                        @RequestParam(value = "errorCorrection", required = false, defaultValue = "L") String errorCorrection,
                        @RequestParam(value = "margin", required = false, defaultValue = "0") Integer margin,
                        @RequestParam(value = "qrColor", required = false, defaultValue = "0xFF000000") String qrColorIm,
                        @RequestParam(value = "backgroundColor", required = false, defaultValue = "0xFFFFFFFF") String backgroundColorIm,
                        @RequestParam(value = "logo", required = false) String logo,
                        @RequestHeader("Accept") String acceptHeader)
            throws DatabaseInternalException, QrGeneratorBadParametersException, QrGeneratorInternalException {
        // Check sequence
        if (!databaseApi.containsSequence(sequence)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sequence not exist");
        } else if (!availableURIChecker.isSequenceAvailable(sequence)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Original URL is not reachable");
        } else if (!availableURIChecker.isSequenceAdsAvailable(sequence)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Associated ad is not reachable");
        }

        // Check colors
        String goodFormColorsRegExp = "0x[a-f0-9A-F]{8}";
        if (!qrColorIm.matches(goodFormColorsRegExp) || !backgroundColorIm.matches(goodFormColorsRegExp)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "qrColor and backgroundColor must be a hexadecimal aRGB value");
        }

        // Get colors from hexadecimal
        int qrColor = parseHexadecimalToInt(qrColorIm);
        int backgroundColor = parseHexadecimalToInt(backgroundColorIm);

        // Check logo
        if (logo != null && !logo.isEmpty() && !availableURIChecker.isURLAvailable(logo)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Logo resource is not available");
        }

        // Check Size
        if (size == null) {
            size = new Size(500, 500);
        } else {
            if (size.getHeight() <= 0 || size.getWidth() <= 0) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Size is incorrect");
            }
        }

        // Check margins
        if (margin < 0 || margin >= size.getWidth() || margin >= size.getHeight()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Margins are incorrect or bigger than size");
        }

        // Check Error correction
        ErrorCorrectionLevel errorCorrectionLevel = getErrorCorrectionLevel(errorCorrection);

        // Check response type
        QRCodeGenerator.ResponseType responseType;
        if (acceptHeader.contains(MediaType.IMAGE_PNG_VALUE)) {
            responseType = QRCodeGenerator.ResponseType.TYPE_PNG;
        } else if (acceptHeader.contains(MediaType.IMAGE_JPEG_VALUE)) {
            responseType = QRCodeGenerator.ResponseType.TYPE_JPEG;
        } else {
            responseType = QRCodeGenerator.ResponseType.TYPE_PNG;
        }

        // Return generated QR
        return qrCodeGenerator.generate(frontEndRedirectURI + "/" + sequence, responseType, size,
                errorCorrectionLevel, margin, qrColor, backgroundColor, logo);
    }

    /**
     * Transform error correction level codified as string in ErrorCorrectionLevel typo
     *
     * @param errorCorrection string representation of error correction level
     * @return error correction level
     */
    private ErrorCorrectionLevel getErrorCorrectionLevel(String errorCorrection) {
        ErrorCorrectionLevel errorCorrectionLevel;
        switch (errorCorrection) {
            case "L":
                errorCorrectionLevel = ErrorCorrectionLevel.L;
                break;
            case "M":
                errorCorrectionLevel = ErrorCorrectionLevel.M;
                break;
            case "Q":
                errorCorrectionLevel = ErrorCorrectionLevel.Q;
                break;
            case "H":
                errorCorrectionLevel = ErrorCorrectionLevel.H;
                break;
            default:
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error correction level is incorrect");
        }
        return errorCorrectionLevel;
    }

    /**
     * Parse hexadecimal to int
     *
     * @param hex Number in form 0xFFFFFFFF
     * @return Parsed number
     */
    private int parseHexadecimalToInt(String hex) {
        return (int) Long.parseLong(hex.substring(2), 16);
    }

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR,
            reason = "Internal server error")
    @ExceptionHandler({DatabaseInternalException.class, QrGeneratorBadParametersException.class, QrGeneratorInternalException.class})
    public void exceptionHandlerInternalServerError(Exception e) {
        logger.error(e.getMessage());
    }
}
