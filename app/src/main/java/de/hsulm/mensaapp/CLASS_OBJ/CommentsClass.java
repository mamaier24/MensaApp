package de.hsulm.mensaapp.CLASS_OBJ;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Stephan Danz 05/12/2018
 * Class necessary for creating comments and parcing it back to the mainactivity
 */
public class CommentsClass implements Parcelable {

    private int user_id;
    private int food_id;
    private int comment_id;
    private String comments;
    private String date;
    private String location;
    private String username;
    private String user_rating;

    public CommentsClass(int user_id, int food_id, int comment_id, String comments, String cdate, String location, String username, String user_rating) //String stars hinzufügen
    {
        this.user_id = user_id;
        this.food_id = food_id;
        this.comment_id = comment_id;
        this.comments = comments;
        this.date = cdate;
        this.location = location;
        this.username = username;
        this.user_rating = user_rating;

    }


    protected CommentsClass(Parcel in) {
        user_id = in.readInt();
        food_id = in.readInt();
        comment_id = in.readInt();
        comments = in.readString();
        date = in.readString();
        location = in.readString();
        username = in.readString();
        user_rating = in.readString();
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

    public String getUserRating() { return user_rating; }

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
        dest.writeString(user_rating);
    }
}
