package org.com.application.Model;

public class CovidModel {
    int id_user;
    String nama,umur, gender,nik,telepon ,provinsi, kota,alamat,gejala;

    public CovidModel(int id_user, String nama, String umur, String gender, String nik, String telepon, String provinsi, String kota, String alamat, String gejala) {
        this.id_user = id_user;
        this.nama = nama;
        this.umur = umur;
        this.gender = gender;
        this.nik = nik;
        this.telepon = telepon;
        this.provinsi = provinsi;
        this.kota = kota;
        this.alamat = alamat;
        this.gejala = gejala;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getUmur() {
        return umur;
    }

    public void setUmur(String umur) {
        this.umur = umur;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public void setProvinsi(String provinsi) {
        this.provinsi = provinsi;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getGejala() {
        return gejala;
    }

    public void setGejala(String gejala) {
        this.gejala = gejala;
    }
}
