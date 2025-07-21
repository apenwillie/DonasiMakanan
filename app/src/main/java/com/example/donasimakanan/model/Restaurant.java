package com.example.donasimakanan.model;

import java.util.UUID;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class Restaurant extends RealmObject {

    
    @PrimaryKey
    @Required
    private String restaurantId;

    
    @Required
    private String name;

    
    @Required
    private String address;

    
    private String phoneNumber;

    
    private String description;

    
    public Restaurant() {
    }

   
    public Restaurant(String name, String address, String phoneNumber, String description) {
        this.restaurantId = UUID.randomUUID().toString(); // ID unik dibuat otomatis
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.description = description;
    }

    

    public String getRestaurantId() { return restaurantId; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getDescription() { return description; }

    

    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setDescription(String description) { this.description = description; }
}
