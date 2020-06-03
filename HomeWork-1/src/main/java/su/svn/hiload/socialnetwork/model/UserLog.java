package su.svn.hiload.socialnetwork.model;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class UserLog implements Serializable, DBEntry {
    static final long serialVersionUID = -2L;

    @Id
    private Long id;

    private Long userProfileId;

    private LocalDateTime dateTime;

    public UserLog() {
    }

    public UserLog(Long id, Long userProfileId, LocalDateTime dateTime) {
        this.id = id;
        this.userProfileId = userProfileId;
        this.dateTime = dateTime;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(Long userProfileId) {
        this.userProfileId = userProfileId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserLog userLog = (UserLog) o;
        return Objects.equals(id, userLog.id) &&
                Objects.equals(userProfileId, userLog.userProfileId) &&
                Objects.equals(dateTime, userLog.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userProfileId, dateTime);
    }

    @Override
    public String toString() {
        return "UserLog{" +
                "id=" + id +
                ", userProfileId=" + userProfileId +
                ", dateTime=" + dateTime +
                '}';
    }
}
