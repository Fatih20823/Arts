package com.example.artsproject.Model;

import java.io.Serializable;

public class Arts implements Serializable {

    private String name;
    private String painterName;
    private String year;
    private String downloadUrl;

    public Arts() {
    }

    public Arts(String name, String painterName, String year,String downloadUrl) {
        this.name = name;
        this.painterName = painterName;
        this.year = year;
        this.downloadUrl = downloadUrl;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPainterName() {
        return painterName;
    }

    public void setPainterName(String painterName) {
        this.painterName = painterName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDownloadUrl() { return downloadUrl;}

    public void setDownloadUrl(String downloadUrl) {this.downloadUrl = downloadUrl;}
}
