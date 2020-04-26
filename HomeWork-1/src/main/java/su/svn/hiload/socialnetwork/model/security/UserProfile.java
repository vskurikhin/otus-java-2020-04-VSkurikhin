package su.svn.hiload.socialnetwork.model.security;

import su.svn.hiload.socialnetwork.model.DBEntry;

import java.io.Serializable;
import java.util.Objects;

public class UserProfile implements Serializable, DBEntry {
    static final long serialVersionUID = -1L;

    private long id;

    private String login;

    private String hash;

    private boolean expired;

    private boolean locked;

    public UserProfile(long id, String login, String hash, boolean expired, boolean locked) {
        this.id = id;
        this.login = login;
        this.hash = hash;
        this.expired = expired;
        this.locked = locked;
    }

    public UserProfile() {
    }

    public long getId() {
        return this.id;
    }

    public String getLogin() {
        return this.login;
    }

    public String getHash() {
        return this.hash;
    }

    public boolean isExpired() {
        return this.expired;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return id == that.id &&
                expired == that.expired &&
                locked == that.locked &&
                Objects.equals(login, that.login) &&
                Objects.equals(hash, that.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, hash, expired, locked);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof UserProfile;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", expired=" + expired +
                ", locked=" + locked +
                '}';
    }
}
