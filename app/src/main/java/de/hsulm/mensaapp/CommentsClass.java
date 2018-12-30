package de.hsulm.mensaapp;

public class CommentsClass {

    private int user_id;
    private int food_id;
    private int comment_id;
    private String comments;
    private String cdate;
    private String location;
    private String username;
    //private String stars;

    public CommentsClass(int user_id, int food_id, int comment_id, String comments, String cdate, String location, String username) //String stars hinzufügen
    {
        this.user_id = user_id;
        this.food_id = food_id;
        this.comment_id = comment_id;
        this.comments = comments;
        this.cdate = cdate;
        this.location = location;
        this.username = username;
        //this.stars = stars;



    }

    public String getUser() {
        return username;
    }

    //public String getStars() {return stars; }

    public String getLocation() {
        return location;
    }

    public String getCDate() {
        return cdate;
    }

    public String getComment() {
        return comments;
    }

    public int getUser_id() {  return user_id;  }

    public int getFood_id() {  return food_id;  }

    public int getComment_id() { return comment_id;   }
}
