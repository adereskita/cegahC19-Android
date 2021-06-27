package org.com.application.Model;

public class StepModel {
    String  date;
    int id_user, step;

    public StepModel(int id_user, String date, int step) {
        this.id_user = id_user;
        this.date = date;
        this.step = step;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
