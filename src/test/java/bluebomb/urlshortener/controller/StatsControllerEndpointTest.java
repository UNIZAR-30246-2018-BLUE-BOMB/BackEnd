package bluebomb.urlshortener.controller;

import bluebomb.urlshortener.database.api.DatabaseApi;
import bluebomb.urlshortener.model.ClickStat;
import bluebomb.urlshortener.model.Stats;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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
        databaseApi.addStats(sequenceWithViews, "Linux", "Firefox");
    }

    @Test
    public void requireStaticsAboutSequenceVisited() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<Stats>> responseEntity = restTemplate.exchange("http://localhost:"+Integer.toString(port)
                + "/" + sequenceWithViews + "/stats/os/daily?maxAmountOfDataToRetrieve=10",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Stats>>(){}
                );
        List<Stats> response = responseEntity.getBody();
        assert response.size() == 1;
        Stats stats = response.get(0);
        List<ClickStat> clickStatList = stats.getClickStat();
        assert clickStatList.size() == 1;
        ClickStat clickStat = clickStatList.get(0);

        assert clickStat.getAgent().equals( "Linux");

        assert clickStat.getClicks() == 1;
    }

    @Test
    public void requireStaticsAboutSequenceNoneVisited() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<Stats>> responseEntity = restTemplate.exchange("http://localhost:"+Integer.toString(port)
                        + "/" + sequenceNoneViews + "/stats/os/daily?maxAmountOfDataToRetrieve=10",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Stats>>(){}
        );
        List<Stats> response = responseEntity.getBody();
        assert response.size() == 0;
    }
}
