package com.example.sphere.ui.home.model;

public class Notif {
    String id;
    String date;
    String message;
    String status;

    public Notif(String id, String date, String message, String status, String reportId) {
        this.id = id;
        this.date = date;
        this.message = message;
        this.status = status;
        this.reportId = reportId;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    String reportId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
