package su.svn.hiload.socialnetwork.model.security;

import org.springframework.data.annotation.Id;
import su.svn.hiload.socialnetwork.model.DBEntry;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class UserProfile implements Serializable, DBEntry {

    static final long serialVersionUID = -1L;

    @Id
    private Long id;

    private String login;

    private String hash;

    private boolean expired;

    private boolean locked;

    private UUID label;

    public UserProfile(Long id, String login, String hash, boolean expired, boolean locked, UUID label) {
        this.id = id;
        this.login = login;
        this.hash = hash;
        this.expired = expired;
        this.locked = locked;
        this.label = label;
    }

    public UserProfile() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getHash() {
        return this.hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public boolean isExpired() {
        return this.expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public UUID getLabel() {
        return label;
    }

    public void setLabel(UUID label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return expired == that.expired &&
                locked == that.locked &&
                Objects.equals(id, that.id) &&
                Objects.equals(login, that.login) &&
                Objects.equals(hash, that.hash) &&
                Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, hash, expired, locked, label);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof UserProfile;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", hash='" + hash + '\'' +
                ", expired=" + expired +
                ", locked=" + locked +
                ", label=" + label +
                '}';
    }
}
