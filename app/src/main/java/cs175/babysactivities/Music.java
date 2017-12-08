package cs175.babysactivities;

/**
 * Created by Loan Vo on 12/7/17.
 */

public class Music {
    private String name;
    private int song;

    public Music(String name, int song){
        this.name = name;
        this.song = song;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSong(int song) {
        this.song = song;
    }

    public int getSong() {
        return song;
    }
}
