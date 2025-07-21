package com.example.donasimakanan.model;

import java.util.UUID;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Food extends RealmObject {

   
    @PrimaryKey
    private String foodId;

    
    private String name;

    
    private String description;

    
    private int stock;

    
    private int price;

    
    private String restaurantId;

    
    private int point;

    
    public Food() {
        
    }

    
    public Food(String name, String description, int stock, String restaurantId, int point) {
        this.foodId = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.stock = stock;
        this.restaurantId = restaurantId;
        this.point = point;
    }

    

    public String getFoodId() { return foodId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getStock() { return stock; }
    public String getRestaurant() { return restaurantId; }
    public int getPrice() { return price; }
    public int getPoint() { return point; }

    

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setRestaurant(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
