package su.svn.hiload.socialnetwork.dao.jdbc;

import su.svn.hiload.socialnetwork.model.UserInterest;

import java.util.List;
import java.util.Optional;

public interface UserInterestDao {

    int create(UserInterest userInfo);

    void createBatch(final List<UserInterest> interests);

    Optional<UserInterest> readById(long id);

    Optional<Long> readIdByUserInfoIdAndInterest(long userInfoId, String interest);

    List<UserInterest> readAllByUserInfoId(long userInfoId);

    int update(UserInterest userInterest);
}
