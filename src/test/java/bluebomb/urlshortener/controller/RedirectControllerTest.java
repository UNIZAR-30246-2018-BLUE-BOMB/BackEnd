package bluebomb.urlshortener.controller;

import bluebomb.urlshortener.database.api.DatabaseApi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RedirectControllerTest {

    @Autowired
    DatabaseApi databaseApi;

    @LocalServerPort
    private int port;

    @Test
    public void adsCorrect() throws Exception {
        String sequence = databaseApi.createShortURL("http://www.google.it", "http://www.google.it");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:" + port
                        + "/" + sequence + "/ads",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<String>() {
                }
        );
        assert responseEntity.getBody() != null && responseEntity.getBody().contains("html");
    }

    @Test
    public void adsSequenceNoAd() throws Exception {
        try {
            String sequence = databaseApi.createShortURL("http://www.google.it");
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
            HttpEntity<String> entity = new HttpEntity<>(null, headers);
            restTemplate.exchange("http://localhost:" + port
                            + "/" + sequence + "/ads",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<String>() {
                    }
            );
            assert false;
        } catch (HttpClientErrorException e) {
            assert e.getStatusCode() == HttpStatus.NOT_FOUND;
        }
    }

    @Test
    public void adsUnavailableSequence() {
        try {
            String sequence = "sequencianoexiste";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
            HttpEntity<String> entity = new HttpEntity<>(null, headers);
            restTemplate.exchange("http://localhost:" + port
                            + "/" + sequence + "/ads",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<String>() {
                    }
            );
            assert false;
        } catch (HttpClientErrorException e) {
            assert e.getStatusCode() == HttpStatus.NOT_FOUND;
        }
    }
}
