package com.example.donasimakanan.model;

import java.util.Date;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Merepresentasikan objek Hadiah (Reward) yang dapat ditukarkan oleh pengguna
 * menggunakan poin yang telah mereka kumpulkan.
 * Setiap objek Reward memiliki detail seperti nama, deskripsi, poin yang dibutuhkan, dan stok yang tersedia.
 */
public class Reward extends RealmObject {

    /**
     * Kunci utama (Primary Key) yang unik untuk setiap hadiah.
     * Digunakan untuk mengidentifikasi setiap hadiah secara spesifik dalam database.
     */
    @PrimaryKey
    private String rewardId;

    /**
     * ID dari pengguna yang membuat hadiah ini.
     * Umumnya digunakan untuk melacak admin atau pihak yang bertanggung jawab atas pembuatan hadiah.
     */
    @Required
    private String userId;

    /**
     * Nama dari hadiah yang ditampilkan kepada pengguna.
     * Contoh: "Voucher Diskon Rp 10.000".
     */
    private String name;

    /**
     * Deskripsi lebih rinci mengenai hadiah, termasuk syarat dan ketentuan jika ada.
     */
    private String description;

    /**
     * Jumlah poin yang harus dimiliki dan digunakan oleh pengguna untuk dapat menukarkan hadiah ini.
     */
    private int pointsRequired;

    /**
     * Jumlah stok atau ketersediaan dari hadiah ini.
     * Stok akan berkurang setiap kali hadiah berhasil ditukarkan.
     */
    private int stock;

    /**
     * Tanggal dan waktu saat hadiah ini pertama kali dibuat dan ditambahkan ke sistem.
     */
    private Date createdDate;

    /**
     * Konstruktor kosong yang wajib ada untuk Realm.
     * Realm menggunakan ini secara internal untuk proses pembuatan objek proxy.
     */
    public Reward() {}

    /**
     * Konstruktor untuk membuat instance baru dari sebuah Reward dengan data lengkap.
     * Berguna saat admin menambahkan hadiah baru ke dalam sistem.
     *
     * @param rewardId ID unik untuk hadiah ini.
     * @param userId ID pengguna (admin) yang membuat hadiah.
     * @param name Nama hadiah.
     * @param description Deskripsi rinci hadiah.
     * @param pointsRequired Jumlah poin yang dibutuhkan.
     * @param stock Jumlah stok awal.
     */
    public Reward(String rewardId, String userId, String name, String description, int pointsRequired, int stock) {
        this.rewardId = rewardId;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.pointsRequired = pointsRequired;
        this.stock = stock;
        this.createdDate = new Date(); // Tanggal ditetapkan otomatis saat pembuatan
    }

    // --- GETTERS ---
    // Kumpulan method di bawah ini berfungsi untuk mengambil nilai dari setiap field.

    public String getRewardId() { return rewardId; }
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPointsRequired() { return pointsRequired; }
    public int getStock() { return stock; }
    public Date getCreatedDate() { return createdDate; }

    // --- SETTERS ---
    // Kumpulan method di bawah ini berfungsi untuk menetapkan nilai baru ke setiap field.
    // Perubahan ini harus dilakukan di dalam sebuah transaksi Realm.

    public void setRewardId(String rewardId) { this.rewardId = rewardId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPointsRequired(int pointsRequired) { this.pointsRequired = pointsRequired; }
    public void setStock(int stock) { this.stock = stock; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    /**
     * Mengurangi jumlah stok hadiah.
     * Method ini melakukan validasi untuk memastikan stok yang ada mencukupi sebelum dikurangi.
     * Method ini harus dipanggil di dalam sebuah transaksi tulis Realm.
     *
     * @param quantity Jumlah yang akan dikurangkan dari stok.
     * @throws IllegalArgumentException jika jumlah yang diminta melebihi stok yang tersedia.
     */
    public void decreseStock(int quantity) {
        if (quantity > 0 && quantity <= this.stock) {
            this.stock -= quantity;
        } else {
            // Melempar exception jika stok tidak cukup atau kuantitas tidak valid.
            throw new IllegalArgumentException("Kuantitas tidak valid atau melebihi stok yang tersedia.");
        }
    }
}
