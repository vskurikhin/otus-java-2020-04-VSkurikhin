package su.svn.hiload.socialnetwork.model;

import java.io.Serializable;
import java.util.Objects;

public class UserInfoSignFriend implements Serializable, DBEntry {
    static final long serialVersionUID = -4L;

    private Long id;

    private String firstName;

    private String surName;

    private Integer age;

    private String sex;

    private String city;

    private Boolean friend;

    public UserInfoSignFriend() {
    }

    public UserInfoSignFriend(
            long id,
            String firstName,
            String surName,
            Integer age,
            String sex,
            String city,
            Boolean friend) {
        this.id = id;
        this.firstName = firstName;
        this.surName = surName;
        this.age = age;
        this.sex = sex;
        this.city = city;
        this.friend = friend;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Boolean getFriend() {
        return friend;
    }

    public void setFriend(Boolean friend) {
        this.friend = friend;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserInfoSignFriend)) return false;
        UserInfoSignFriend that = (UserInfoSignFriend) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(surName, that.surName) &&
                Objects.equals(age, that.age) &&
                Objects.equals(sex, that.sex) &&
                Objects.equals(city, that.city) &&
                Objects.equals(friend, that.friend);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, surName, age, sex, city, friend);
    }

    @Override
    public String toString() {
        return "UserInfoSignFriend{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", surName='" + surName + '\'' +
                ", age=" + age +
                ", sex='" + sex + '\'' +
                ", city='" + city + '\'' +
                ", friend=" + friend +
                '}';
    }
}
