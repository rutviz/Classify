package com.classify.classify;


public class Classify_path {

    String path;
    String category;
    String date;

    public Classify_path( ) {

    }

    public Classify_path(String path) {
        this.path = path;
        this.category = "";
        this.date = "";
    }

    public Classify_path(String path, String category, String date) {
        this.path = path;
        this.category = category;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {

        return getPath()+"  "+getCategory()+" "+getDate();
    }
}
