package su.svn.hiload.socialnetwork.dao.jdbc;

import su.svn.hiload.socialnetwork.model.UserInfo;

import java.util.Optional;

public interface UserInfoDao {

    int create(UserInfo userProfile);

    Optional<UserInfo> readById(long id);
}
