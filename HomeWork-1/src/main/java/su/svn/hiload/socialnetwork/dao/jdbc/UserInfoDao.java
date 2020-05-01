package su.svn.hiload.socialnetwork.dao.jdbc;

import su.svn.hiload.socialnetwork.model.UserInfo;

import java.util.Optional;

public interface UserInfoDao {

    int create(UserInfo userInfo);

    Optional<UserInfo> readById(long id);

    boolean existsById(long id);

    int update(UserInfo userInfo);
}
