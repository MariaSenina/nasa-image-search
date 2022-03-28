package com.example.nasa_image_search.models;

public class ApiResponse {
    private String url;
    private String date;
    private String hdUrl;

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
        return "ApiResponse{" +
                "url='" + url + '\'' +
                ", date='" + date + '\'' +
                ", hdUrl='" + hdUrl + '\'' +
                '}';
    }
}
