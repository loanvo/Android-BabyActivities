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

    public int getQuanity() {
        return quanity;
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
}