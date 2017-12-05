package cs175.babysactivities;

/**
 * Created by Loan Vo on 12/3/17.
 */

public class ActivityData {
    private long bottleTime;
    private long leftTime;
    private long rightTime;
    private long sleepTime;
    private long walkTime;
    private int quanity;
    private String startType;
    private String stopType;
    private String start;
    private String stop;

    public void setBottleTime(long bottleTime){
        this.bottleTime = bottleTime;
    }
    public void setLeftTime(long leftTime){
        this.leftTime = leftTime;
    }
    public void setRightTime(long rightTime){
        this.rightTime = rightTime;
    }
    public void setSleepTime(long sleepTime){
        this.sleepTime = sleepTime;
    }
    public void setWalkTime(long walkTime){
        this.walkTime = walkTime;
    }
    public void setQuanity(int quantity){
        this.quanity = quantity;
    }
    public void setStartType(String startType){
        this.startType = startType;
    }
    public void setStopType(String stopType){
        this.stopType = stopType;
    }
    public int getQuanity() {
        return quanity;
    }
    public void setStart(String start){
        this.start = start;
    }
    public void setStop(String stop){
        this.stop = stop;
    }

    public long getBottleTime() {
        return bottleTime;
    }

    public long getLeftTime() {
        return leftTime;
    }

    public long getRightTime() {
        return rightTime;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public long getWalkTime() {
        return walkTime;
    }

    public String getStartType() {
        return startType;
    }

    public String getStopType() {
        return stopType;
    }

    public String getStart() {
        return start;
    }

    public String getStop() {
        return stop;
    }
}