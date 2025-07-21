package com.example.donasimakanan.manager;

import android.content.Context;
import android.util.Log;

import com.example.donasimakanan.DatabaseManager;
import com.example.donasimakanan.model.Reward;
import com.example.donasimakanan.model.User;
import com.example.donasimakanan.model.UserRewardExchange;
import com.example.donasimakanan.util.SessionManager;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.exceptions.RealmException;


public class RewardManager {
    private Realm realm;
    private SessionManager sessionManager;

   
    public RewardManager(Context context) {
        this.realm = DatabaseManager.getInstance().getRealm();
        this.sessionManager = new SessionManager(context);
    }

    
    public boolean createReward(String userId, String name, String description, int pointsRequired, int stock) {
        realm.beginTransaction();
        try {
            Reward reward = realm.createObject(Reward.class, UUID.randomUUID().toString());
            reward.setUserId(userId);
            reward.setName(name);
            reward.setDescription(description);
            reward.setPointsRequired(pointsRequired);
            reward.setStock(stock);
            reward.setCreatedDate(new Date());
            realm.commitTransaction();
            return true;
        } catch (RealmException e) {
            if (realm.isInTransaction()) realm.cancelTransaction();
            return false;
        }
    }

    
    public RealmResults<Reward> getAllActiveRewards() {
        return realm.where(Reward.class)
                .greaterThan("stock", 0)
                .sort("pointsRequired", Sort.ASCENDING)
                .findAll();
    }

    
    public Reward getRewardById(String rewardId) {
        return realm.where(Reward.class)
                .equalTo("rewardId", rewardId)
                .findFirst();
    }

    
    public boolean redeemReward(String rewardId) {
        
        realm.beginTransaction();
        try {
            String userId = sessionManager.getUserId();
            User user = realm.where(User.class).equalTo("userId", userId).findFirst();
            Reward reward = realm.where(Reward.class).equalTo("rewardId", rewardId).findFirst();

            // Validasi data sebelum melanjutkan
            if (user == null || reward == null) {
                throw new RealmException("Pengguna atau Hadiah tidak ditemukan.");
            }
            if (user.getTotalPoints() < reward.getPointsRequired()) {
                throw new IllegalStateException("Poin tidak cukup.");
            }
            if (reward.getStock() <= 0) {
                throw new IllegalStateException("Stok hadiah habis.");
            }

            // Lakukan semua perubahan data
            reward.setStock(reward.getStock() - 1);
            user.usePoints(reward.getPointsRequired()); // Menggunakan method dari User model

            UserRewardExchange exchange = realm.createObject(UserRewardExchange.class, UUID.randomUUID().toString());
            exchange.setUserId(userId);
            exchange.setRewardId(rewardId);
            exchange.setPointsUsed(reward.getPointsRequired());

            realm.commitTransaction();
            return true;

        } catch (Exception e) {
            if (realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            Log.e("RedeemReward", "Gagal menukar hadiah, transaksi dibatalkan.", e);
            return false;
        }
    }

    
    public RealmResults<UserRewardExchange> getUserRewards(String userId) {
        return realm.where(UserRewardExchange.class)
                .equalTo("userId", userId)
                .sort("redeemedDate", Sort.DESCENDING)
                .findAll();
    }

    
    public boolean updateRewardStock(String rewardId, int newStock) {
        realm.beginTransaction();
        try {
            Reward reward = realm.where(Reward.class)
                    .equalTo("rewardId", rewardId)
                    .findFirst();

            if (reward != null) {
                reward.setStock(newStock);
                realm.commitTransaction();
                return true;
            } else {
                realm.cancelTransaction();
                return false;
            }
        } catch (RealmException e) {
            if (realm.isInTransaction()) realm.cancelTransaction();
            return false;
        }
    }
}
