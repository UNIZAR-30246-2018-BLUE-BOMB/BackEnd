package bluebomb.urlshortener.service;

import bluebomb.urlshortener.model.Size;
import bluebomb.urlshortener.services.QRCodeGenerator;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QRCodeGeneratorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(QRCodeGeneratorTest.class);

    @Autowired
    QRCodeGenerator qrCodeGenerator;

    @Test
    public void testQRGenerator() {
        String url = "http://www.localhost:3000/asdasdasdas";
        QRCodeGenerator.ResponseType format = QRCodeGenerator.ResponseType.TYPE_PNG;
        Size size = new Size(500, 500);
        ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
        Integer margin = 20;
        int qrColor = 0xFF000000;
        int backgroundColor = 0xFFFFFFFF;

        try {
            qrCodeGenerator.generate(url, format, size, errorCorrectionLevel, margin, qrColor, backgroundColor,
                    "http://www.unizar.es/profiles/unizarwww/themes/unizar01/img/logo_iberus.png");
        } catch (Exception w) {
            assert false;
            throw new RuntimeException(w);
        }

    }

    @Test
    public void testQRGeneratorCache() {
        String url = "http://www.localhost:3000/asdasdasdas";
        QRCodeGenerator.ResponseType format = QRCodeGenerator.ResponseType.TYPE_PNG;
        Size size = new Size(500, 500);
        ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
        Integer margin = 20;
        int qrColor = 0xFF000000;
        int backgroundColor = 0xFFFFFFFF;
        try {
            LOGGER.info("Antes primera petición");
            qrCodeGenerator.generate(url, format, size, errorCorrectionLevel, margin, qrColor, backgroundColor,
                    "http://www.unizar.es/profiles/unizarwww/themes/unizar01/img/logo_iberus.png");
            LOGGER.info("Entre primera y segunda petición");
            qrCodeGenerator.generate(url, format, size, errorCorrectionLevel, margin, qrColor, backgroundColor,
                    "http://www.unizar.es/profiles/unizarwww/themes/unizar01/img/logo_iberus.png");
            LOGGER.info("Después segunda petición");
        } catch (Exception w) {
            assert false;
            throw new RuntimeException(w);
        }

    }
}
