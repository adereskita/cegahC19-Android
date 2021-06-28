package org.com.application.Model;

public class GejalaModel {
    int id;
    String gejala, created_at;

    public GejalaModel(int no, String gejala, String date) {
        this.id = no;
        this.gejala = gejala;
        this.created_at = date;
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
