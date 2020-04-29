package su.svn.hiload.socialnetwork.model;

import java.io.Serializable;
import java.util.Objects;

public class UserInfo implements Serializable, DBEntry {
    static final long serialVersionUID = -2L;

    private long id;

    private String firstName;

    private String surName;

    private Integer age;

    private String sex;

    private String city;

    public UserInfo() {
    }

    public UserInfo(long id, String firstName, String surName, Integer age, String sex, String city) {
        this.id = id;
        this.firstName = firstName;
        this.surName = surName;
        this.age = age;
        this.sex = sex;
        this.city = city;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurName() {
        return this.surName;
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
        this.sex = sex.substring(0, 1);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo userInfo = (UserInfo) o;
        return id == userInfo.id &&
                Objects.equals(firstName, userInfo.firstName) &&
                Objects.equals(surName, userInfo.surName) &&
                Objects.equals(age, userInfo.age) &&
                Objects.equals(sex, userInfo.sex) &&
                Objects.equals(city, userInfo.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, surName, age, sex, city);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof UserInfo;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", surName='" + surName + '\'' +
                ", age=" + age +
                ", sex='" + sex + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
