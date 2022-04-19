package com.example.cs4084;

public class Video {
    private String url; //Preferably embed links so that itll take up the entire web view area in layout
    private String title;
    private String description;

    public Video(String url, String title,String description) {
        this.url = url;
        this.title = title;
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
