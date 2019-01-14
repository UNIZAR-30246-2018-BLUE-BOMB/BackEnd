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
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MainControllerEndpointTest {
    @Autowired
    DatabaseApi databaseApi;

    @LocalServerPort
    private int port;


    @Test
    public void verifyShortEndPoint() {

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("headURL", "http://www.google.es");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ShortResponse> responseEntity = restTemplate.exchange("http://localhost:"+ port
                        + "/short",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<ShortResponse>(){}
        );

        assertEquals(201, responseEntity.getStatusCode().value());

        assertNotNull(responseEntity);

        assertEquals("", responseEntity.getBody().getAdsUrl());
    }
}
