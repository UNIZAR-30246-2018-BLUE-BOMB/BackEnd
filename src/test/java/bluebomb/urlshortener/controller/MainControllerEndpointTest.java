package bluebomb.urlshortener.controller;

import bluebomb.urlshortener.database.api.DatabaseApi;
import bluebomb.urlshortener.model.ShortResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Objects;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MainControllerEndpointTest {
    @Autowired
    DatabaseApi databaseApi;

    @LocalServerPort
    private int port;


    @Test
    public void verifyShortEndPoint() {

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("headURL", "http://www.google.es");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ShortResponse> responseEntity = restTemplate.exchange("http://localhost:" + port
                        + "/short",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<ShortResponse>() {
                }
        );

        assertEquals(201, responseEntity.getStatusCode().value());

        assertNotNull(responseEntity);

        assertEquals("", Objects.requireNonNull(responseEntity.getBody()).getAdsUrl());
    }

    @Test
    public void verifyShortEndPoint2() {

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("headURL", "http://www.google.es");
        map.add("interstitialURL", "http://www.google.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ShortResponse> responseEntity = restTemplate.exchange("http://localhost:" + port
                        + "/short",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<ShortResponse>() {
                }
        );

        assertEquals(201, responseEntity.getStatusCode().value());

        assertNotNull(responseEntity);

        assertNotEquals("", Objects.requireNonNull(responseEntity.getBody()).getAdsUrl());
    }


    @Test
    public void verifyShortEndPoint3() {

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("headURL", "http://www.google.es");
        map.add("interstitialURL", "http://www.google.com");
        map.add("secondsToRedirect", "100");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ShortResponse> responseEntity = restTemplate.exchange("http://localhost:" + port
                        + "/short",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<ShortResponse>() {
                }
        );

        assertEquals(201, responseEntity.getStatusCode().value());

        assertNotNull(responseEntity);

        assertNotEquals("", Objects.requireNonNull(responseEntity.getBody()).getAdsUrl());
    }

    // EXCEPTIONS

    @Test(expected = HttpClientErrorException.class)
    public void verifyExceptionShort() throws HttpClientErrorException {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("headURL", "notrechable");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ShortResponse> responseEntity = restTemplate.exchange("http://localhost:" + port
                        + "/short",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<ShortResponse>() {
                }
        );
    }

    @Test(expected = HttpClientErrorException.class)
    public void verifyExceptionShort2() throws HttpClientErrorException {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("headURL", "http://www.google.es");
        map.add("interstitialURL", "notrechable");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ShortResponse> responseEntity = restTemplate.exchange("http://localhost:" + port
                        + "/short",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<ShortResponse>() {
                }
        );
    }

    @Test(expected = HttpClientErrorException.class)
    public void verifyExceptionShort3() throws HttpClientErrorException {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("headURL", "http://www.google.es");
        map.add("interstitialURL", null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ShortResponse> responseEntity = restTemplate.exchange("http://localhost:" + port
                        + "/short",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<ShortResponse>() {
                }
        );
    }

    @Test
    public void qrCorrect() throws Exception {
        String sequence = databaseApi.createShortURL("http://www.google.it");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.IMAGE_PNG));
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange("http://localhost:" + port
                        + "/" + sequence + "/qr",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<byte[]>() {
                }
        );
        assert responseEntity.getBody().length > 0;
    }

}
