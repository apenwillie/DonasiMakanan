package com.example.donasimakanan.manager;

import com.example.donasimakanan.DatabaseManager;
import com.example.donasimakanan.model.Food;
import com.example.donasimakanan.model.Restaurant;

import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;


public class FoodManager {
    private Realm realm;

    
    public FoodManager() {
        this.realm = DatabaseManager.getInstance().getRealm();
    }

    
    public Food addFood(String name, String description, int stock, String restaurantId) {
        realm.beginTransaction();
        try {
            
            Food newFood = realm.createObject(Food.class, UUID.randomUUID().toString());
            newFood.setName(name);
            newFood.setDescription(description);
            newFood.setStock(stock);
            newFood.setRestaurant(restaurantId);
            realm.commitTransaction();
            return newFood;
        } catch (RealmException e) {
            if (realm.isInTransaction()) realm.cancelTransaction();
            return null;
        }
    }

    
    public Food getFoodById(String foodId) {
        try {
            return realm.where(Food.class).equalTo("foodId", foodId).findFirst();
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengambil makanan berdasarkan ID: " + e.getMessage(), e);
        }
    }

    
    public List<Food> getFoodByRestaurantId(String restaurantId) {
        RealmResults<Food> foods = realm.where(Food.class).equalTo("restaurantId", restaurantId)
                .findAll();
        // Mengembalikan salinan agar aman digunakan di luar thread Realm
        return realm.copyFromRealm(foods);
    }

    
    public void decreaseStock(String foodId, int quantity) {
        realm.beginTransaction();
        try {
            Food food = realm.where(Food.class).equalTo("foodId", foodId).findFirst();
            if (food != null && food.getStock() >= quantity) {
                food.setStock(food.getStock() - quantity);
                realm.commitTransaction();
            } else {
                realm.cancelTransaction();
                if (food != null) throw new IllegalArgumentException("Stok makanan tidak mencukupi.");
            }
        } catch (Exception e) {
            if (realm.isInTransaction()) realm.cancelTransaction();
            throw e; // Lemparkan kembali error untuk ditangani di pemanggil
        }
    }
}
