package com.example.donasimakanan.model;

import java.util.Date;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class UserRewardExchange extends RealmObject {

    
    @PrimaryKey
    private String userRewardId;

    
    @Required
    private String userId;

    
    @Required
    private String rewardId;

    
    private int pointsUsed;

    
    private Date redeemedDate;

    
    public UserRewardExchange() {}

   
    public UserRewardExchange(String userRewardId, String userId, String rewardId, int pointsUsed) {
        this.userRewardId = userRewardId;
        this.userId = userId;
        this.rewardId = rewardId;
        this.pointsUsed = pointsUsed;
        this.redeemedDate = new Date(); // Tanggal ditetapkan otomatis saat pembuatan
    }

    
    public String getUserRewardId() { return userRewardId; }

    
    public void setUserRewardId(String userRewardId) { this.userRewardId = userRewardId; }

    
    public String getUserId() { return userId; }

    
    public void setUserId(String userId) { this.userId = userId; }

    
    public String getRewardId() { return rewardId; }

    
    public void setRewardId(String rewardId) { this.rewardId = rewardId; }

    
    public int getPointsUsed() { return pointsUsed; }

    
    public void setPointsUsed(int pointsUsed) { this.pointsUsed = pointsUsed; }
}
