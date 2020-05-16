package su.svn.hiload.socialnetwork.view;

import java.util.ArrayList;
import java.util.List;

public class ApplicationForm {
    private String username;

    private String firstName;

    private String surName;

    private int age;

    private String sex;

    private String city;

    private List<Interest> interests;

    public ApplicationForm() { }

    public ApplicationForm(
            String username,
            String firstName,
            String surName,
            int age,
            String sex,
            String city,
            List<Interest> interests) {
        this.username = username;
        this.firstName = firstName;
        this.surName = surName;
        this.age = age;
        this.sex = sex;
        this.city = city;
        this.interests = interests;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
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

    public List<Interest> getInterests() {
        return interests;
    }

    public void setInterests(List<Interest> interests) {
        this.interests = interests;
    }

    @Override
    public String toString() {
        return "ApplicationForm{" +
                "username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", surName='" + surName + '\'' +
                ", age=" + age +
                ", sex='" + sex + '\'' +
                ", city='" + city + '\'' +
                ", interests=" + interests +
                '}';
    }
}
