package su.svn.hiload.socialnetwork.dao.jdbc;

import su.svn.hiload.socialnetwork.model.security.UserProfile;

import java.util.Optional;

public interface UserProfileDao {

    int create(UserProfile userProfile);

    Optional<UserProfile> readLogin(String login);
}
