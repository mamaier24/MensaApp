package de.hsulm.mensaapp;

public class CommentsClass {

    private String user;
    private String stars;
    private String location;
    private String date;
    private String comment;

    public CommentsClass(String user, String stars, String location, String date, String comment) {
        this.user = user;
        this.stars = stars;
        this.location = location;
        this.date = date;
        this.comment = comment;
    }

    public String getUser() {
        return user;
    }

    public String getStars() {
        return stars;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }
}
