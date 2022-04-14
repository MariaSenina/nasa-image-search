package com.example.nasa_image_search.enums;

public enum ApiSetting {
    BASE_URL("https://api.nasa.gov/planetary/apod?"),
    API_KEY("api_key=VXuoBhcCmhio6gHo2aXog0Np7fElERQ0LpTXVXBM");

    ApiSetting(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }
}
