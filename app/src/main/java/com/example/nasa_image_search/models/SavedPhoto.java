package com.example.nasa_image_search.models;

public class SavedPhoto {
    private long id;
    private String url;
    private String date;
    private String hdUrl;

    public SavedPhoto(long id, String url, String date, String hdUrl) {
        this.id = id;
        this.url = url;
        this.date = date;
        this.hdUrl = hdUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
                ", url='" + url + '\'' +
                ", date='" + date + '\'' +
                ", hdUrl='" + hdUrl + '\'' +
                '}';
    }
}
