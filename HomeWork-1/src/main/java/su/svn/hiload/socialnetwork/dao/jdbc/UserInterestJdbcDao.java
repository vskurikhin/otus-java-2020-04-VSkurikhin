package su.svn.hiload.socialnetwork.dao.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import su.svn.hiload.socialnetwork.dao.jdbc.mappers.UserInterestBatchPreparedStatementSetter;
import su.svn.hiload.socialnetwork.dao.jdbc.mappers.UserInterestRowMapper;
import su.svn.hiload.socialnetwork.model.UserInterest;

import java.util.*;

@Service("userInterestJdbcDao")
public class UserInterestJdbcDao implements UserInterestDao {

    private static final Logger LOG = LoggerFactory.getLogger(UserInterestJdbcDao.class);

    private final static String CREATE = "INSERT INTO user_interest (id, user_info_id, interest)" +
            " VALUES (:id, :userInfoId, :interest)";

    private final static String BATCH_INSERT = "INSERT INTO user_interest (user_info_id, interest) VALUES (?, ?)";

    private final static String READ_BY_ID = "SELECT id, user_info_id, interest FROM user_interest WHERE id = :id";

    private final static String READ_ID_BY_USER_INFO_ID_INTEREST = "SELECT id FROM user_interest" +
            " WHERE user_info_id = :userInfoId AND interest = :interest";

    private final static String READ_BY_USER_INFO_ID = "SELECT id, user_info_id, interest FROM user_interest" +
            " WHERE user_info_id = :userInfoId";

    private final static String UPDATE = "UPDATE user_interest SET user_info_id = :userInfoId, interest = :interest" +
            " WHERE id = :id";

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public UserInterestJdbcDao(JdbcTemplate jdbcTemplate, JdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(namedParameterJdbcTemplate);
    }

    @Override
    public int create(UserInterest userInterest) {
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(userInterest);
        return namedParameterJdbcTemplate.update(CREATE, namedParameters);
    }

    public void createBatch(final List<UserInterest> interests) {
        final int batchSize = 10;
        for (int j = 0; j < interests.size(); j += batchSize) {
            final List<UserInterest> batchList = interests.subList(j, Math.min(j + batchSize, interests.size()));
            jdbcTemplate.batchUpdate(BATCH_INSERT, new UserInterestBatchPreparedStatementSetter(batchList));
        }
    }

    @Override
    public Optional<UserInterest> readById(long id) {
        UserInterest userInterest = new UserInterest();
        userInterest.setId(id);
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(userInterest);
        try {
            userInterest = namedParameterJdbcTemplate.queryForObject(READ_BY_USER_INFO_ID, namedParameters, new UserInterestRowMapper());
            return userInterest != null ? Optional.of(userInterest) : Optional.empty();
        } catch (Exception e) {
            LOG.error("readById ", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Long> readIdByUserInfoIdAndInterest(long userInfoId, String interest) {
        UserInterest userProfile = new UserInterest();
        userProfile.setUserInfoId(userInfoId);
        userProfile.setInterest(interest);
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(userProfile);
        try {
            Long result = namedParameterJdbcTemplate.queryForObject(READ_ID_BY_USER_INFO_ID_INTEREST, namedParameters, Long.class);
            return result != null ? Optional.of(result) : Optional.empty();
        } catch (Exception e) {
            LOG.error("existsById ", e);
        }
        return Optional.empty();
    }

    @Override
    public List<UserInterest> readAllByUserInfoId(long userInfoId) {
        Map<String, Long> namedParameters = new HashMap<>() {{ put("userInfoId", userInfoId); }};
        try {
            return namedParameterJdbcTemplate.query(READ_BY_USER_INFO_ID, namedParameters, new UserInterestRowMapper());
        } catch (Exception e) {
            LOG.error("readAllByUserInfoId ", e);
        }
        return Collections.emptyList();
    }

    @Override
    public int update(UserInterest userInterest) {
        SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(userInterest);
        try {
            return namedParameterJdbcTemplate.update(UPDATE, namedParameters);
        } catch (Exception e) {
            LOG.error("update ", e);
        }
        return 0;
    }
}
