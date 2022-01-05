package com.example.sphere.ui.home.model;

public class Hourly {
    String hour, temp, idIcon;

    public Hourly(String hour, String temp, String idIcon) {
        this.hour = hour;
        this.temp = temp;
        this.idIcon = idIcon;
    }

    public Hourly() {}

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getIdIcon() {
        return idIcon;
    }

    public void setIdIcon(String idIcon) {
        this.idIcon = idIcon;
    }
}
