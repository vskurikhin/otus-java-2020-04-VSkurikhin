package su.svn.hiload.socialnetwork.dao.jdbc.mappers;

import org.springframework.jdbc.core.RowMapper;
import su.svn.hiload.socialnetwork.model.UserInterest;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserInterestRowMapper implements RowMapper<UserInterest> {
    @Override
    public UserInterest mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            UserInterest userInterest = new UserInterest();
            userInterest.setId(rs.getLong("id"));
            userInterest.setUserInfoId(rs.getLong("user_info_id"));
            userInterest.setInterest(rs.getString("interest"));

            return userInterest;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
