package cs175.babysactivities;

/**
 * Created by Loan Vo on 12/3/17.
 */

public class Supplies {
    private int diaper;
    private int formula;
    private String  date;

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDiaper(int diaper){
        this.diaper = diaper;
    }
    public void setFormula(int formula){
        this.formula = formula;
    }

    public int getDiaper() {
        return diaper;
    }

    public int getFormula() {
        return formula;
    }
}