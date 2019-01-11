package bluebomb.urlshortener.database.api;

import bluebomb.urlshortener.database.model.ClickStatDB;
import bluebomb.urlshortener.database.rowmapper.ClickStatDBRowMapper;
import bluebomb.urlshortener.database.rowmapper.ClickStatRowMapper;
import bluebomb.urlshortener.exceptions.DatabaseInternalException;
import bluebomb.urlshortener.model.ClickStat;
import bluebomb.urlshortener.model.RedirectURL;

import bluebomb.urlshortener.model.Stats;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.*;

@Repository
public class DatabaseApi {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private static final String DB_EXCEPTION_MESSAGE = "DB failed at method: ";
    private static final String NOT_SUPPORTED = " not supported";
    public static final String DEFAULT_EMPTY_AD = "empty";
    public static final Integer DEFAULT_NO_AD_TIMEOUT = -1;
    public static final Integer DEFAULT_TIMEOUT = 10;

    public String toSequence(int input) {
        int auxVal;
        StringBuilder bld = new StringBuilder();
        while (input > 0) {
            auxVal = 87 + (input - 1) % 36;
            if (auxVal >= 87 && auxVal <= 96) {
                bld.append((char) (auxVal - 39));
            } else {
                bld.append((char) auxVal);
            }
            input = input / 62;
        }
        return bld.toString();
    }

    private boolean isSupported(String input) {
        return input.equalsIgnoreCase("os") || input.equalsIgnoreCase("browser");
    }

    /**
     * Map List<ClickStatDB> to List<Stats>
     *
     * @param input input
     * @return List<Stats> representation of List<ClickStatDB>
     */
    private List<Stats> formatDailyStats(List<ClickStatDB> input) {
        // If input is empty
        if (input == null || input.isEmpty()) {
            return new ArrayList<>();
        }

        // Input values have been segmentated by parameter, so we don´t need to add parameter to the primary key
        Map<Date, List<ClickStat>> tempReturnValues = new HashMap<>();

        for (ClickStatDB item : input) {
            if (!tempReturnValues.containsKey(item.getDate())) {
                tempReturnValues.put(item.getDate(), new ArrayList<>());
            }
            ClickStat clickStat = new ClickStat(item.getAgent(), item.getClicks());
            tempReturnValues.get(item.getDate()).add(clickStat);
        }

        ArrayList<Stats> toReturn = new ArrayList<>();
        for (Map.Entry<Date, List<ClickStat>> item : tempReturnValues.entrySet()) {
            toReturn.add(new Stats(item.getKey(), item.getValue()));
        }
        return toReturn;
    }

    /**
     * Create a new Direct shortened URL without interstitialURL
     *
     * @param headURL head URL
     * @return shortened URL
     * @throws DatabaseInternalException if database fails doing the operation
     */
    public String createShortURL(@NotNull String headURL) throws DatabaseInternalException {
        return createShortURL(headURL, DEFAULT_EMPTY_AD, DEFAULT_NO_AD_TIMEOUT);
    }

    /**
     * Create a shortened URL with default secondsToRedirect (10)
     *
     * @param headURL         head URL
     * @param interstitialURL interstitial URL
     * @return shortened URL
     * @throws DatabaseInternalException if database fails doing the operation
     */
    public String createShortURL(@NotNull String headURL, String interstitialURL) throws DatabaseInternalException {
        return createShortURL(headURL, interstitialURL, DEFAULT_TIMEOUT);
    }

    /**
     * Create a shortened URL and return the sequence related to it
     *
     * @param headURL           head URL
     * @param interstitialURL   interstitial URL
     * @param secondsToRedirect seconds to redirect
     * @return shortened URL
     * @throws DatabaseInternalException if database fails doing the operation
     */
    public String createShortURL(@NotNull String headURL, String interstitialURL, Integer secondsToRedirect)
            throws DatabaseInternalException {
        try {
            return createNewShortURL(headURL, interstitialURL, secondsToRedirect);
        } catch (Exception e) {
            throw new DatabaseInternalException(DB_EXCEPTION_MESSAGE + "createURL");
        }
    }

    private String createNewShortURL(@NotNull String headURL, String interstitialURL, Integer secondsToRedirect) {
        String query = "SELECT sequence FROM short_url WHERE url = ? AND redirect = ? AND time = ?";
        try {
            return jdbcTemplate.queryForObject(query, new Object[]{headURL, interstitialURL, secondsToRedirect}, String.class);
        } catch (EmptyResultDataAccessException e) {
            query = "insert into short_url(url, redirect, time) values (?, ?, ?)";
            jdbcTemplate.update(query, new Object[]{headURL, interstitialURL, secondsToRedirect});

            query = "SELECT id FROM short_url WHERE url = ? AND redirect = ? AND time = ?";
            int id = jdbcTemplate.queryForObject(query, new Object[]{headURL, interstitialURL, secondsToRedirect}, Integer.class);

            query = "UPDATE short_url set sequence = ? WHERE id = ?";
            String sequence = toSequence(id);
            jdbcTemplate.update(query, new Object[]{sequence, id});
            return sequence;
        }
    }

    /**
     * Return true if sequence exist in DB
     *
     * @param sequence sequence
     * @return true if sequence exists in database
     * @throws DatabaseInternalException if database fails doing the operation
     */
    public boolean containsSequence(@NotNull String sequence) throws DatabaseInternalException {
        String query = "SELECT id FROM short_url WHERE sequence = ?";
        if (sequence == null) {
            throw new DatabaseInternalException(DB_EXCEPTION_MESSAGE + "containsSequence");
        }
        try {
            jdbcTemplate.queryForObject(query, new Object[]{sequence}, String.class);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    /**
     * Update static of some sequence
     *
     * @param sequence sequence
     * @param os       operating system
     * @param browser  browser
     * @return (New operating system number of clicks, New browser number of clicks) or null if sequence non exist
     * @throws DatabaseInternalException if database fails doing the operation
     */
    public ImmutablePair<Integer, Integer> addStats(@NotNull String sequence, @NotNull String os, @NotNull String browser)
            throws DatabaseInternalException {
        if (sequence == null || os == null || browser == null) {
            throw new DatabaseInternalException(DB_EXCEPTION_MESSAGE + "addStats");
        }
        if (containsSequence(sequence)) {
            java.sql.Date nowDate = new java.sql.Date(new Date().getTime());
            int browserClicks = updateBrowserClicks(sequence, browser, nowDate);
            int osClicks = updateOsClicks(sequence, os, nowDate);
            return new ImmutablePair<>(osClicks, browserClicks);
        } else {
            return null;
        }
    }

    private int updateBrowserClicks(@NotNull String sequence, @NotNull String browser, java.sql.Date nowDate) {
        String query;
        Integer aux;
        try {
            query = "SELECT clicks FROM browser_stat WHERE seq = ? AND date = ? AND browser = ?";
            aux = jdbcTemplate.queryForObject(query, new Object[]{sequence, nowDate, browser}, Integer.class);

            query = "UPDATE browser_stat SET clicks = ? WHERE seq = ? AND date = ? AND browser = ?";
            jdbcTemplate.update(query, new Object[]{++aux, sequence, nowDate, browser});
            return aux;
        } catch (EmptyResultDataAccessException e) {
            query = "INSERT INTO browser_stat(seq, date, browser, clicks) VALUES(?, ?, ?, ?)";
            jdbcTemplate.update(query, new Object[]{sequence, nowDate, browser, 1});
            return 1;
        }
    }

    private int updateOsClicks(@NotNull String sequence, @NotNull String os, java.sql.Date nowDate) {
        String query;
        Integer aux;
        try {
            query = "SELECT clicks FROM os_stat WHERE seq = ? AND date = ? AND os = ?";
            aux = jdbcTemplate.queryForObject(query, new Object[]{sequence, nowDate, os}, Integer.class);

            query = "UPDATE os_stat SET clicks = ? WHERE seq = ? AND date = ? AND os = ?";
            jdbcTemplate.update(query, new Object[]{++aux, sequence, nowDate, os});
            return aux;
        } catch (EmptyResultDataAccessException e) {
            query = "INSERT INTO os_stat(seq, date, os, clicks) VALUES(?, ?, ?, ?)";
            jdbcTemplate.update(query, new Object[]{sequence, nowDate, os, 1});
            return 1;
        }
    }

    /**
     * Check if the sequence got add
     *
     * @param sequence sequence
     * @return null if no ad or ad in the other case
     */
    public RedirectURL getAd(@NotNull String sequence) throws DatabaseInternalException {
        if (sequence == null) {
            throw new DatabaseInternalException(DB_EXCEPTION_MESSAGE + "getAd");
        }
        if (containsSequence(sequence)) {
            String query = "SELECT redirect FROM short_url WHERE sequence = ?";
            String interstitialURL = jdbcTemplate.queryForObject(query, new Object[]{sequence}, String.class);
            if (!interstitialURL.equals(DEFAULT_EMPTY_AD)) {
                query = "SELECT time FROM short_url WHERE sequence = ?";
                int secondsToRedirect = jdbcTemplate.queryForObject(query, new Object[]{sequence}, Integer.class);
                return new RedirectURL(secondsToRedirect, interstitialURL);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Get the original url related with sequence if exist, null in other case
     *
     * @param sequence sequence to obtain original URL
     * @return original URL associated with sequence or null if sequence not exist
     * @throws DatabaseInternalException if database fails doing the operation
     */
    public String getHeadURL(@NotNull String sequence) throws DatabaseInternalException {
        if (sequence == null) {
            throw new DatabaseInternalException(DB_EXCEPTION_MESSAGE + "getHeadURL");
        }
        if (containsSequence(sequence)) {
            String query = "SELECT url FROM short_url WHERE sequence = ?";
            return jdbcTemplate.queryForObject(query, new Object[]{sequence}, String.class);
        } else {
            return null;
        }
    }

    /**
     * Return sequence global stats filter by parameter
     *
     * @param sequence  sequence
     * @param parameter parameter (available values: os, browser)
     * @return sequence global stats filter by parameter or empty collection if sequence non exist
     * @throws DatabaseInternalException if database fails doing the operation
     */
    public List<ClickStat> getGlobalStats(@NotNull String sequence, @NotNull String parameter)
            throws DatabaseInternalException {
        if (sequence == null || parameter == null) {
            throw new DatabaseInternalException(DB_EXCEPTION_MESSAGE + "getGlobalStats");
        }
        if (containsSequence(sequence)) {
            if (isSupported(parameter)) {
                String query = "SELECT " + parameter + " AS item, SUM(clicks) AS clicks FROM " + parameter + "_stat WHERE seq = ? GROUP BY seq, " + parameter;
                return jdbcTemplate.query(query, new Object[]{sequence}, new ClickStatRowMapper());
            } else {
                throw new DatabaseInternalException(parameter + NOT_SUPPORTED);
            }
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Return stats associated with sequence filter by parameter
     *
     * @param sequence                  sequence
     * @param parameter                 parameters from which statistics will be obtained
     * @param startDate                 First day to get stats
     * @param endDate                   Last day to get stats
     * @param sortType                  Sort type (based on total clicks)
     * @param maxAmountOfDataToRetrieve max amount of data to retrieve
     * @return stats associated with sequence filter by parameter or null if sequence non exist
     * @throws DatabaseInternalException if database fails doing the operation
     */
    public List<Stats> getDailyStats(String sequence, String parameter, Date startDate, Date endDate, String sortType,
                                     Integer maxAmountOfDataToRetrieve) throws DatabaseInternalException {
        if (sequence == null || parameter == null || startDate == null
                || endDate == null || sortType == null
                || maxAmountOfDataToRetrieve == null) {
            throw new DatabaseInternalException(DB_EXCEPTION_MESSAGE + "getDailyStats");
        }
        if (containsSequence(sequence)) {
            if (isSupported(parameter)) {
                if (sortType.equalsIgnoreCase("asc") || sortType.equalsIgnoreCase("desc")) {
                    String query = "SELECT o.date, o." + parameter + " AS item, o.clicks, (SELECT SUM(clicks) " +
                            "FROM " + parameter + "_stat o2 " +
                            "WHERE o2.seq = ? AND o2.date = o.date " +
                            "GROUP BY o2.date) AS SUM " +
                            "FROM " + parameter + "_stat o " +
                            "WHERE o.seq = ? AND o.date BETWEEN ? AND ? " +
                            "GROUP BY o.date, o." + parameter + ", o.clicks, o.seq " +
                            "ORDER BY SUM " + sortType +
                            " LIMIT ?";
                    List<ClickStatDB> aux = jdbcTemplate.query(query, new Object[]{sequence,
                                    sequence,
                                    startDate,
                                    endDate,
                                    maxAmountOfDataToRetrieve},
                            new ClickStatDBRowMapper());
                    return formatDailyStats(aux);
                } else {
                    throw new DatabaseInternalException(sortType + NOT_SUPPORTED);
                }
            } else {
                throw new DatabaseInternalException(parameter + NOT_SUPPORTED);
            }
        } else {
            return new ArrayList<>();
        }
    }
}
