package bluebomb.urlshortener.services;

import bluebomb.urlshortener.exceptions.QrGeneratorBadParametersException;
import bluebomb.urlshortener.exceptions.QrGeneratorInternalException;
import bluebomb.urlshortener.model.Size;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.lang.NonNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class QRCodeGenerator {

    /**
     * Available response types
     */
    public enum ResponseType {
        TYPE_PNG("PNG"),
        TYPE_JPEG("JPEG");

        private final String type;

        ResponseType(final String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    /**
     * Generate QR code
     *
     * @param url                  Url to be inserted as content
     * @param format               Generated image format
     * @param size                 Generated image size
     * @param errorCorrectionLevel Generated image correction level
     * @param margin               Generated image correction margins
     * @param qrColor              Generated image QR color
     * @param backgroundColor      Generated image background color
     * @param logoURL              Generated image optional embedded logo
     * @return Generated QR code
     * @throws QrGeneratorBadParametersException Caused by parameters error
     * @throws QrGeneratorInternalException      Caused by internal function error
     */
    @Cacheable("qrCodesCache")
    public byte[] generate(@NonNull String url, @NonNull ResponseType format, @NonNull Size size, @NonNull ErrorCorrectionLevel errorCorrectionLevel,
                           @NonNull Integer margin, @NonNull int qrColor, @NonNull int backgroundColor, String logoURL)
            throws QrGeneratorBadParametersException, QrGeneratorInternalException {
        // Check size
        if (size.getHeight() <= 0 || size.getWidth() <= 0) {
            throw new QrGeneratorBadParametersException("Height and width of the QR must be greater than 0");
        }

        // Check margin
        if (margin < 0) {
            throw new QrGeneratorBadParametersException("Margin must be a natural number");
        }

        // Download logo
        BufferedImage logo = downloadLogo(logoURL);

        // Generate Qr matrix
        BufferedImage qrImage = generateQrMatrix(url, size, errorCorrectionLevel, margin, qrColor, backgroundColor);

        // Combine logo and matrix
        BufferedImage combinedImage = combineQrAndLogo(size, margin, qrImage, logo);

        // Write to byte stream
        try {
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            ImageIO.write(combinedImage, format.getType(), pngOutputStream);
            return pngOutputStream.toByteArray();
        } catch (IOException e) {
            throw new QrGeneratorInternalException("Qr write to image fails");
        }
    }

    /**
     * Combine Qr and logo
     *
     * @param size    size of the result image
     * @param margin  margins in the result image
     * @param qrImage qr matrix image
     * @param logo    logo image
     * @return combined qr image and logo
     */
    private BufferedImage combineQrAndLogo(@NonNull Size size, @NonNull Integer margin, @NonNull BufferedImage qrImage, BufferedImage logo) {
        if (logo != null) {
            // Logo must be rescaled
            int logoFinalHeight = (size.getHeight() - margin) / 8;
            int logoFinalWidth = (size.getWidth() - margin) / 8;

            Image tmpLogo = logo.getScaledInstance(logoFinalWidth, logoFinalHeight, Image.SCALE_SMOOTH);
            BufferedImage rescaledLogo = new BufferedImage(logoFinalWidth, logoFinalHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2dRescaledLogo = rescaledLogo.createGraphics();
            g2dRescaledLogo.drawImage(tmpLogo, 0, 0, null);
            g2dRescaledLogo.dispose();

            // Combine images
            BufferedImage combinedImage = new BufferedImage(qrImage.getHeight(), qrImage.getWidth(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2gCombinedImage = (Graphics2D) combinedImage.getGraphics();

            g2gCombinedImage.drawImage(qrImage, 0, 0, null);
            g2gCombinedImage.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            // Write logo centred
            g2gCombinedImage.drawImage(rescaledLogo, margin + (size.getWidth() - logoFinalWidth) / 2,
                    margin + (size.getHeight() - logoFinalHeight) / 2, null);

            // qrImage now will point to the combined image
            return combinedImage;
        } else return qrImage;
    }

    /**
     * Generate QR Matrix
     *
     * @param url                  content of generated QR
     * @param size                 size of generated QR
     * @param errorCorrectionLevel error correction level of generated QR
     * @param margin               margin of generated QR
     * @param qrColor              color of generated QR
     * @param backgroundColor      color of the background of the generated QR
     * @return generated QR
     * @throws QrGeneratorInternalException if the generator fails
     */
    private BufferedImage generateQrMatrix(@NonNull String url, @NonNull Size size,
                                           @NonNull ErrorCorrectionLevel errorCorrectionLevel, @NonNull Integer margin,
                                           @NonNull int qrColor, @NonNull int backgroundColor)
            throws QrGeneratorInternalException {
        // Add options
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
        hints.put(EncodeHintType.MARGIN, margin);

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            return MatrixToImageWriter.toBufferedImage(
                    qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, size.getWidth(), size.getHeight(), hints),
                    new MatrixToImageConfig(qrColor, backgroundColor)
            );
        } catch (WriterException e) {
            throw new QrGeneratorInternalException("Qr encoding fails");
        }

    }

    /**
     * Download logo located at logoURL
     *
     * @param logoURL URL where is located the logo
     * @return downloaded logo or null if logoURL is null or empty
     * @throws QrGeneratorBadParametersException if logoURL is not reachable
     */
    private BufferedImage downloadLogo(String logoURL) throws QrGeneratorBadParametersException {
        if (logoURL != null && !logoURL.isEmpty()) {
            try {
                return ImageIO.read(new URL(logoURL));
            } catch (IOException e) {
                throw new QrGeneratorBadParametersException("Logo resource is not available");
            }
        } else return null;
    }
}