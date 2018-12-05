package de.hsulm.mensaapp;

public class Food {

    private int id;
    private String name;
    private String category;
    private String date;
    private int vegan;
    private int vegetarian;
    private float price;
    private String uuid;

    public Food(int id, String name, String category, String date, int vegan, int vegetarian, float price, String uuid){
        this.id = id;
        this.name = name;
        this.category = category;
        this.date = date;
        this.vegan = vegan;
        this.vegetarian = vegetarian;
        this.price = price;
        this.uuid = uuid;
    }

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

}
