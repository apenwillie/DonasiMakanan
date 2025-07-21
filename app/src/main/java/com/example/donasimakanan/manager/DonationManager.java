package com.example.donasimakanan.manager;

import android.content.Context;
import android.widget.Toast;

import com.example.donasimakanan.DatabaseManager;
import com.example.donasimakanan.model.Donation;
import com.example.donasimakanan.model.Food;
import com.example.donasimakanan.model.User;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Mengelola semua operasi database yang terkait dengan Donasi (Donation).
 * Kelas ini menyediakan serangkaian fungsi untuk menangani proses pembuatan donasi,
 * pengambilan riwayat, dan validasi terkait donasi.
 */
public class DonationManager {
    private Realm realm;
    private FoodManager foodManager;
    private UserManager userManager;
    private Context context;

    /**
     * Konstruktor untuk DonationManager.
     * Menginisialisasi instance Realm dan manager lain yang dibutuhkan (FoodManager, UserManager)
     * untuk semua operasi di dalam kelas ini.
     *
     * @param context Context dari aplikasi, diperlukan untuk UserManager dan fungsionalitas lain.
     */
    public DonationManager(Context context) {
        this.context = context.getApplicationContext();
        this.realm = DatabaseManager.getInstance().getRealm();
        this.foodManager = new FoodManager();
        this.userManager = new UserManager(context);
    }

    /**
     * Menambahkan catatan donasi baru ke dalam sistem.
     * Method ini melakukan serangkaian operasi dalam satu transaksi atomik:
     * 1. Memvalidasi saldo pengguna.
     * 2. Membuat objek Donasi baru.
     * 3. Mengurangi stok makanan.
     * 4. Mengurangi saldo pengguna.
     * 5. Menambahkan poin ke pengguna.
     * Jika salah satu langkah gagal, semua perubahan akan dibatalkan.
     *
     * @param userId ID pengguna yang melakukan donasi.
     * @param foodId ID makanan yang didonasikan.
     * @param quantity Jumlah makanan yang didonasikan.
     * @param restaurantId ID restoran tempat makanan berasal.
     * @param description Catatan tambahan dari pengguna untuk donasi ini.
     * @throws IllegalArgumentException jika makanan tidak ditemukan, saldo tidak cukup, atau pengguna tidak ditemukan.
     * @throws Exception jika terjadi kesalahan lain saat transaksi.
     */
    public void addDonation(String userId, String foodId, int quantity, String restaurantId, String description) {
        realm.beginTransaction();
        try {
            User user = userManager.getUserById(userId);
            Food food = foodManager.getFoodById(foodId);

            if (food == null) {
                throw new IllegalArgumentException("Makanan dengan ID yang diberikan tidak ditemukan.");
            }
            if (user == null) {
                throw new IllegalArgumentException("Pengguna dengan ID yang diberikan tidak ditemukan.");
            }

            int totalAmount = food.getPrice() * quantity;
            if (user.getBalance() < totalAmount) {
                // Memberikan feedback langsung ke pengguna jika saldo tidak cukup.
                Toast.makeText(context, "Saldo tidak mencukupi untuk donasi ini.", Toast.LENGTH_SHORT).show();
                throw new IllegalArgumentException("Saldo tidak mencukupi untuk donasi ini.");
            }

            // Membuat objek donasi baru
            Donation donation = realm.createObject(Donation.class, UUID.randomUUID().toString());
            donation.setUserId(userId);
            donation.setFoodId(foodId);
            donation.setQuantity(quantity);
            donation.setDescription(description);
            donation.setRestaurantId(restaurantId);
            donation.setDonationDate(new Date());

            // Melakukan perubahan pada objek lain yang terkait
            food.setStock(food.getStock() - quantity);
            user.decreaseBalance(totalAmount);
            user.addPoints(food.getPoint() * quantity);

            realm.commitTransaction();

        } catch (Exception e) {
            if (realm.isInTransaction()) {
                realm.cancelTransaction();
            }
            throw e; // Lemparkan kembali error untuk ditangani di pemanggil
        }
    }

    /**
     * Mengambil riwayat donasi untuk seorang pengguna spesifik.
     * Method ini mengembalikan salinan data (unmanaged list) dari Realm,
     * yang berarti data tidak akan otomatis ter-update jika ada perubahan di database.
     *
     * @param userId ID dari pengguna yang riwayatnya ingin dilihat.
     * @return {@link List<Donation>} daftar salinan objek donasi.
     */
    public List<Donation> getUserDonation(String userId) {
        RealmResults<Donation> donations = realm.where(Donation.class).equalTo("userId", userId).findAll();
        return realm.copyFromRealm(donations);
    }

    /**
     * Menghitung dan menetapkan poin yang didapat untuk sebuah donasi yang sudah ada.
     * Fungsi ini bisa digunakan jika poin tidak dihitung saat donasi pertama kali dibuat.
     *
     * @param donationId ID dari donasi yang poinnya akan dihitung.
     * @throws IllegalArgumentException jika makanan terkait donasi tidak ditemukan.
     */
    public void calcaulatePoints(String donationId) {
        realm.beginTransaction();
        try {
            Donation donation = realm.where(Donation.class).equalTo("donationId", donationId).findFirst();
            if (donation == null) {
                throw new IllegalArgumentException("Donasi tidak ditemukan.");
            }
            Food food = foodManager.getFoodById(donation.getFoodId());
            if (food != null) {
                int points = food.getPoint() * donation.getQuantity();
                donation.setPointsEarned(points);
            } else {
                throw new IllegalArgumentException("Makanan terkait donasi tidak ditemukan.");
            }
            realm.commitTransaction();
        } catch (Exception e) {
            if (realm.isInTransaction()) realm.cancelTransaction();
            throw e;
        }
    }

    /**
     * Memeriksa apakah stok makanan yang tersedia mencukupi untuk jumlah donasi yang diminta.
     *
     * @param foodId ID dari makanan yang akan diperiksa.
     * @param quantity Jumlah yang ingin didonasikan.
     * @return {@code true} jika stok mencukupi, {@code false} jika tidak atau jika makanan tidak ditemukan.
     * @throws RuntimeException jika terjadi kesalahan saat mengakses database.
     */
    public boolean checkQuantity(String foodId, int quantity) {
        try {
            Food food = foodManager.getFoodById(foodId);
            if (food != null) {
                return food.getStock() >= quantity;
            }
            return false; // Makanan tidak ditemukan
        } catch (Exception e) {
            throw new RuntimeException("Gagal memeriksa kuantitas makanan: " + e.getMessage(), e);
        }
    }
}
