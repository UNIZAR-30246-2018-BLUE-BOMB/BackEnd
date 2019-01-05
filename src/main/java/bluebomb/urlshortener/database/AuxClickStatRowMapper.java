package bluebomb.urlshortener.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class AuxClickStatRowMapper implements RowMapper<AuxClickStat> {
    @Override
    public AuxClickStat mapRow(ResultSet rs, int rowNum) throws SQLException {
        AuxClickStat retVal = new AuxClickStat();
        retVal.setDate(rs.getDate("date"));
        retVal.setAgent(rs.getString("item"));
        retVal.setClicks(rs.getInt("clicks"));
        retVal.setSum(rs.getInt("sum"));
        return retVal;
    }
}