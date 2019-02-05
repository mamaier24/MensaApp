package de.hsulm.mensaapp.CLASS_OBJ_AND_ADPT;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Stephan Danz 05/12/2018
 * Class necessary for creating all food objects throughout the application
 */
public class FoodClass implements Parcelable {

    private int id;
    private String name;
    private String category;
    private String date;
    private int vegan;
    private int vegetarian;
    private String price;
    private String uuid;
    private int rating;
    private String imgID;
    private int number_rating;
    private String lastTime;


    public FoodClass(){ }

    public FoodClass(int id, String name, String category, int vegan, int vegetarian, String price,
                     String uuid, int rating, String mimgId, int number_rating, String lastTime){
        this.id = id;
        this.name = name;
        this.category = category;
        this.date = date;
        this.vegan = vegan;
        this.vegetarian = vegetarian;
        this.price = price;
        this.uuid = uuid;
        this.rating = rating;
        this.imgID = mimgId;
        this.number_rating = number_rating;
        this.lastTime = lastTime;
    }

    protected FoodClass(Parcel in) {
        id = in.readInt();
        name = in.readString();
        category = in.readString();
        date = in.readString();
        vegan = in.readInt();
        vegetarian = in.readInt();
        price = in.readString();
        uuid = in.readString();
        rating = in.readInt();
        imgID = in.readString();
        number_rating = in.readInt();
        lastTime = in.readString();
    }

    public static final Creator<FoodClass> CREATOR = new Creator<FoodClass>() {
        @Override
        public FoodClass createFromParcel(Parcel in) {
            return new FoodClass(in);
        }

        @Override
        public FoodClass[] newArray(int size) {
            return new FoodClass[size];
        }
    };

    public int getId() {
        return id;
    }


    public String getName() {
        if(name != null) {
            name = name.replaceAll(System.getProperty("line.separator"), (""));
        }
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


    public String getPrice() {
        if(price != null) {
            price = price.replaceAll(System.getProperty("line.separator"), (""));
            price = price.replaceAll("\\?","");
        }
        return price;
    }


    public String getUuid() {
        return uuid;
    }


    public int getRating(){return rating;}


    public String getmimgId() {
        return imgID;
    }


    public String getCategory(){return category;}


    public int getNumberRating(){return number_rating;}


    public String getLastTime() { return lastTime; }


    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(category);
        dest.writeString(date);
        dest.writeInt(vegan);
        dest.writeInt(vegetarian);
        dest.writeString(price);
        dest.writeString(uuid);
        dest.writeInt(rating);
        dest.writeString(imgID);
        dest.writeInt(number_rating);
        dest.writeString(lastTime);
    }

}
