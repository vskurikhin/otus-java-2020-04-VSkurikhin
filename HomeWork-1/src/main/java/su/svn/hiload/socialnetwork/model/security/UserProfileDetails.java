package su.svn.hiload.socialnetwork.model.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserProfileDetails implements UserDetails {

    private final UserProfile userProfile;

    public UserProfileDetails(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
        return Collections.singletonList(grantedAuthority);
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
