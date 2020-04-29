package su.svn.hiload.socialnetwork.dao.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import su.svn.hiload.socialnetwork.dao.jdbc.mappers.UserInfoRowMapper;
import su.svn.hiload.socialnetwork.model.UserInfo;

import java.util.Optional;

@Service("userInfoJdbcDao")
public class UserInfoJdbcDao implements UserInfoDao {

    private final static String READ_BY_ID = "SELECT id, first_name, sur_name, age, sex, city FROM user_info WHERE id = :id";

    private final static String CREATE = "INSERT INTO user_info ( id, first_name, sur_name, age, sex, city)" +
            " VALUES (:id, :first_name, :sur_name, :age, :sex, :city)";

    private final NamedParameterJdbcTemplate jdbcTemplate;


    public UserInfoJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public int create(UserInfo userInfo) {
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(userInfo);
        return jdbcTemplate.update(CREATE, namedParameters);
    }

    @Override
    public Optional<UserInfo> readById(long id) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(userInfo);
        userInfo = jdbcTemplate.queryForObject(READ_BY_ID, namedParameters, new UserInfoRowMapper());

        return userInfo != null ? Optional.of(userInfo) : Optional.empty();
    }
}
