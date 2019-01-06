package bluebomb.urlshortener.database.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import bluebomb.urlshortener.database.model.ClickStatDB;
import org.springframework.jdbc.core.RowMapper;

public class ClickStatDBRowMapper implements RowMapper<ClickStatDB> {
    @Override
    public ClickStatDB mapRow(ResultSet rs, int rowNum) throws SQLException {
        ClickStatDB retVal = new ClickStatDB();
        retVal.setDate(rs.getDate("date"));
        retVal.setAgent(rs.getString("item"));
        retVal.setClicks(rs.getInt("clicks"));
        retVal.setSum(rs.getInt("sum"));
        return retVal;
    }
}