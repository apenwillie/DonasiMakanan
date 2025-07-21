package com.example.donasimakanan.model;

import java.util.Date;
import java.util.UUID;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class Donation extends RealmObject {

    
    @PrimaryKey
    private String donationId;

    
    @Required
    private String userId;

    
    @Required
    private String restaurantId;

    
    @Required
    private String foodId;

    
    @Required
    private String foodName;

    
    private String description;

    
    private int quantity;

    
    private Date donationDate;

    
    private int pointsEarned;

    
    public Donation() {
        this.pointsEarned = 0;
        this.donationDate = new Date();
    }

    
    public Donation(String donationId, String foodId, String userId, String restaurantId, String foodName, String description, int quantity, int pointsEarned) {
        this.donationId = donationId;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.foodId = foodId;
        this.foodName = foodName;
        this.description = description;
        this.quantity = quantity;
        this.pointsEarned = pointsEarned;
    }

    

    public String getDonationId() { return donationId; }
    public String getUserId() { return userId; }
    public String getRestaurantId() { return restaurantId; }
    public String getFoodName() { return foodName; }
    public String getDescription() { return description; }
    public int getQuantity() { return quantity; }
    public Date getDonationDate() { return donationDate; }
    public int getPointsEarned() { return pointsEarned; }
    public String getFoodId() { return foodId; }

    

    public void setDonationId(String donationId) { this.donationId = donationId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }
    public void setFoodName(String foodName) { this.foodName = foodName; }
    public void setDescription(String description) { this.description = description; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPointsEarned(int pointsEarned) { this.pointsEarned = pointsEarned; }
    public void setFoodId(String foodId) { this.foodId = foodId; }
    public void setDonationDate(Date donationDate) { this.donationDate = donationDate; }
}
