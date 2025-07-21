package com.example.donasimakanan.manager;

import com.example.donasimakanan.DatabaseManager;
import com.example.donasimakanan.model.Food;
import com.example.donasimakanan.model.Restaurant;

import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

/**
 * Mengelola semua operasi database yang terkait dengan Makanan (Food).
 * Kelas ini menyediakan serangkaian fungsi untuk menangani penambahan,
 * pengambilan, dan pembaruan data makanan yang tersedia di restoran.
 */
public class FoodManager {
    private Realm realm;

    /**
     * Konstruktor untuk FoodManager.
     * Menginisialisasi instance Realm dari DatabaseManager yang akan digunakan
     * untuk semua operasi di dalam kelas ini.
     */
    public FoodManager() {
        this.realm = DatabaseManager.getInstance().getRealm();
    }

    /**
     * Menambahkan item makanan baru ke dalam sistem untuk sebuah restoran.
     * Operasi ini dilakukan di dalam sebuah transaksi Realm yang aman.
     *
     * @param name Nama dari makanan.
     * @param description Deskripsi singkat mengenai makanan.
     * @param stock Jumlah stok awal yang tersedia.
     * @param restaurantId ID dari restoran yang menyediakan makanan ini.
     * @return Objek {@link Food} yang baru dibuat jika berhasil, atau {@code null} jika terjadi kesalahan.
     */
    public Food addFood(String name, String description, int stock, String restaurantId) {
        realm.beginTransaction();
        try {
            // Seharusnya menggunakan createObject(Food.class, primaryKeyValue)
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

    /**
     * Mengambil satu objek makanan spesifik berdasarkan ID uniknya.
     *
     * @param foodId ID unik dari makanan yang dicari.
     * @return Objek {@link Food} jika ditemukan, atau {@code null} jika tidak.
     * @throws RuntimeException jika terjadi kesalahan saat mengakses database.
     */
    public Food getFoodById(String foodId) {
        try {
            return realm.where(Food.class).equalTo("foodId", foodId).findFirst();
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengambil makanan berdasarkan ID: " + e.getMessage(), e);
        }
    }

    /**
     * Mengambil semua makanan yang tersedia di sebuah restoran tertentu.
     * Method ini mengembalikan salinan data (unmanaged list) dari Realm.
     * Ini berarti data tidak akan otomatis ter-update jika ada perubahan di database.
     *
     * @param restaurantId ID dari restoran yang makanannya ingin ditampilkan.
     * @return {@link List<Food>} daftar salinan objek makanan.
     */
    public List<Food> getFoodByRestaurantId(String restaurantId) {
        RealmResults<Food> foods = realm.where(Food.class).equalTo("restaurantId", restaurantId)
                .findAll();
        // Mengembalikan salinan agar aman digunakan di luar thread Realm
        return realm.copyFromRealm(foods);
    }

    /**
     * Mengurangi jumlah stok untuk sebuah item makanan.
     * Operasi ini dilakukan dalam sebuah transaksi dan melakukan validasi untuk
     * memastikan stok yang ada mencukupi sebelum dikurangi.
     *
     * @param foodId ID dari makanan yang stoknya akan dikurangi.
     * @param quantity Jumlah yang akan dikurangkan dari stok.
     * @throws IllegalArgumentException jika stok tidak mencukupi.
     * @throws Exception jika terjadi kesalahan lain saat transaksi.
     */
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
