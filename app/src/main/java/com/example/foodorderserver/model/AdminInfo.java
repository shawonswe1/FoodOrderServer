package com.example.foodorderserver.model;

public class AdminInfo {
    String adName,adEmail,adPassword;

    public AdminInfo() {
    }

    public AdminInfo(String adName , String adEmail , String adPassword) {
        this.adName = adName;
        this.adEmail = adEmail;
        this.adPassword = adPassword;
    }

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public String getAdEmail() {
        return adEmail;
    }

    public void setAdEmail(String adEmail) {
        this.adEmail = adEmail;
    }

    public String getAdPassword() {
        return adPassword;
    }

    public void setAdPassword(String adPassword) {
        this.adPassword = adPassword;
    }
}
