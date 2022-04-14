package com.example.nasa_image_search.models;

public class ApiResponse {
    private String title;
    private byte[] image;
    private String date;
    private String hdUrl;

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
        return "ApiResponse{" +
                "title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", date='" + date + '\'' +
                ", hdUrl='" + hdUrl + '\'' +
                '}';
    }
}
