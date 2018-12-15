package bluebomb.urlshortener.service;

import bluebomb.urlshortener.model.Size;
import bluebomb.urlshortener.qr.QRCodeGenerator;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;

public class QRCodeGeneratorTest {

    @Test
    public void testQRGenerator() {
        String url = "http://www.localhost:3000/asdasdasdas";
        QRCodeGenerator.ResponseType format = QRCodeGenerator.ResponseType.TYPE_PNG;
        Size size = new Size(500, 500);
        ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
        Integer margin = 20;
        int qrColor = 0xFF000000;
        int backgroundColor = 0xFFFFFFFF;
        BufferedImage logo;

        try {
            URL urlR = new URL("http://www.unizar.es/profiles/unizarwww/themes/unizar01/img/logo_iberus.png");
            logo = ImageIO.read(urlR);
        } catch (Exception w) {
            assert false;
            throw new RuntimeException(w);
        }

        try {
            QRCodeGenerator.generate(url, format, size, errorCorrectionLevel, margin, qrColor, backgroundColor, logo);
        } catch (Exception w) {
            assert false;
            throw new RuntimeException(w);
        }

    }
}
