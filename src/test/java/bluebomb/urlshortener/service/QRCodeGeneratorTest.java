package bluebomb.urlshortener.service;

import bluebomb.urlshortener.model.Size;
import bluebomb.urlshortener.services.QRCodeGenerator;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest

public class QRCodeGeneratorTest {
    @Autowired
    QRCodeGenerator qrCodeGenerator;

    @Test
    public void testQRGenerator() throws Exception{
        String url = "http://www.localhost:3000/asdasdasdas";
        QRCodeGenerator.ResponseType format = QRCodeGenerator.ResponseType.TYPE_PNG;
        Size size = new Size(500, 500);
        ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
        Integer margin = 20;
        int qrColor = 0xFF000000;
        int backgroundColor = 0xFFFFFFFF;

        qrCodeGenerator.generate(url, format, size, errorCorrectionLevel, margin, qrColor, backgroundColor,
                "http://www.unizar.es/profiles/unizarwww/themes/unizar01/img/logo_iberus.png");
    }

    @Test
    public void testQRGeneratorWithOutLogo() throws Exception{
        String url = "http://www.localhost:3000/asdasdasdas";
        QRCodeGenerator.ResponseType format = QRCodeGenerator.ResponseType.TYPE_PNG;
        Size size = new Size(500, 500);
        ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
        Integer margin = 20;
        int qrColor = 0xFF000000;
        int backgroundColor = 0xFFFFFFFF;

        qrCodeGenerator.generate(url, format, size, errorCorrectionLevel, margin, qrColor, backgroundColor,
                null);
    }

    @Test
    public void testQRGeneratorFailSize() {
        String url = "http://www.localhost:3000/asdasdasdas";
        QRCodeGenerator.ResponseType format = QRCodeGenerator.ResponseType.TYPE_PNG;
        Size size = new Size(-500, 500);
        ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
        Integer margin = 20;
        int qrColor = 0xFF000000;
        int backgroundColor = 0xFFFFFFFF;

        try {
            qrCodeGenerator.generate(url, format, size, errorCorrectionLevel, margin, qrColor, backgroundColor,
                    "http://www.unizar.es/profiles/unizarwww/themes/unizar01/img/logo_iberus.png");
            assert false;
        } catch (Exception w) {
            // Nothing to do
        }
    }

    @Test
    public void testQRGeneratorFailMargin() {
        String url = "http://www.localhost:3000/asdasdasdas";
        QRCodeGenerator.ResponseType format = QRCodeGenerator.ResponseType.TYPE_PNG;
        Size size = new Size(500, 500);
        ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
        Integer margin = -20;
        int qrColor = 0xFF000000;
        int backgroundColor = 0xFFFFFFFF;

        try {
            qrCodeGenerator.generate(url, format, size, errorCorrectionLevel, margin, qrColor, backgroundColor,
                    "http://www.unizar.es/profiles/unizarwww/themes/unizar01/img/logo_iberus.png");
            assert false;
        } catch (Exception w) {
            // Nothing to do
        }
    }

    @Test
    public void testQRGeneratorFailLogo() {
        String url = "http://www.localhost:3000/asdasdasdas";
        QRCodeGenerator.ResponseType format = QRCodeGenerator.ResponseType.TYPE_PNG;
        Size size = new Size(500, 500);
        ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
        Integer margin = 20;
        int qrColor = 0xFF000000;
        int backgroundColor = 0xFFFFFFFF;

        try {
            qrCodeGenerator.generate(url, format, size, errorCorrectionLevel, margin, qrColor, backgroundColor,
                    "http://www.unnnnnizar.es/profiles/unizarwww/themes/unizar01/img/logo_iberus.png");
            assert false;
        } catch (Exception w) {
            // Nothing to do
        }
    }
}
