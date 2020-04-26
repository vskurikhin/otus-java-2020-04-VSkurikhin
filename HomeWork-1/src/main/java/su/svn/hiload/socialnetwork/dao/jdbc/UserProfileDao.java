package su.svn.hiload.socialnetwork.dao.jdbc;

import su.svn.hiload.socialnetwork.model.security.UserProfile;

import java.util.Optional;

public interface UserProfileDao {
    Optional<UserProfile> findByLogin(String login);
}
