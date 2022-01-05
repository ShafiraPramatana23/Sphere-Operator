package com.example.sphere.ui.profile.model;

public class MyReportList {
    String id;
    String userId;
    String title;
    String desc;
    String category;
    String address;
    String image;
    String date;
    String latitude;
    String longitude;
    Boolean isExpandable;

    public Boolean getExpandable() {
        return isExpandable;
    }

    public void setExpandable(Boolean expandable) {
        isExpandable = expandable;
    }

    public MyReportList(String id, String userId, String title, String desc, String category, String address, String image, String date, String latitude, String longitude, Boolean isExpandable) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.desc = desc;
        this.category = category;
        this.address = address;
        this.image = image;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isExpandable = isExpandable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
