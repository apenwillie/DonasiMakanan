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

/**
 * Mengelola semua operasi database yang terkait dengan Hadiah (Reward).
 * Kelas ini menyediakan fungsi untuk membuat, mengambil, dan menukarkan hadiah,
 * serta melihat riwayat penukaran oleh pengguna.
 */
public class RewardManager {
    private Realm realm;
    private SessionManager sessionManager;

    /**
     * Konstruktor untuk RewardManager.
     * Menginisialisasi instance Realm dan SessionManager yang akan digunakan
     * untuk semua operasi di dalam kelas ini.
     *
     * @param context Context dari aplikasi, diperlukan untuk SessionManager.
     */
    public RewardManager(Context context) {
        this.realm = DatabaseManager.getInstance().getRealm();
        this.sessionManager = new SessionManager(context);
    }

    /**
     * Membuat hadiah baru di dalam sistem. Fungsi ini biasanya hanya untuk admin.
     * Operasi dilakukan dalam sebuah transaksi Realm yang aman.
     *
     * @param userId ID pengguna (admin) yang membuat hadiah.
     * @param name Nama dari hadiah.
     * @param description Deskripsi rinci mengenai hadiah.
     * @param pointsRequired Jumlah poin yang dibutuhkan untuk menukar hadiah.
     * @param stock Jumlah stok awal yang tersedia untuk hadiah ini.
     * @return {@code true} jika hadiah berhasil dibuat, {@code false} jika terjadi kesalahan.
     */
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

    /**
     * Mengambil semua hadiah yang masih aktif (stok lebih dari 0).
     * Hasilnya diurutkan berdasarkan poin yang dibutuhkan (dari yang termurah),
     * sehingga memudahkan pengguna untuk melihat hadiah yang bisa mereka jangkau.
     *
     * @return {@link RealmResults<Reward>} daftar hadiah aktif yang bersifat live-updating.
     */
    public RealmResults<Reward> getAllActiveRewards() {
        return realm.where(Reward.class)
                .greaterThan("stock", 0)
                .sort("pointsRequired", Sort.ASCENDING)
                .findAll();
    }

    /**
     * Mengambil satu objek hadiah spesifik berdasarkan ID-nya.
     *
     * @param rewardId ID unik dari hadiah yang dicari.
     * @return Objek {@link Reward} jika ditemukan, atau {@code null} jika tidak.
     */
    public Reward getRewardById(String rewardId) {
        return realm.where(Reward.class)
                .equalTo("rewardId", rewardId)
                .findFirst();
    }

    /**
     * Memproses penukaran hadiah oleh pengguna.
     * Method ini melakukan serangkaian operasi dalam satu transaksi atomik:
     * 1. Memvalidasi data pengguna, hadiah, poin, dan stok.
     * 2. Mengurangi stok hadiah.
     * 3. Mengurangi poin pengguna.
     * 4. Membuat catatan riwayat penukaran di {@link UserRewardExchange}.
     * Jika salah satu langkah gagal, semua perubahan akan dibatalkan.
     *
     * @param rewardId ID dari hadiah yang akan ditukar.
     * @return {@code true} jika seluruh proses penukaran berhasil, {@code false} jika gagal.
     */
    public boolean redeemReward(String rewardId) {
        // Menggunakan try-with-resources adalah praktik yang baik, namun karena instance
        // realm di-manage secara global di sini, kita akan gunakan try-finally biasa.
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

    /**
     * Mengambil riwayat penukaran hadiah untuk seorang pengguna spesifik.
     * Hasilnya diurutkan berdasarkan tanggal penukaran (dari yang terbaru).
     *
     * @param userId ID dari pengguna yang riwayatnya ingin dilihat.
     * @return {@link RealmResults<UserRewardExchange>} daftar riwayat penukaran yang bersifat live-updating.
     */
    public RealmResults<UserRewardExchange> getUserRewards(String userId) {
        return realm.where(UserRewardExchange.class)
                .equalTo("userId", userId)
                .sort("redeemedDate", Sort.DESCENDING)
                .findAll();
    }

    /**
     * Memperbarui jumlah stok untuk sebuah hadiah.
     * Fungsi ini biasanya hanya untuk admin.
     *
     * @param rewardId ID dari hadiah yang stoknya akan diubah.
     * @param newStock Jumlah stok yang baru.
     * @return {@code true} jika berhasil, {@code false} jika hadiah tidak ditemukan atau terjadi error.
     */
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
