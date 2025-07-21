package com.example.donasimakanan.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Merepresentasikan objek Pengguna (User) dalam aplikasi.
 * Kelas ini bertanggung jawab untuk menyimpan semua informasi yang terkait dengan pengguna,
 * termasuk data pribadi, kredensial untuk login, serta data terkait poin dan saldo.
 * Objek ini dikelola oleh Realm sebagai tabel di dalam database.
 */
public class User extends RealmObject {

    /**
     * Kunci utama (Primary Key) yang unik untuk setiap pengguna.
     * Biasanya diisi dengan UUID (Universally Unique Identifier) saat registrasi.
     */
    @PrimaryKey
    @Required
    private String userId;

    /**
     * Alamat email pengguna, digunakan sebagai username untuk login.
     * Wajib diisi (@Required) dan harus unik untuk setiap pengguna.
     */
    @Required
    private String email;

    /**
     * Hash dari password pengguna. Password asli tidak pernah disimpan,
     * hanya hasil hash-nya menggunakan algoritma SHA-256.
     * Wajib diisi (@Required).
     */
    @Required
    private String passwordHash;

    /**
     * Nama lengkap pengguna. Wajib diisi (@Required).
     */
    @Required
    private String fullName;

    /**
     * Nomor telepon pengguna. Bersifat opsional.
     */
    private String phoneNumber;

    /**
     * Alamat tempat tinggal pengguna. Bersifat opsional.
     */
    private String address;

    /**
     * Jumlah total poin yang dimiliki pengguna saat ini.
     * Poin didapatkan dari donasi dan dapat ditukarkan dengan hadiah.
     */
    private int totalPoints;

    /**
     * Jumlah saldo uang elektronik yang dimiliki pengguna.
     */
    private int balance;

    /**
     * Status keaktifan akun pengguna.
     * Dapat digunakan untuk menonaktifkan akun tanpa menghapusnya.
     */
    private boolean isActive;

    /**
     * Konstruktor kosong yang wajib ada untuk Realm.
     * Realm menggunakan ini untuk membuat objek proxy.
     * Menginisialisasi nilai default untuk poin, saldo, dan status aktif.
     */
    public User() {
        this.totalPoints = 0;
        this.balance = 0;
        this.isActive = true;
    }

    /**
     * Konstruktor untuk membuat pengguna baru saat proses registrasi.
     * Secara otomatis melakukan hash pada password yang diberikan.
     *
     * @param userId ID unik untuk pengguna baru.
     * @param email Alamat email pengguna.
     * @param password Password dalam bentuk teks biasa (plain text) yang akan di-hash.
     * @param fullName Nama lengkap pengguna.
     */
    public User(String userId, String email, String password, String fullName) {
        this(); // Memanggil konstruktor kosong untuk inisialisasi default
        this.userId = userId;
        this.email = email;
        this.passwordHash = hashPassword(password);  // Password langsung di-hash
        this.fullName = fullName;
    }

    // --- GETTERS ---
    // Kumpulan method di bawah ini berfungsi untuk mengambil nilai dari setiap field.

    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddress() { return address; }
    public int getTotalPoints() { return totalPoints; }
    public boolean isActive() { return isActive; }
    public int getBalance() {return balance;}

    // --- SETTERS ---
    // Kumpulan method di bawah ini berfungsi untuk menetapkan nilai baru ke setiap field.
    // Perubahan ini harus dilakukan di dalam sebuah transaksi Realm.

    public void setUserId(String userId) { this.userId = userId; }
    public void setEmail(String email) { this.email = email; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAddress(String address) { this.address = address; }
    public void setActive(boolean active) { this.isActive = active; }
    public void setBalance(int balance) {this.balance = balance;}
    public void setTotalPoints(int points) {this.totalPoints = points;}

    /**
     * Menetapkan password baru untuk pengguna.
     * Method ini menerima password dalam bentuk teks biasa dan secara otomatis
     * melakukan hashing sebelum menyimpannya ke field `passwordHash`.
     *
     * @param newPassword Password baru dalam bentuk teks biasa.
     */
    public void setPassword(String newPassword) {
        this.passwordHash = hashPassword(newPassword);
    }

    /**
     * Memverifikasi apakah password yang dimasukkan pengguna cocok dengan hash yang tersimpan.
     * Digunakan saat proses login untuk otentikasi.
     *
     * @param inputPassword Password dalam bentuk teks biasa yang dimasukkan oleh pengguna.
     * @return {@code true} jika password cocok, {@code false} jika tidak.
     */
    public boolean verifyPassword(String inputPassword) {
        if (inputPassword == null || this.passwordHash == null) {
            return false;
        }
        String inputHash = hashPassword(inputPassword);
        return this.passwordHash.equals(inputHash);
    }

    /**
     * Method internal untuk melakukan hash pada password menggunakan algoritma SHA-256.
     * Mengubah string password menjadi representasi heksadesimal yang aman.
     *
     * @param password Password dalam bentuk teks biasa.
     * @return String representasi hash dari password.
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());

            // Konversi array byte ke dalam format string heksadesimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Fallback jika SHA-256 tidak tersedia (sangat jarang terjadi).
            // Di lingkungan produksi, ini harus ditangani dengan logging atau error yang lebih baik.
            return password; // Tidak direkomendasikan untuk produksi.
        }
    }

    /**
     * Menambahkan poin ke total poin pengguna.
     * Hanya menambahkan jika jumlah poin yang diberikan lebih besar dari nol.
     *
     * @param points Jumlah poin yang akan ditambahkan.
     */
    public void addPoints(int points) {
        if (points > 0) this.totalPoints += points;
    }

    /**
     * Mengurangi saldo pengguna.
     * Melakukan validasi untuk memastikan saldo cukup sebelum dikurangi.
     *
     * @param amount Jumlah saldo yang akan dikurangi.
     */
    public void decreaseBalance(int amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
        }
    }

    /**
     * Menggunakan (mengurangi) poin pengguna untuk ditukar dengan hadiah.
     * Melakukan validasi untuk memastikan poin yang dimiliki pengguna mencukupi.
     *
     * @param points Jumlah poin yang akan digunakan.
     * @return {@code true} jika poin berhasil digunakan, {@code false} jika poin tidak cukup.
     */
    public boolean usePoints(int points) {
        if (points > 0 && this.totalPoints >= points) {
            this.totalPoints -= points;
            return true;
        }
        return false;
    }
}
