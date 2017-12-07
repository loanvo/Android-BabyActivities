package cs175.babysactivities;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Created by Loan Vo on 12/5/17.
 */

public class ActivityLog {
    private int id;
    private String name;
    private String log;
    private String date;
    private String time;

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public void setLogDate(String date) {
        this.date = date;
    }

    public void setTime(String date) {
        this.date = date;
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

    public String getLogDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String formatTimeView(long millis){
        String timeView = "";
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        int hours = minutes/60;
        int day = hours/24;
        seconds = seconds % 60;
        minutes = minutes % 60;
        hours = hours % 24;
        if(day < 1){
            if(hours < 1){
                if(minutes < 1){
                    timeView = String.format("%02d", seconds) + " sec";
                }else{
                    timeView = String.format("%02d", minutes) + " min "
                            + String.format("%02d", seconds) + " sec";
                }
            }else{
                timeView = String.format("%02d", hours) + " hr "
                        + String.format("%02d", minutes) + " min "
                        + String.format("%02d", seconds) + " sec";
            }
        }else {
            timeView = "" + String.format("%02d", day) + " day "
                    + String.format("%02d", hours) + " hr "
                    + String.format("%02d", minutes) + " min "
                    + String.format("%02d", seconds) + " sec";
        }
        return timeView;
    }

    public String getCurrentTime(){
        String current = "";
        DateTime dateTime = new DateTime();
        current = dateTime.toString(DateTimeFormat.shortDateTime());
        return current;
    }
    public String splitDate(String current){
        String date ="";
        String [] parts = current.split(" ");
        date = parts[0];
        return date;
    }

    public String splitTime(String current){
        String time="";
        String [] parts = current.split(" ");
        time = parts[1] + " " + parts[2];
        return time;
    }
}
