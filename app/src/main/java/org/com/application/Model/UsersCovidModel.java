package org.com.application.Model;

public class UsersCovidModel {
    int id;
    String gejala, created_at;

    public UsersCovidModel(String gejala, String created_at) {
        this.gejala = gejala;
        this.created_at = created_at;
    }

    public UsersCovidModel(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int no) {
        this.id = no;
    }

    public String getGejala() {
        return gejala;
    }

    public void setGejala(String gejala) {
        this.gejala = gejala;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

}
