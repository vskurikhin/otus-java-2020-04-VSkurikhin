package su.svn.hiload.socialnetwork.dao.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(UserInfoJdbcDao.class);

    private final static String CHECK_ID = "SELECT id FROM user_info WHERE id = :id";

    private final static String READ_BY_ID = "SELECT id, first_name, sur_name, age, sex, city FROM user_info WHERE id = :id";

    private final static String CREATE = "INSERT INTO user_info ( id, first_name, sur_name, age, sex, city)" +
            " VALUES (:id, :firstName, :surName, :age, :sex, :city)";

    private final static String UPDATE = "UPDATE user_info SET first_name = :firstName, sur_name = :surName, age = :age," +
            " sex = :sex, city = :city WHERE id = :id";

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
        try {
            userInfo = jdbcTemplate.queryForObject(READ_BY_ID, namedParameters, new UserInfoRowMapper());
            return userInfo != null ? Optional.of(userInfo) : Optional.empty();
        } catch (Exception e) {
            LOG.error("readById ", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(long id) {
        UserInfo userProfile = new UserInfo();
        userProfile.setId(id);
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(userProfile);
        try {
            Long result = jdbcTemplate.queryForObject(CHECK_ID, namedParameters, Long.class);
            return result != null;
        } catch (Exception e) {
            LOG.error("existsById ", e);
        }
        return false;
    }

    @Override
    public int update(UserInfo userInfo) {
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(userInfo);
        try {
            return jdbcTemplate.update(UPDATE, namedParameters);
        } catch (Exception e) {
            LOG.error("update ", e);
        }
        return 0;
    }
}
