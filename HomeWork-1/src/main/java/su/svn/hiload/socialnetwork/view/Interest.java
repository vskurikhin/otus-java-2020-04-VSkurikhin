package su.svn.hiload.socialnetwork.view;

public class Interest {

    private Long id;

    private String interest;

    public Interest() { }

    public Interest(long id, String interest) {
        this.id = id;
        this.interest = interest;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    @Override
    public String toString() {
        return "Interest{" +
                "id=" + id +
                ", interest='" + interest + '\'' +
                '}';
    }
}
