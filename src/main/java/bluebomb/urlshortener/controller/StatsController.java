package bluebomb.urlshortener.controller;

import bluebomb.urlshortener.database.api.DatabaseApi;
import bluebomb.urlshortener.exceptions.DatabaseInternalException;
import bluebomb.urlshortener.model.Stats;
import bluebomb.urlshortener.model.StatsAgent;
import bluebomb.urlshortener.services.AvailableURIChecker;
import bluebomb.urlshortener.services.UserAgentDetector;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@RestController
public class StatsController {
    /**
     * Logger instance
     */
    private static Logger logger = LoggerFactory.getLogger(StatsController.class);

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
    @RequestMapping(value = "/{sequence}/stats/{parameter}/daily", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Stats> getStatsDaily(@PathVariable(value = "sequence") String sequence,
                                     @PathVariable(value = "parameter") String parameter,
                                     @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                     @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                                     @RequestParam(value = "sortType", required = false, defaultValue = "asc") String sortType,
                                     @RequestParam(value = "maxAmountOfDataToRetrieve") Integer maxAmountOfDataToRetrieve)
            throws DatabaseInternalException {

        // Check parameter
        if (!(parameter.equals("os") || parameter.equals("browser"))) {
            // Unavailable parameter
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unavailable parameter: " + parameter);
        }

        // Check sequence
        if (!databaseApi.containsSequence(sequence)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sequence not exist");
        } else if (!availableURIChecker.isSequenceAvailable(sequence)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Original URL is not reachable");
        }

        // Check maxAmountOfDataToRetrieve
        if (maxAmountOfDataToRetrieve <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "maxAmountOfDataToRetrieve must be greater than 0");
        }

        // Check and set startDate and endDate
        // If startDate is not set, we set the older date that we can
        if (startDate == null) startDate = new Date(0);
        startDate = DateUtils.truncate(startDate, Calendar.DAY_OF_MONTH);

        // If endDate is not set, we set the newer date that we can
        if (endDate == null) endDate = new Date();
        endDate = DateUtils.truncate(endDate, Calendar.DAY_OF_MONTH);

        Date actualServerDate =  DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);

        if (actualServerDate.compareTo(endDate) < 0) {
            // Check if end date is greater than now
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "endDate must be before or equal than today");
        }

        if (endDate.compareTo(startDate) < 0) {
            // endDate is before than startDate
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "endDate must be after than startDate");
        }

        // Get STATS
        return databaseApi.getDailyStats(sequence, parameter, startDate, endDate, sortType, maxAmountOfDataToRetrieve);
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
    public List<StatsAgent> getSupportedAgents(@PathVariable(value = "element") String element) {
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

    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR,
            reason = "Internal server error")
    @ExceptionHandler({DatabaseInternalException.class})
    public void exceptionHandlerInternalServerError(Exception e) {
        logger.error(e.getMessage());
    }
}
