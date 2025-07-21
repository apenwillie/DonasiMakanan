package com.example.donasimakanan.manager;

import com.example.donasimakanan.DatabaseManager;
import com.example.donasimakanan.model.Restaurant;

import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

public class RestaurantManager {

    /**
     * Menambahkan restoran baru secara asinkron untuk mencegah blokir UI thread.
     */
    public Restaurant addRestaurant(String name, String address, String phoneNumber, String description) {
        try(Realm realm = DatabaseManager.getInstance().getRealm()){
            realm.beginTransaction();
            Restaurant newRestaurant = realm.createObject(Restaurant.class, UUID.randomUUID().toString());
            newRestaurant.setName(name);
            newRestaurant.setAddress(address);
            newRestaurant.setPhoneNumber(phoneNumber);
            newRestaurant.setDescription(description);
            realm.commitTransaction();
            return newRestaurant;
        } catch (RealmException e) {
            throw new RuntimeException("Error adding restaurant: " + e.getMessage(), e);
        }
    }

    /**
     * Fungsi untuk mengambil semua restoran yang ada di dalam database.
     */
    public List<Restaurant> getAllRestaurants(){
        try(Realm realm = DatabaseManager.getInstance().getRealm()){
            List<Restaurant> restaurants = realm.where(Restaurant.class).findAll();
            return realm.copyFromRealm(restaurants);
        }
    }

    /**
     * Fungsi untuk mengambil restoran berdasarkan ID.
     * @param restaurantId ID restoran yang ingin diambil.
     * @return Objek Restaurant jika ditemukan, null jika tidak ditemukan.
     */
    public Restaurant getRestaurantById(String restaurantId) {
        try(Realm realm = DatabaseManager.getInstance().getRealm()) {
            Restaurant restaurant = realm.where(Restaurant.class)
                    .equalTo("restaurantId", restaurantId)
                    .findFirst();
            return restaurant != null ? realm.copyFromRealm(restaurant) : null;
        }
    }
}
