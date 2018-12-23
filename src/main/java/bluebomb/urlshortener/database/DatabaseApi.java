package bluebomb.urlshortener.database;

import bluebomb.urlshortener.config.DbManager;
import bluebomb.urlshortener.exceptions.DatabaseInternalException;
import bluebomb.urlshortener.model.ClickStat;
import bluebomb.urlshortener.model.RedirectURL;

import bluebomb.urlshortener.model.Stats;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseApi {
    private static DatabaseApi ourInstance = new DatabaseApi();

    public static DatabaseApi getInstance() {
        return ourInstance;
    }

    private DatabaseApi() {
    }

    /**
     * Create a new Direct shortened URL without interstitialURL
     *
     * @param headURL head URL
     * @return shortened URL
     * @throws DatabaseInternalException if database fails doing the operation
     */
    public String createShortURL(@NotNull String headURL) throws DatabaseInternalException {
        return createShortURL(headURL, "empty", 10);
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
        return createShortURL(headURL, interstitialURL, 10);
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
        Connection connection = null;
        try {
            connection = DbManager.getConnection();
            String query = "SELECT * FROM new_shortened_url(?,?,?) AS seq";
            PreparedStatement ps =
                    connection.prepareStatement(query,
                            ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, headURL);
            ps.setString(2, interstitialURL);
            ps.setInt(3, secondsToRedirect);
            ResultSet rs = ps.executeQuery(); //Execute query
            if (rs.first()) {
                return rs.getString("seq");
            }
            return null;
            //throw new SQLException();
        } catch (SQLException e) {
            try {
                connection.rollback();
                throw new DatabaseInternalException("createShortUrl failed, rolling back");
            } catch (SQLException e1) {
                throw new DatabaseInternalException("createShortUrl failed, cannot roll back");
            }
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DatabaseInternalException("Cannot close connection");
            }
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
        Connection connection = null;
        try {
            connection = DbManager.getConnection();
            String query = "SELECT * FROM short_sequences WHERE seq = ?";
            PreparedStatement ps =
                    connection.prepareStatement(query,
                            ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, sequence); //replace seq = ? -> seq = sequence
            ResultSet rs = ps.executeQuery(); //Execute query
            return rs.first(); //Return if result is not empty
        } catch (SQLException e) {
            throw new DatabaseInternalException("containsSequence failed");
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DatabaseInternalException("Cannot close connection");
            }
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
        Connection connection = null;
        try {
            connection = DbManager.getConnection();
            String query = "SELECT * FROM insert_stat(?,?,?)";
            PreparedStatement ps = 
                connection.prepareStatement(query, 
                                            ResultSet.TYPE_SCROLL_SENSITIVE, 
                                            ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, sequence); 
            ps.setString(2, browser.toLowerCase());
            ps.setString(3, os.toLowerCase());

            ResultSet rs = ps.executeQuery(); //Execute query
            if(rs.first()) {
                return new ImmutablePair<Integer,Integer>(rs.getInt("os"), 
                                                        rs.getInt("browser"));
            }
            return null;      
            //throw new SQLException();    
        } catch (SQLException e) {
            try {
                connection.rollback();
                throw new DatabaseInternalException("addStats failed, rolling back");
            } catch (SQLException e1) {
                throw new DatabaseInternalException("addStats failed, cannot roll back");
            }
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DatabaseInternalException("Cannot close connection");
            }
        }
    }

    /**
     * Check if the sequence got add
     *
     * @param sequence
     * @return null if no ad or ad in the other case
     */
    public RedirectURL getAd(@NotNull String sequence) throws DatabaseInternalException {
        Connection connection = null;
        try {
            connection = DbManager.getConnection();
            String query = "SELECT * FROM get_ad(?)";
            PreparedStatement ps =
                    connection.prepareStatement(query,
                            ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, sequence);
            ResultSet rs = ps.executeQuery();
            if(rs.first()) {
                return new RedirectURL(rs.getInt("t_out"), rs.getString("ad"));
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseInternalException("getAd failed");
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DatabaseInternalException("Cannot close connection");
            }
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
        Connection connection = null;
        try {
            connection = DbManager.getConnection();
            String query = "SELECT * FROM get_head_url(?) AS url";
            PreparedStatement ps =
                    connection.prepareStatement(query,
                            ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, sequence);
            ResultSet rs = ps.executeQuery();
            if(rs.first()) {
                return rs.getString("url");
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseInternalException("getHeadURL failed");
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DatabaseInternalException("Cannot close connection");
            }
        }
    }

    /**
     * Return sequence global stats filter by parameter
     *
     * @param sequence  sequence
     * @param parameter parameter (available values: os, browser)
     * @return sequence global stats filter by parameter or null if sequence non exist
     * @throws DatabaseInternalException if database fails doing the operation
     */
    public ArrayList<ClickStat> getGlobalStats(@NotNull String sequence, @NotNull String parameter)
            throws DatabaseInternalException {
        Connection connection = null;
        ArrayList<ClickStat> retVal = new ArrayList<ClickStat>();
        String query = "";
        switch (parameter.toLowerCase()) {
            case "os":
                query = "SELECT * FROM get_os_global_stats(?)";
                break;
            case "browser":
                query = "SELECT * FROM get_browser_global_stats(?)";
                break;
            default:
                throw new DatabaseInternalException(parameter + " not supported");
        }

        try {
            connection = DbManager.getConnection();
            PreparedStatement ps =
                    connection.prepareStatement(query,
                            ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, sequence);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                do {
                    retVal.add(new ClickStat(rs.getString("item"), rs.getInt("number")));
                } while (rs.next());
            }
            return retVal;
        } catch (SQLException e) {
            throw new DatabaseInternalException("getGlobalStats failed");
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DatabaseInternalException("Cannot close connection");
            }
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
    public ArrayList<Stats> getDailyStats(String sequence, String parameter, Date startDate, Date endDate, String sortType,
                                          Integer maxAmountOfDataToRetrieve) throws DatabaseInternalException {
        Connection connection = null;
        ArrayList<Stats> retVal = new ArrayList<Stats>();
        String query = "";
        switch(parameter.toLowerCase()){
            case "os":
                query = "SELECT * FROM get_os_daily_stats(?,?,?) ORDER BY SUM " + sortType + " LIMIT ?";
                break;
            case "browser":
                query = "SELECT * FROM get_browser_daily_stats(?,?,?) ORDER BY SUM " + sortType + " LIMIT ?";
                break;
            default:
                throw new DatabaseInternalException(parameter + " not supported");
        }
        try {
            connection = DbManager.getConnection();
            PreparedStatement ps =
                    connection.prepareStatement(query,
                            ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, sequence);
            System.out.println(startDate);
            ps.setDate(2, new java.sql.Date(startDate.getTime()));
            ps.setDate(3, new java.sql.Date(endDate.getTime()));
            ps.setInt(4, maxAmountOfDataToRetrieve);
            ResultSet rs = ps.executeQuery();
            Date aux = null;
            ArrayList<ClickStat> auxStat = null;
            Boolean first = true;
            if(rs.first()) {
                do {
                    if(rs.getDate("Date").equals(aux)) {
                        auxStat.add(new ClickStat(rs.getString("item"), rs.getInt("click")));
                    } else {
                        if(!first) {
                            retVal.add(new Stats(aux, auxStat));
                        }
                        aux = rs.getDate("Date");
                        auxStat = new ArrayList<ClickStat>();
                        auxStat.add(new ClickStat(rs.getString("item"), rs.getInt("click")));
                        first = false;
                    }
                } while (rs.next());
                retVal.add(new Stats(aux, auxStat));
                return retVal;
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseInternalException("getDailyStats failed");
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DatabaseInternalException("Cannot close connection");
            }
        }
    }
}
