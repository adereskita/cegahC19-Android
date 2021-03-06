package org.com.application.Model;

public class UserModel {
    String id, name,email,password,nik,access_token;

    public UserModel(String name, String email, String access_token) {
        this.name = name;
        this.email = email;
        this.access_token = access_token;
    }

    public UserModel(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserModel(String name, String email, String password, String nik) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.nik = nik;
    }

    public UserModel(String id,String name, String email, String password, String nik) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.nik = nik;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
