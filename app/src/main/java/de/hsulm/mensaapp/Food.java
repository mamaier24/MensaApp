package de.hsulm.mensaapp;

import android.os.Parcel;
import android.os.Parcelable;

public class Food implements Parcelable {

    private int id;
    private String name;
    private String category;
    private String date;
    private int vegan;
    private int vegetarian;
    private float price;
    private String uuid;
    private float rating;
    private int mimgId;

    public Food(int id, String name, String category, int vegan, int vegetarian, float price, String uuid, float rating, int mimgId){
        this.id = id;
        this.name = name;
        this.category = category;
        this.date = date;
        this.vegan = vegan;
        this.vegetarian = vegetarian;
        this.price = price;
        this.uuid = uuid;
        this.rating = rating;
        this.mimgId = mimgId;
    }

    protected Food(Parcel in) {
        id = in.readInt();
        name = in.readString();
        category = in.readString();
        date = in.readString();
        vegan = in.readInt();
        vegetarian = in.readInt();
        price = in.readFloat();
        uuid = in.readString();
        rating = in.readFloat();
        mimgId = in.readInt();
    }

    public static final Creator<Food> CREATOR = new Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel in) {
            return new Food(in);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public int isVegan() {
        return vegan;
    }

    public int isVegetarian() {
        return vegetarian;
    }

    public float getPrice() {
        return price;
    }

    public String getUuid() {
        return uuid;
    }

    public float getRating(){return rating;}

    public int getmimgId() {
        return mimgId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(((Float)price).toString());
        dest.writeString(name);
        dest.writeString(((Float)rating).toString());
        dest.writeInt(mimgId);

    }
}
