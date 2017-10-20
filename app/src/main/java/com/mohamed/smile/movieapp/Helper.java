package com.mohamed.smile.movieapp;

public class Helper {

    private String MovieId;
    private String MovieTitle;
    private String MoviePoster;
    private String MovieAverage;
    private String MovieType;
    private String MovieDate;
    private String MovieOverView;
    private String BackDropBath;



    public String getId() {
        return MovieId;
    }

    public void setId(String id) {
        this.MovieId = id;
    }

    public String getTitle() {
        return MovieTitle;
    }

    public void setTitle(String originalTitle) {
        this.MovieTitle = originalTitle;
    }

    public String getPoster() {
        return MoviePoster;
    }

    public void setPoster(String posterUrl) {
        this.MoviePoster = posterUrl;
    }

    public String getVoteAverage() {
        return MovieAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.MovieAverage = voteAverage;
    }

    public String getAdult() {
        return MovieType;
    }

    public void setAdult(String isAdult) {
        this.MovieType = isAdult;
    }

    public String getDate() {
        return MovieDate;
    }

    public void setDate(String relaseDate) {
        this.MovieDate = relaseDate;
    }

    public String getOverview() {
        return MovieOverView;
    }

    public void setOverview(String textAboutFilm) {
        this.MovieOverView = textAboutFilm;
    }

    public String getBackDropBath() {return BackDropBath;}

    public void setBackDropBath(String backDropBath) {BackDropBath = backDropBath;}

}