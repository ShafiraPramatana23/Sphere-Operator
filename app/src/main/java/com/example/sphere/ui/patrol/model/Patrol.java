package com.example.sphere.ui.patrol.model;

public class Patrol {
    String id;
    String rivers_id;
    String userId;
    String status;
    String description;
    String latitude;
    String longitude;
    String photo;
    String task_date;
    String created_at;
    String name;
    Boolean isExpandable;

    public Patrol(String id, String rivers_id, String userId, String status, String description, String latitude, String longitude, String photo, String task_date, String created_at, String name, Boolean isExpandable) {
        this.id = id;
        this.rivers_id = rivers_id;
        this.userId = userId;
        this.status = status;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photo = photo;
        this.task_date = task_date;
        this.created_at = created_at;
        this.name = name;
        this.isExpandable = isExpandable;
    }

    public Boolean getExpandable() {
        return isExpandable;
    }

    public void setExpandable(Boolean expandable) {
        isExpandable = expandable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRivers_id() {
        return rivers_id;
    }

    public void setRivers_id(String rivers_id) {
        this.rivers_id = rivers_id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTask_date() {
        return task_date;
    }

    public void setTask_date(String task_date) {
        this.task_date = task_date;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
