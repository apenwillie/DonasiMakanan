package com.example.donasimakanan.model;

import java.util.Date;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Merepresentasikan sebuah catatan transaksi tunggal ketika seorang pengguna (User)
 * berhasil menukarkan poinnya (points) dengan sebuah hadiah (Reward).
 * Kelas ini berfungsi sebagai tabel penghubung antara User dan Reward,
 * menyimpan detail historis dari setiap penukaran.
 *
 * @see User
 * @see Reward
 */
public class UserRewardExchange extends RealmObject {

    /**
     * Kunci utama (Primary Key) yang unik untuk setiap transaksi penukaran.
     * Dibuat secara otomatis untuk memastikan setiap catatan dapat diidentifikasi secara individual.
     */
    @PrimaryKey
    private String userRewardId;

    /**
     * ID dari pengguna yang melakukan penukaran.
     * Berfungsi sebagai kunci asing (foreign key) yang merujuk ke objek {@link User}.
     * Anotasi @Required memastikan field ini tidak boleh null.
     */
    @Required
    private String userId;

    /**
     * ID dari hadiah yang ditukarkan.
     * Berfungsi sebagai kunci asing (foreign key) yang merujuk ke objek {@link Reward}.
     * Anotasi @Required memastikan field ini tidak boleh null.
     */
    @Required
    private String rewardId;

    /**
     * Jumlah poin yang dihabiskan oleh pengguna untuk penukaran ini.
     * Nilainya diambil dari field `pointsRequired` pada objek {@link Reward} saat transaksi terjadi.
     */
    private int pointsUsed;

    /**
     * Waktu dan tanggal pasti saat penukaran hadiah dilakukan.
     * Ditetapkan secara otomatis saat objek ini dibuat.
     */
    private Date redeemedDate;

    /**
     * Konstruktor kosong yang wajib ada untuk Realm.
     * Realm menggunakan konstruktor ini di belakang layar untuk membuat objek proxy
     * yang terhubung dengan database.
     */
    public UserRewardExchange() {}

    /**
     * Konstruktor untuk membuat instance baru dari UserRewardExchange dengan data awal.
     * Berguna saat membuat catatan penukaran baru secara manual.
     *
     * @param userRewardId ID unik untuk transaksi ini.
     * @param userId ID pengguna yang melakukan penukaran.
     * @param rewardId ID hadiah yang ditukarkan.
     * @param pointsUsed Jumlah poin yang digunakan.
     */
    public UserRewardExchange(String userRewardId, String userId, String rewardId, int pointsUsed) {
        this.userRewardId = userRewardId;
        this.userId = userId;
        this.rewardId = rewardId;
        this.pointsUsed = pointsUsed;
        this.redeemedDate = new Date(); // Tanggal ditetapkan otomatis saat pembuatan
    }

    /**
     * Mengembalikan ID unik dari catatan transaksi penukaran ini.
     * @return String ID unik transaksi.
     */
    public String getUserRewardId() { return userRewardId; }

    /**
     * Menetapkan ID unik untuk catatan transaksi penukaran ini.
     * @param userRewardId String ID unik yang akan ditetapkan.
     */
    public void setUserRewardId(String userRewardId) { this.userRewardId = userRewardId; }

    /**
     * Mengembalikan ID pengguna yang terkait dengan transaksi ini.
     * @return String ID pengguna.
     */
    public String getUserId() { return userId; }

    /**
     * Menetapkan ID pengguna untuk catatan transaksi ini.
     * @param userId String ID pengguna yang akan ditetapkan.
     */
    public void setUserId(String userId) { this.userId = userId; }

    /**
     * Mengembalikan ID hadiah yang ditukarkan dalam transaksi ini.
     * @return String ID hadiah.
     */
    public String getRewardId() { return rewardId; }

    /**
     * Menetapkan ID hadiah untuk catatan transaksi ini.
     * @param rewardId String ID hadiah yang akan ditetapkan.
     */
    public void setRewardId(String rewardId) { this.rewardId = rewardId; }

    /**
     * Mengembalikan jumlah poin yang digunakan dalam transaksi ini.
     * @return integer jumlah poin.
     */
    public int getPointsUsed() { return pointsUsed; }

    /**
     * Menetapkan jumlah poin yang digunakan untuk catatan transaksi ini.
     * @param pointsUsed integer jumlah poin yang akan ditetapkan.
     */
    public void setPointsUsed(int pointsUsed) { this.pointsUsed = pointsUsed; }
}