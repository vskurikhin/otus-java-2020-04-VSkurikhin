package su.svn.hiload.socialnetwork.dao.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import su.svn.hiload.socialnetwork.dao.jdbc.mappers.UserProfileRowMapper;
import su.svn.hiload.socialnetwork.model.security.UserProfile;

import java.util.Optional;

@Service("userProfileJdbcDao")
public class UserProfileJdbcDao implements UserProfileDao {

    private final static String READ_LOGIN = "SELECT id, login, hash, expired, locked FROM user_profile WHERE login = :login";

    private final static String CREATE = "INSERT INTO user_profile (login, hash, expired, locked) VALUES (:login, :hash, :expired, :locked)";

    private final NamedParameterJdbcTemplate jdbcTemplate;


    public UserProfileJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public int create(UserProfile userProfile) {
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(userProfile);
        return jdbcTemplate.update(CREATE, namedParameters);
    }

    @Override
    public Optional<UserProfile> readLogin(String login) {
        UserProfile userProfile = new UserProfile();
        userProfile.setLogin(login);
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(userProfile);
        UserProfile result = jdbcTemplate.queryForObject(READ_LOGIN, namedParameters, new UserProfileRowMapper());

        return result != null ? Optional.of(result) : Optional.empty();
    }
}
