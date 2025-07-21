package com.example.donasimakanan.model;

import java.util.Date;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class Reward extends RealmObject {

    
    @PrimaryKey
    private String rewardId;

    
    @Required
    private String userId;

    
    private String name;

   
    private String description;

    
    private int pointsRequired;

    
    private int stock;

    
    private Date createdDate;

    
    public Reward() {}

    
    public Reward(String rewardId, String userId, String name, String description, int pointsRequired, int stock) {
        this.rewardId = rewardId;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.pointsRequired = pointsRequired;
        this.stock = stock;
        this.createdDate = new Date(); // Tanggal ditetapkan otomatis saat pembuatan
    }

    

    public String getRewardId() { return rewardId; }
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPointsRequired() { return pointsRequired; }
    public int getStock() { return stock; }
    public Date getCreatedDate() { return createdDate; }

    

    public void setRewardId(String rewardId) { this.rewardId = rewardId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPointsRequired(int pointsRequired) { this.pointsRequired = pointsRequired; }
    public void setStock(int stock) { this.stock = stock; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    
    public void decreseStock(int quantity) {
        if (quantity > 0 && quantity <= this.stock) {
            this.stock -= quantity;
        } else {
            // Melempar exception jika stok tidak cukup atau kuantitas tidak valid.
            throw new IllegalArgumentException("Kuantitas tidak valid atau melebihi stok yang tersedia.");
        }
    }
}
