package bluebomb.urlshortener.controller;

import bluebomb.urlshortener.model.RedirectURL;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedirectController {
    @RequestMapping(value = "{sequence}/redirect", produces = MediaType.APPLICATION_JSON_VALUE)
    public RedirectURL redirect(@PathVariable(value = "sequence") String sequence, @RequestHeader("User-Agent") String userAgent) {
        return new RedirectURL("www.google.es", "www.unizar.es");
    }

    @RequestMapping(value = "{sequence}/ads", produces = MediaType.TEXT_HTML_VALUE)
    public String ads(@PathVariable(value = "sequence") String sequence) {
        //TODO emplementar la funcion de busqueda sequence->ads
        return "<html><body> <P align=\"center\">hola! </P> </body> </html>" ;
    }
}