package com.example.donasimakanan;

import android.app.Application;
import android.util.Log;

import com.example.donasimakanan.model.Food;
import com.example.donasimakanan.model.Restaurant;
import com.example.donasimakanan.model.Reward;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class DatabaseManager extends Application {

    private static DatabaseManager instance;
    private Realm realm;

    
    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
        initializeRealm();
        seedDatabaseIfEmpty();
    }

    
    public static synchronized DatabaseManager getInstance(){
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

   
    private void initializeRealm(){
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("donasimakanan.realm")
                .schemaVersion(3) // Naikkan versi ini jika Anda mengubah skema model
                .deleteRealmIfMigrationNeeded() // Hapus DB jika skema berubah (hanya untuk development)
                .allowWritesOnUiThread(true) // Izinkan operasi tulis di UI thread (untuk kemudahan)
                .build();
        Realm.setDefaultConfiguration(config);
        realm = getRealm();
        Log.d("DatabaseManager", "Realm berhasil diinisialisasi dengan konfigurasi: " + config.toString());
    }

    
    public Realm getRealm(){
        return Realm.getDefaultInstance();
    }

    
    public void closeRealm(){
        if(realm != null && !realm.isClosed()){
            realm.close();
            Log.d("DatabaseManager", "Instance Realm berhasil ditutup.");
        } else {
            Log.w("DatabaseManager", "Percobaan menutup Realm, tetapi sudah tertutup atau null.");
        }
    }

   

    public void seedDatabaseIfEmpty() {
        Realm realm = Realm.getDefaultInstance();
        try {
            // --- Seed Restoran ---
            if (realm.where(Restaurant.class).count() == 0) {
                realm.executeTransaction(r -> {
                    // Data Restoran
                    String r1Id = UUID.randomUUID().toString();
                    Restaurant r1 = r.createObject(Restaurant.class, r1Id);
                    r1.setName("Restoran Sehat");
                    r1.setAddress("Jl. Kesehatan No. 10, Yogyakarta");
                    r1.setPhoneNumber("081234567890");
                    r1.setDescription("Menyediakan makanan sehat dan bergizi.");

                    String r2Id = UUID.randomUUID().toString();
                    Restaurant r2 = r.createObject(Restaurant.class, r2Id);
                    r2.setName("Dapur Berkah");
                    r2.setAddress("Jl. Berkah No. 5, Sleman");
                    r2.setPhoneNumber("081234123123");
                    r2.setDescription("Melayani donasi makanan siap saji.");

                    String r3Id = UUID.randomUUID().toString();
                    Restaurant r3 = r.createObject(Restaurant.class, r3Id);
                    r3.setName("Sari Roti Bakery");
                    r3.setAddress("Jl. Malioboro No. 120, Yogyakarta");
                    r3.setPhoneNumber("085511223344");
                    r3.setDescription("Roti segar setiap hari, cocok untuk sarapan.");

                    String r4Id = UUID.randomUUID().toString();
                    Restaurant r4 = r.createObject(Restaurant.class, r4Id);
                    r4.setName("Warteg Ibu Siti");
                    r4.setAddress("Jl. Gejayan No. 1, Condongcatur");
                    r4.setPhoneNumber("087755667788");
                    r4.setDescription("Masakan rumah dengan harga terjangkau.");
                });
            }

            // --- Seed Makanan ---
            if (realm.where(Food.class).count() == 0) {
                realm.executeTransaction(r -> {
                    // Ambil ID restoran yang sudah dibuat
                    Restaurant resto1 = r.where(Restaurant.class).equalTo("name", "Restoran Sehat").findFirst();
                    Restaurant resto2 = r.where(Restaurant.class).equalTo("name", "Dapur Berkah").findFirst();
                    Restaurant resto3 = r.where(Restaurant.class).equalTo("name", "Sari Roti Bakery").findFirst();
                    Restaurant resto4 = r.where(Restaurant.class).equalTo("name", "Warteg Ibu Siti").findFirst();

                    // Makanan untuk Restoran Sehat
                    if (resto1 != null) {
                        Food f1 = r.createObject(Food.class, UUID.randomUUID().toString());
                        f1.setName("Paket Nasi Ayam Bakar");
                        f1.setDescription("Nasi, ayam bakar madu, lalapan, dan sambal.");
                        f1.setStock(50);
                        f1.setPrice(20000);
                        f1.setPoint(20);
                        f1.setRestaurant(resto1.getRestaurantId());

                        Food f2 = r.createObject(Food.class, UUID.randomUUID().toString());
                        f2.setName("Salad Buah Segar");
                        f2.setDescription("Campuran buah segar dengan saus yogurt.");
                        f2.setStock(30);
                        f2.setPrice(15000);
                        f2.setPoint(15);
                        f1.setRestaurant(resto1.getRestaurantId());
                    }

                    // Makanan untuk Dapur Berkah
                    if (resto2 != null) {
                        Food f3 = r.createObject(Food.class, UUID.randomUUID().toString());
                        f3.setName("Nasi Kuning Komplit");
                        f3.setDescription("Nasi kuning, telur, kering tempe, dan abon.");
                        f3.setStock(40);
                        f3.setPrice(12000);
                        f3.setPoint(10);
                        f3.setRestaurant(resto2.getRestaurantId());
                    }

                    // Makanan untuk Sari Roti Bakery
                    if (resto3 != null) {
                        Food f4 = r.createObject(Food.class, UUID.randomUUID().toString());
                        f4.setName("Donat Cokelat Meses");
                        f4.setDescription("Donat empuk dengan topping cokelat dan meses.");
                        f4.setStock(100);
                        f4.setPrice(5000);
                        f4.setPoint(5);
                        f4.setRestaurant(resto3.getRestaurantId());
                    }

                    // Makanan untuk Warteg Ibu Siti
                    if (resto4 != null) {
                        Food f5 = r.createObject(Food.class, UUID.randomUUID().toString());
                        f5.setName("Paket Nasi Telur Orek");
                        f5.setDescription("Nasi putih, telur dadar, orek tempe, dan sayur.");
                        f5.setStock(60);
                        f5.setPrice(10000);
                        f5.setPoint(10);
                        f5.setRestaurant(resto4.getRestaurantId());
                    }
                });
            }

            // --- Seed Hadiah (Reward) ---
            if (realm.where(Reward.class).count() == 0) {
                realm.executeTransaction(r -> {
                    Reward reward1 = r.createObject(Reward.class, UUID.randomUUID().toString());
                    reward1.setName("Voucher Diskon Rp 5.000");
                    reward1.setDescription("Potongan Rp 5.000 untuk donasi berikutnya.");
                    reward1.setPointsRequired(100);
                    reward1.setStock(999);

                    Reward reward2 = r.createObject(Reward.class, UUID.randomUUID().toString());
                    reward2.setName("Voucher Diskon Rp 15.000");
                    reward2.setDescription("Potongan Rp 15.000 untuk donasi berikutnya.");
                    reward2.setPointsRequired(250);
                    reward2.setStock(500);

                    Reward reward3 = r.createObject(Reward.class, UUID.randomUUID().toString());
                    reward3.setName("Kaos Eksklusif Donasi Makanan");
                    reward3.setDescription("T-Shirt official sebagai tanda apresiasi.");
                    reward3.setPointsRequired(500);
                    reward3.setStock(100);

                    Reward reward4 = r.createObject(Reward.class, UUID.randomUUID().toString());
                    reward4.setName("Donasi Ganda");
                    reward4.setDescription("Donasi Anda berikutnya akan kami gandakan (maks. 3 porsi).");
                    reward4.setPointsRequired(750);
                    reward4.setStock(50);
                });
            }
        } finally {
            // Selalu tutup realm setelah selesai
            realm.close();
        }
    }
}
