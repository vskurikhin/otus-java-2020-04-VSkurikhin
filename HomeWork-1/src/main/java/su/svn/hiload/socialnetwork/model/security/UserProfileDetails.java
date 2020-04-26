package su.svn.hiload.socialnetwork.model.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import su.svn.hiload.socialnetwork.model.security.UserProfile;

import java.util.Collection;
import java.util.Collections;

public class UserProfileDetails implements UserDetails {

    private final UserProfile userProfile;

    public UserProfileDetails(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList((GrantedAuthority) () -> "USERS");
    }

    @Override
    public String getPassword() {
        return userProfile.getHash();
    }

    @Override
    public String getUsername() {
        return  userProfile.getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return ! userProfile.isExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return ! userProfile.isLocked() ;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return ! userProfile.isExpired();
    }

    @Override
    public boolean isEnabled() {
        return ! userProfile.isExpired();
    }
}
