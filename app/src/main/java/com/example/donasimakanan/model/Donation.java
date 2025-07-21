package com.example.donasimakanan.model;

import java.util.Date;
import java.util.UUID;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Merepresentasikan sebuah catatan transaksi Donasi tunggal.
 * Kelas ini berfungsi sebagai catatan historis setiap kali seorang pengguna
 * berhasil melakukan donasi makanan dari sebuah restoran. Objek ini dikelola oleh Realm
 * sebagai sebuah tabel di dalam database.
 */
public class Donation extends RealmObject {

    /**
     * Kunci utama (Primary Key) yang unik untuk setiap transaksi donasi.
     * Dibuat secara otomatis untuk memastikan setiap catatan donasi dapat diidentifikasi
     * secara individual.
     */
    @PrimaryKey
    private String donationId;

    /**
     * ID dari pengguna yang melakukan donasi.
     * Berfungsi sebagai kunci asing (foreign key) yang merujuk ke objek {@link User}.
     * Wajib diisi (@Required).
     */
    @Required
    private String userId;

    /**
     * ID dari restoran tempat makanan didonasikan.
     * Berfungsi sebagai kunci asing (foreign key) yang merujuk ke objek {@link Restaurant}.
     * Wajib diisi (@Required).
     */
    @Required
    private String restaurantId;

    /**
     * ID dari makanan yang didonasikan.
     * Berfungsi sebagai kunci asing (foreign key) yang merujuk ke objek {@link Food}.
     * Wajib diisi (@Required).
     */
    @Required
    private String foodId;

    /**
     * Nama makanan yang didonasikan.
     * Disimpan di sini untuk kemudahan menampilkan riwayat, meskipun bisa juga didapat
     * melalui relasi dengan {@link Food}. Wajib diisi (@Required).
     */
    @Required
    private String foodName;

    /**
     * Deskripsi atau catatan tambahan dari pengguna saat melakukan donasi.
     * Bersifat opsional.
     */
    private String description;

    /**
     * Jumlah atau kuantitas makanan yang didonasikan dalam transaksi ini.
     */
    private int quantity;

    /**
     * Waktu dan tanggal pasti saat donasi dilakukan.
     * Ditetapkan secara otomatis saat objek ini dibuat.
     */
    private Date donationDate;

    /**
     * Jumlah poin yang diperoleh pengguna dari transaksi donasi ini.
     * Dihitung berdasarkan poin per makanan dikalikan dengan kuantitas.
     */
    private int pointsEarned;

    /**
     * Konstruktor kosong yang wajib ada untuk Realm.
     * Realm menggunakan ini untuk membuat objek proxy dari data di database.
     * Menginisialisasi nilai default untuk poin dan tanggal donasi.
     */
    public Donation() {
        this.pointsEarned = 0;
        this.donationDate = new Date();
    }

    /**
     * Konstruktor untuk membuat objek Donasi baru dengan data awal.
     *
     * @param donationId ID unik untuk donasi ini.
     * @param foodId ID makanan yang didonasikan.
     * @param userId ID pengguna yang berdonasi.
     * @param restaurantId ID restoran asal makanan.
     * @param foodName Nama makanan.
     * @param description Catatan tambahan dari pengguna.
     * @param quantity Jumlah makanan yang didonasikan.
     * @param pointsEarned Total poin yang didapat dari donasi ini.
     */
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

    // --- GETTERS ---
    // Kumpulan method di bawah ini berfungsi untuk mengambil nilai dari setiap field.

    public String getDonationId() { return donationId; }
    public String getUserId() { return userId; }
    public String getRestaurantId() { return restaurantId; }
    public String getFoodName() { return foodName; }
    public String getDescription() { return description; }
    public int getQuantity() { return quantity; }
    public Date getDonationDate() { return donationDate; }
    public int getPointsEarned() { return pointsEarned; }
    public String getFoodId() { return foodId; }

    // --- SETTERS ---
    // Kumpulan method di bawah ini berfungsi untuk menetapkan nilai baru ke setiap field.
    // Perubahan ini harus dilakukan di dalam sebuah transaksi tulis Realm.

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
