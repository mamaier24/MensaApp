package de.hsulm.mensaapp;

import android.os.Parcel;
import android.os.Parcelable;

public class CommentsClass implements Parcelable {

    private int user_id;
    private int food_id;
    private int comment_id;
    private String comments;
    private String date;
    private String location;
    private String username;
    //private String stars;

    public CommentsClass(int user_id, int food_id, int comment_id, String comments, String cdate, String location, String username) //String stars hinzufügen
    {
        this.user_id = user_id;
        this.food_id = food_id;
        this.comment_id = comment_id;
        this.comments = comments;
        this.date = cdate;
        this.location = location;
        this.username = username;
        //this.stars = stars;

    }


    protected CommentsClass(Parcel in) {
        user_id = in.readInt();
        food_id = in.readInt();
        comment_id = in.readInt();
        comments = in.readString();
        date = in.readString();
        location = in.readString();
        username = in.readString();
    }


    public static final Creator<CommentsClass> CREATOR = new Creator<CommentsClass>() {
        @Override
        public CommentsClass createFromParcel(Parcel in) {
            return new CommentsClass(in);
        }

        @Override
        public CommentsClass[] newArray(int size) {
            return new CommentsClass[size];
        }
    };

    public String getUser() {
        return username;
    }

    //public String getStars() {
    // return stars;
    //}

    public String getLocation() {
        return location;
    }

    public String getCDate() {
        return date;
    }

    public String getComment() {
        return comments;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getFood_id() {
        return food_id;
    }

    public int getComment_id() {
        return comment_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(user_id);
        dest.writeInt(food_id);
        dest.writeInt(comment_id);
        dest.writeString(comments);
        dest.writeString(date);
        dest.writeString(location);
        dest.writeString(username);
    }
}
