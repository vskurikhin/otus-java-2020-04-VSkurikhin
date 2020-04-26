package su.svn.hiload.socialnetwork.dao.jdbc.mappers;

import org.springframework.jdbc.core.RowMapper;
import su.svn.hiload.socialnetwork.model.security.UserProfile;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserProfileRowMapper implements RowMapper<UserProfile> {
    @Override
    public UserProfile mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            UserProfile userProfile = new UserProfile();
            userProfile.setId(rs.getLong("id"));
            userProfile.setLogin(rs.getString("login"));
            userProfile.setHash(rs.getString("hash"));
            userProfile.setExpired(rs.getBoolean("expired"));
            userProfile.setLocked(rs.getBoolean("locked"));

            return userProfile;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
