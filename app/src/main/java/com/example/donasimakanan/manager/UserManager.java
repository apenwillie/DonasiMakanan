package com.example.donasimakanan.manager;

import android.content.Context;

import io.realm.Realm;

import com.example.donasimakanan.DatabaseManager;
import com.example.donasimakanan.model.User;
import com.example.donasimakanan.util.SessionManager;

import java.util.UUID;

/**
 * Mengelola semua operasi database yang terkait dengan pengguna (User).
 * Kelas ini menyediakan serangkaian fungsi untuk menangani registrasi, otentikasi,
 * manajemen sesi, serta operasi pada poin dan saldo pengguna.
 * Ini adalah implementasi lengkap untuk sistem otentikasi pengguna.
 */
public class UserManager {
    private Realm realm;
    private SessionManager sessionManager;

    /**
     * Konstruktor untuk UserManager.
     * Menginisialisasi instance Realm dari DatabaseManager dan SessionManager
     * yang akan digunakan untuk semua operasi di dalam kelas ini.
     *
     * @param context Context dari aplikasi, diperlukan untuk menginisialisasi SessionManager.
     */
    public UserManager(Context context) {
        this.realm = DatabaseManager.getInstance().getRealm();
        this.sessionManager = new SessionManager(context);
    }

    /**
     * Mendaftarkan pengguna baru ke dalam sistem.
     * Method ini akan memeriksa terlebih dahulu apakah email sudah terdaftar.
     * Jika belum, pengguna baru akan dibuat di dalam sebuah transaksi Realm.
     *
     * @param email    Alamat email pengguna (harus unik).
     * @param password Kata sandi pengguna dalam bentuk teks biasa (akan di-hash secara otomatis).
     * @param fullName Nama lengkap pengguna.
     * @return Objek {@link User} yang baru dibuat jika berhasil, atau {@code null} jika email sudah ada.
     */
    public User registerUser(String email, String password, String fullName) {
        // Cek apakah email sudah ada sebelumnya
        User existingUser = getUserByEmail(email);
        if (existingUser != null) {
            return null; // Email sudah terdaftar
        }

        realm.beginTransaction();
        try {
            String userId = UUID.randomUUID().toString();
            User newUser = realm.createObject(User.class, userId);
            newUser.setEmail(email);
            newUser.setPassword(password); // Password akan di-hash otomatis di dalam kelas User
            newUser.setFullName(fullName);

            realm.commitTransaction();
            return newUser;
        } catch (Exception e) {
            realm.cancelTransaction();
            return null;
        }
    }

    /**
     * Mengambil data pengguna berdasarkan alamat email.
     * Method ini hanya akan mengembalikan pengguna dengan status aktif.
     *
     * @param email Alamat email pengguna yang dicari.
     * @return Objek {@link User} jika ditemukan, atau {@code null} jika tidak.
     */
    public User getUserByEmail(String email) {
        return realm.where(User.class)
                .equalTo("email", email)
                .equalTo("isActive", true)
                .findFirst();
    }

    /**
     * Mengotentikasi pengguna dengan email dan password.
     * Method ini akan mencari pengguna berdasarkan email, kemudian memverifikasi
     * hash password yang dimasukkan dengan yang tersimpan di database.
     *
     * @param email    Email pengguna.
     * @param password Kata sandi dalam bentuk teks biasa.
     * @return Objek {@link User} jika otentikasi berhasil, atau {@code null} jika gagal.
     */
    public User authenticateUser(String email, String password) {
        User user = getUserByEmail(email);
        if (user != null && user.verifyPassword(password)) {
            return user;
        }
        return null;
    }

    /**
     * Mengambil data pengguna berdasarkan ID uniknya.
     *
     * @param userId ID unik dari pengguna yang dicari.
     * @return Objek {@link User} jika ditemukan, atau {@code null} jika tidak.
     */
    public User getUserById(String userId) {
        return realm.where(User.class).equalTo("userId", userId).findFirst();
    }

    /**
     * Mengambil data pengguna yang sedang login saat ini.
     * Method ini membaca ID pengguna dari sesi yang aktif menggunakan SessionManager.
     *
     * @return Objek {@link User} dari pengguna yang sedang login, atau {@code null} jika tidak ada sesi aktif.
     */
    public User getCurrentUser() {
        String userId = sessionManager.getUserId();
        if (userId != null) {
            return getUserById(userId);
        }
        return null; // Tidak ada pengguna yang login
    }

    /**
     * Menambahkan poin ke akun pengguna yang sedang login.
     * Operasi ini dilakukan di dalam sebuah transaksi Realm yang aman.
     *
     * @param points Jumlah poin yang akan ditambahkan.
     * @throws Exception jika terjadi kesalahan saat transaksi.
     */
    public void addPoints(int points) {
        realm.beginTransaction();
        try {
            User user = getCurrentUser(); // Menggunakan getCurrentUser untuk lebih ringkas
            if (user != null) {
                user.addPoints(points);
                realm.commitTransaction();
            } else {
                realm.cancelTransaction();
            }
        } catch (Exception e) {
            if (realm.isInTransaction()) realm.cancelTransaction();
            throw e; // Lemparkan kembali error untuk ditangani di pemanggil
        }
    }

    /**
     * Mengurangi poin dari akun pengguna yang sedang login.
     * Melakukan validasi untuk memastikan poin pengguna mencukupi sebelum dikurangi.
     *
     * @param points Jumlah poin yang akan dikurangi.
     * @throws IllegalArgumentException jika poin tidak mencukupi.
     * @throws Exception                jika terjadi kesalahan lain saat transaksi.
     */
    public void decreasePoints(int points) {
        realm.beginTransaction();
        try {
            User user = getCurrentUser();
            if (user != null && user.getTotalPoints() >= points) {
                user.setTotalPoints(user.getTotalPoints() - points);
                realm.commitTransaction();
            } else {
                realm.cancelTransaction();
                if (user != null) throw new IllegalArgumentException("Poin tidak mencukupi");
            }
        } catch (Exception e) {
            if (realm.isInTransaction()) realm.cancelTransaction();
            throw e;
        }
    }

    /**
     * Menambahkan saldo ke akun pengguna yang sedang login.
     * Operasi ini dilakukan di dalam sebuah transaksi Realm yang aman.
     *
     * @param balance Jumlah saldo yang akan ditambahkan.
     * @throws Exception jika terjadi kesalahan saat transaksi.
     */
    public void addBalance(int balance) {
        realm.beginTransaction();
        try {
            User user = getCurrentUser();
            if (user != null) {
                user.setBalance(user.getBalance() + balance);
                realm.commitTransaction();
            } else {
                realm.cancelTransaction();
            }
        } catch (Exception e) {
            if (realm.isInTransaction()) realm.cancelTransaction();
            throw e;
        }
    }

    /**
     * Mengurangi saldo dari akun pengguna yang sedang login.
     * Melakukan validasi untuk memastikan saldo pengguna mencukupi sebelum dikurangi.
     *
     * @param balance Jumlah saldo yang akan dikurangi.
     * @throws IllegalArgumentException jika saldo tidak mencukupi.
     * @throws Exception                jika terjadi kesalahan lain saat transaksi.
     */
    public void decreaseBalance(int balance) {
        realm.beginTransaction();
        try {
            User user = getCurrentUser();
            if (user != null && user.getBalance() >= balance) {
                user.setBalance(user.getBalance() - balance);
                realm.commitTransaction();
            } else {
                realm.cancelTransaction();
                if (user != null) throw new IllegalArgumentException("Saldo tidak mencukupi");
            }
        } catch (Exception e) {
            if (realm.isInTransaction()) realm.cancelTransaction();
            throw e;
        }
    }

    /**
     * Menutup instance Realm untuk melepaskan sumber daya.
     * Method ini wajib dipanggil saat sebuah komponen (seperti Activity atau Fragment)
     * dihancurkan (onDestroy) untuk mencegah kebocoran memori.
     */
    public void close() {
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }
}
