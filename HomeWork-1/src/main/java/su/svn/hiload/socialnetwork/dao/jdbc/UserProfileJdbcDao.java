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

    private final static String SELECT = "SELECT id, login, hash, expired, locked FROM user_profile WHERE login = :login";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UserProfileJdbcDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public Optional<UserProfile> findByLogin(String login) {
        UserProfile userProfile = new UserProfile();
        userProfile.setLogin(login);
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(userProfile);
        UserProfile result = jdbcTemplate.queryForObject(SELECT, namedParameters, new UserProfileRowMapper());

        return result != null ? Optional.of(result) : Optional.empty();
    }
}
