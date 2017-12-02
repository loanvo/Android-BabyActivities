package cs175.babysactivities;

import java.sql.Date;

/**
 * Created by Loan Vo on 12/1/17.
 */

public class BabyProfile {
    int id;
    String name;
    String DOB;
    double height;
    double weight;
    double headSize;

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDOB(String DOB){
        this.DOB = DOB;
    }

    public void setHeight(double height){
        this.height = height;
    }

    public void setWeight(double weight){
         this.weight = weight;
    }

    public void setHeadsize(double headSize){
        this.headSize = headSize;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDOB() {
        return DOB;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeadSize() {
        return headSize;
    }
}
