package com.example.searchmovieapp.Domain;


import java.io.Serializable;

public class Film implements Serializable {


    private String imgLink;

    private String title;

    private Integer year;

    private String director;

    private String genre;

    private String country;

    private String plot;

    private String IMDb;

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getIMDb() {
        return IMDb;
    }

    public void setIMDb(String IMDb) {
        this.IMDb = IMDb;
    }

}