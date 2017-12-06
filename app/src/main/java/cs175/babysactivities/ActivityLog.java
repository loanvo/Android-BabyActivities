package cs175.babysactivities;

/**
 * Created by Loan Vo on 12/5/17.
 */

public class ActivityLog {
    private int id;
    private String name;
    private String log;

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getLog() {
        return log;
    }
}
