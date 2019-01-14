package bluebomb.urlshortener.controller;

import bluebomb.urlshortener.database.api.DatabaseApi;
import bluebomb.urlshortener.exceptions.DatabaseInternalException;
import bluebomb.urlshortener.model.ClickStat;
import bluebomb.urlshortener.model.Stats;
import bluebomb.urlshortener.model.StatsAgent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StatsControllerEndpointTest {
    @Autowired
    DatabaseApi databaseApi;

    @LocalServerPort
    private int port;

    private String sequenceNoneViews;

    private String sequenceWithViews;

    @Before
    public void setup() throws Exception {
        sequenceNoneViews = databaseApi.createShortURL("http://www.google.fr");
        sequenceWithViews = databaseApi.createShortURL("http://www.google.it");
    }

    @Test
    public void requireStaticsAboutSequenceVisited() throws DatabaseInternalException {
        databaseApi.addStats(sequenceWithViews, "Linux", "Firefox");
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<Stats>> responseEntity = restTemplate.exchange("http://localhost:" + port
                        + "/" + sequenceWithViews + "/stats/os/daily?maxAmountOfDataToRetrieve=10",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Stats>>() {
                }
        );
        List<Stats> response = responseEntity.getBody();
        assertEquals(1, response.size());
        Stats stats = response.get(0);
        List<ClickStat> clickStatList = stats.getClickStat();

        assertEquals(1, clickStatList.size());

        ClickStat clickStat = clickStatList.get(0);

        assertEquals("Linux", clickStat.getAgent());

        assertEquals(1, (int) clickStat.getClicks());
    }

    @Test
    public void requireStaticsAboutSequenceNoneVisited() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<Stats>> responseEntity = restTemplate.exchange("http://localhost:" + port
                        + "/" + sequenceNoneViews + "/stats/os/daily?maxAmountOfDataToRetrieve=10",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Stats>>() {
                }
        );
        List<Stats> response = responseEntity.getBody();
        assertEquals(0, response.size());
    }

    @Test
    public void requireStaticsAboutSequenceNoneExist() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.exchange("http://localhost:" + port
                            + "/" + "sequenciaquenoexiste" + "/stats/os/daily?maxAmountOfDataToRetrieve=10",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Stats>>() {
                    }
            );
            assert false;
        } catch (HttpClientErrorException e) {
            assert e.getStatusCode() == HttpStatus.NOT_FOUND;
        }
    }

    @Test
    public void requireStaticsAboutUnavailableParameter() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.exchange("http://localhost:" + port
                            + "/" + sequenceNoneViews + "/stats/parametronoexiste/daily?maxAmountOfDataToRetrieve=10",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Stats>>() {
                    }
            );
            assert false;
        } catch (HttpClientErrorException e) {
            assert e.getStatusCode() == HttpStatus.NOT_FOUND;
        }
    }

    @Test
    public void requireStaticsAboutUnavailableAmountOfData() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.exchange("http://localhost:" + port
                            + "/" + sequenceNoneViews + "/stats/os/daily?maxAmountOfDataToRetrieve=-2",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Stats>>() {
                    }
            );
            assert false;
        } catch (HttpClientErrorException e) {
            assert e.getStatusCode() == HttpStatus.NOT_FOUND;
        }
    }

    @Test
    public void requireStaticsAboutStartDateAfterEndDate() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.exchange("http://localhost:" + port
                            + "/" + sequenceNoneViews + "/stats/os/daily?maxAmountOfDataToRetrieve=10&endDate=2018-12-15&startDate=2019-01-14",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Stats>>() {
                    }
            );
            assert false;
        } catch (HttpClientErrorException e) {
            assert e.getStatusCode() == HttpStatus.NOT_FOUND;
        }
    }

    @Test
    public void requireStaticsAboutEndDateAfterActualDate() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.exchange("http://localhost:" + port
                            + "/" + sequenceNoneViews + "/stats/os/daily?maxAmountOfDataToRetrieve=10&startDate=2018-12-15&endDate=3287-01-14",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Stats>>() {
                    }
            );
            assert false;
        } catch (HttpClientErrorException e) {
            assert e.getStatusCode() == HttpStatus.NOT_FOUND;
        }
    }

    @Test
    public void requireSupportedBrowsers() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<StatsAgent>> responseEntity = restTemplate.exchange("http://localhost:" + port
                        + "/browser/support",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<StatsAgent>>() {
                }
        );

        List<StatsAgent> response = responseEntity.getBody();

        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(response);
        assertEquals(7, response.size());
        assertTrue(response.contains(new StatsAgent("Other")));
    }

    @Test
    public void requireSupportedOs() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<StatsAgent>> responseEntity = restTemplate.exchange("http://localhost:" + port
                        + "/os/support",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<StatsAgent>>() {
                }
        );

        List<StatsAgent> response = responseEntity.getBody();

        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(response);
        assertEquals(8, response.size());
        assertTrue(response.contains(new StatsAgent("Other")));
    }

    @Test
    public void requireSupportedAgentNoneExist() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.exchange("http://localhost:" + port + "/nonexist/support",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<StatsAgent>>() {
                    }
            );
            assert false;
        } catch (HttpClientErrorException e) {
            assert e.getStatusCode() == HttpStatus.NOT_FOUND;
        }
    }
}
