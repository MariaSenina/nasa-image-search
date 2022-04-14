package com.example.nasa_image_search.models;

import java.io.Serializable;

public class Photo implements Serializable {
    private long id;
    private String title;
    private byte[] image;
    private String date;
    private String hdUrl;

    public Photo(long id, byte[] image, String date, String hdUrl, String title) {
        this.id = id;
        this.image = image;
        this.date = date;
        this.hdUrl = hdUrl;
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHdUrl() {
        return hdUrl;
    }

    public void setHdUrl(String hdUrl) {
        this.hdUrl = hdUrl;
    }

    @Override
    public String toString() {
        return "SavedPhoto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", date='" + date + '\'' +
                ", hdUrl='" + hdUrl + '\'' +
                '}';
    }
}
