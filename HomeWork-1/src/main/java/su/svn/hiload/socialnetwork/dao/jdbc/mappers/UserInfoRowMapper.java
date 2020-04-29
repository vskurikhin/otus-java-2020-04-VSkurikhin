package su.svn.hiload.socialnetwork.dao.jdbc.mappers;

import org.springframework.jdbc.core.RowMapper;
import su.svn.hiload.socialnetwork.model.UserInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserInfoRowMapper implements RowMapper<UserInfo> {
    @Override
    public UserInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            UserInfo userProfile = new UserInfo();
            userProfile.setId(rs.getLong("id"));
            userProfile.setFirstName(rs.getString("first_name"));
            userProfile.setSurName(rs.getString("sur_name"));
            userProfile.setAge(rs.getInt("age"));
            userProfile.setSex(rs.getString("sex"));
            userProfile.setCity(rs.getString("city"));

            return userProfile;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
