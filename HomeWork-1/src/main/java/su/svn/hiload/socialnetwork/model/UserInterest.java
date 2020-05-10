package su.svn.hiload.socialnetwork.model;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Objects;

public class UserInterest  implements Serializable, DBEntry {
    static final long serialVersionUID = -3L;

    @Id
    private Long id;

    private long userInfoId;

    private String interest;

    public UserInterest() {
    }

    public UserInterest(Long id, long userInfoId, String interest) {
        this.id = id;
        this.userInfoId = userInfoId;
        this.interest = interest;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserInfoId() {
        return userInfoId;
    }

    public void setUserInfoId(long userInfoId) {
        this.userInfoId = userInfoId;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInterest that = (UserInterest) o;
        return id.equals(that.id) &&
                userInfoId == that.userInfoId &&
                Objects.equals(interest, that.interest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userInfoId, interest);
    }

    @Override
    public String toString() {
        return "UserInterest{" +
                "id=" + id +
                ", userInfoId=" + userInfoId +
                ", interest='" + interest + '\'' +
                '}';
    }
}
