package bluebomb.urlshortener.controller;

import bluebomb.urlshortener.model.RedirectURL;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedirectController {
    @RequestMapping(value = "/redirect/{sequence}")
    public RedirectURL redirect(@PathVariable(value = "sequence") String sequence, @RequestHeader("User-Agent") String userAgent) {
        System.out.println(userAgent);
        return new RedirectURL("www.google.es", "www.unizar.es");
    }
}