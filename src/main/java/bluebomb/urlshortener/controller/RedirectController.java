package bluebomb.urlshortener.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedirectController {
    @RequestMapping(value = "{sequence}/ads", produces = MediaType.TEXT_HTML_VALUE)
    public String ads(@PathVariable(value = "sequence") String sequence) {
        //TODO emplementar la funcion de busqueda sequence->ads
        return "<html><body> <P align=\"center\">hola! </P> </body> </html>" ;
    }
}