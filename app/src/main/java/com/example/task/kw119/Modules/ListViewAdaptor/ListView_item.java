package com.example.task.kw119.Modules.ListViewAdaptor;

public class ListView_item {
    private int topicId;
    private String title;
    private String author;
    private String date;
    private String kind;
    private String location;
    private String imgPath;

    public ListView_item(int topicId, String title, String author, String date, String kind, String location, String imgPath) {
        this.topicId = topicId;
        this.title = title;
        this.author = author;
        this.date = date;
        this.kind = kind;
        this.location = location;
        this.imgPath = imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImgPath() {
        return imgPath;
    }

    public int getTopicId() {
        return topicId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getKind() {
        return kind;
    }

    public String getLocation() {
        return location;
    }
}
