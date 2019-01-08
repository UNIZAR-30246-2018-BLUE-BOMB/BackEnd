package bluebomb.urlshortener.controller;

import bluebomb.urlshortener.database.api.DatabaseApi;
import bluebomb.urlshortener.exceptions.DatabaseInternalException;
import bluebomb.urlshortener.model.Stats;
import bluebomb.urlshortener.model.StatsAgent;
import bluebomb.urlshortener.services.AvailableURIChecker;
import bluebomb.urlshortener.services.UserAgentDetector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RestController
public class StatsController {
    /**
     * Uri checker service
     */
    @Autowired
    AvailableURIChecker availableURIChecker;

    @Autowired
    DatabaseApi databaseApi;

    /**
     * Gets the stats of the shortened URL
     *
     * @param sequence                  Shortened URL sequence code
     * @param parameter                 parameters from which statistics will be obtained
     * @param startDate                 First day to get stats
     * @param endDate                   Last day to get stats
     * @param sortType                  Sort type (based on total clicks)
     * @param maxAmountOfDataToRetrieve Amount of data to get
     * @return the stats of the shortened URL associated with sequence
     */
    @CrossOrigin
    @RequestMapping(value = "/{sequence}/stats/daily", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Stats> getStatsDaily(@PathVariable(value = "sequence") String sequence,
                                          @RequestParam(value = "parameter") String parameter,
                                          @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                          @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                                          @RequestParam(value = "sortType", required = false) String sortType,
                                          @RequestParam(value = "maxAmountOfDataToRetrieve") Integer maxAmountOfDataToRetrieve) {
        // Check sequence
        try {
            if (!databaseApi.containsSequence(sequence)) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Original URL is not available");
            } else if (!availableURIChecker.isSequenceAvailable(sequence)) {

                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Original URL is not available");
            }
        } catch (DatabaseInternalException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error when trying to check if QR exist");
        }

        // Get STATS
        try {
            return databaseApi.getDailyStats(sequence, parameter, startDate, endDate, sortType, maxAmountOfDataToRetrieve);

        } catch (DatabaseInternalException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error when trying obtain Stats from DB");
        }
    }

    /**
     * User agent detector service
     */
    @Autowired
    UserAgentDetector userAgentDetector;

    /**
     * Get supported agents
     *
     * @param element element to get all supported options
     * @return supported agents
     */
    @CrossOrigin
    @RequestMapping(value = "/{element}/support", produces = MediaType.APPLICATION_JSON_VALUE)
    public ArrayList<StatsAgent> getSupportedAgents(@PathVariable(value = "element") String element) {
        ArrayList<StatsAgent> statsAgents;
        switch (element.toLowerCase()) {
            case "os":
                statsAgents = new ArrayList<>(userAgentDetector.getSupportedOS());
                break;
            case "browser":
                statsAgents = new ArrayList<>(userAgentDetector.getSupportedBrowsers());
                break;
            default:
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The searched parameter is not available");
        }
        return statsAgents;
    }
}
