package su.svn.hiload.socialnetwork.view;

public class Interest {

    private long id;

    private String interest;

    public Interest() { }

    public Interest(long id, String interest) {
        this.id = id;
        this.interest = interest;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }
}
