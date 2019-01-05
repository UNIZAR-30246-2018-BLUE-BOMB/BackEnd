package bluebomb.urlshortener.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import bluebomb.urlshortener.model.ClickStat;;

public class ClickStatRowMapper implements RowMapper<ClickStat> {
    @Override
    public ClickStat mapRow(ResultSet rs, int rowNum) throws SQLException {
        ClickStat retVal = new ClickStat();
        retVal.setAgent(rs.getString("item"));
        retVal.setClicks(rs.getInt("clicks"));
        return retVal;
    }
}