package bluebomb.urlshortener.controller;

import bluebomb.urlshortener.config.CommonValues;
import bluebomb.urlshortener.database.DatabaseApi;
import bluebomb.urlshortener.errors.SequenceNotFoundError;
import bluebomb.urlshortener.exceptions.DatabaseInternalException;
import bluebomb.urlshortener.exceptions.QrGeneratorBadParametersException;
import bluebomb.urlshortener.exceptions.QrGeneratorInternalException;
import bluebomb.urlshortener.model.ShortResponse;
import bluebomb.urlshortener.model.Size;

import bluebomb.urlshortener.services.QRCodeGenerator;
import bluebomb.urlshortener.services.AvailableURIChecker;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class MainController {

    @Autowired
	DatabaseApi databaseApi;

    @GetMapping(value = "/test")
    public String test(@RequestParam(value = "t") String t) throws DatabaseInternalException {
        String retVal = databaseApi.getAd(t) + "";
        return retVal;
    }

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
                                     @RequestParam(value = "secondsToRedirect", required = false) Integer secondsToRedirect) {
        // Original URL is not reachable
        if (!AvailableURIChecker.getInstance().isURLAvailable(headURL)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Original URL is not reachable");
        }

        // Ad URL is not reachable
        if (interstitialURL != null && !AvailableURIChecker.getInstance().isURLAvailable(interstitialURL)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ad URL is not reachable");
        }

        // Set a value on secondsToRedirect
        if (interstitialURL == null) {
            interstitialURL = "empty";
            secondsToRedirect = -1;
        } else if (secondsToRedirect == null) secondsToRedirect = 10;

        String sequence;
        try {
            sequence = databaseApi.createShortURL(headURL, interstitialURL, secondsToRedirect);
        } catch (DatabaseInternalException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error when creating shortened URL");
        }

        AvailableURIChecker.getInstance().registerURL(headURL);

        if (interstitialURL != null) AvailableURIChecker.getInstance().registerURL(interstitialURL);

        return new ShortResponse(sequence, interstitialURL == null);
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
                        @RequestParam(value = "margin", required = false, defaultValue = "3") Integer margin,
                        @RequestParam(value = "qrColor", required = false, defaultValue = "0xFF000000") String qrColorIm,
                        @RequestParam(value = "backgroundColor", required = false, defaultValue = "0xFFFFFFFF") String backgroundColorIm,
                        @RequestParam(value = "logo", required = false) String logo,
                        @RequestHeader("Accept") String acceptHeader) {
        // Check sequence
        try {
            if (!databaseApi.containsSequence(sequence)) {
                throw new SequenceNotFoundError();
            } else if (!AvailableURIChecker.getInstance().isSequenceAvailable(sequence)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Original URL is not available");
            } else if (!AvailableURIChecker.getInstance().isSequenceAdsAvailable(sequence)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Associated ad is not available");
            }
        } catch (DatabaseInternalException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error when trying to check if QR exist");
        }

        int qrColor;
        int backgroundColor;

        // Check colors
        String goodFormColorsRegExp = "0x[a-f0-9A-F]{8}";
        if (!qrColorIm.matches(goodFormColorsRegExp) || !backgroundColorIm.matches(goodFormColorsRegExp)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "qrColor and backgroundColor must be a hexadecimal aRGB value");
        } else {
            qrColor = parseHexadecimalToInt(qrColorIm);
            backgroundColor = parseHexadecimalToInt(backgroundColorIm);
        }

        // Check logo
        if (logo != null && !logo.isEmpty() && !AvailableURIChecker.getInstance().isURLAvailable(logo)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Logo resource is not available");
        }

        // Check Size
        if (size == null) {
            size = new Size(500, 500);
        }

        // Check Error correction
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
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error in error correction level");
        }

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
        try {
            return qrCodeGenerator.generate(CommonValues.FRONT_END_REDIRECT_URI + "/" + sequence, responseType, size,
                    errorCorrectionLevel, margin, qrColor, backgroundColor, logo);
        } catch (QrGeneratorBadParametersException e) {
            // Bad parameters
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (QrGeneratorInternalException e) {
            // Something went wrong in QR generation
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong in QR generation");
        }
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
}
