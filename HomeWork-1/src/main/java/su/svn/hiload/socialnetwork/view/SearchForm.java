package su.svn.hiload.socialnetwork.view;

public class SearchForm {
    private String firstName;

    private String surName;

    public SearchForm() { }

    public SearchForm(
            String firstName,
            String surName) {
        this.firstName = firstName;
        this.surName = surName;
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

    @Override
    public String toString() {
        return "ApplicationForm{" +
                "firstName='" + firstName + '\'' +
                ", surName='" + surName + '\'' +
                '}';
    }
}
