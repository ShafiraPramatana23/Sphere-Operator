package com.example.sphere.ui.home.model;

public class Daily {
    String hour, temp, idIcon;

    public Daily(String hour, String temp, String idIcon) {
        this.hour = hour;
        this.temp = temp;
        this.idIcon = idIcon;
    }

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
