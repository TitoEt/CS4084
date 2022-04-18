package com.example.cs4084;

public class Video {
    private String url;
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

//    /**
//     * @return Youtube Frame to be attached into a Webview.
//     */
//    public String getFrame(){
//        return "<iframe width=\"100%\" height=\"100%\" src=\""+ url +"\" frameborder=\"0\" allow=\"accelerometer; encrypted-media;\" allowfullscreen></iframe>";
//    }
}
