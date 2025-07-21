package com.example.donasimakanan.model;

import java.util.UUID;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Merepresentasikan objek Restoran (Restaurant) yang terdaftar dalam aplikasi.
 * Kelas ini menyimpan semua informasi detail mengenai sebuah restoran,
 * seperti nama, alamat, dan deskripsi. Objek ini dikelola oleh Realm
 * sebagai sebuah tabel di dalam database lokal.
 */
public class Restaurant extends RealmObject {

    /**
     * Kunci utama (Primary Key) yang unik untuk setiap restoran.
     * ID ini dibuat secara otomatis menggunakan UUID saat objek baru dibuat,
     * memastikan tidak ada dua restoran yang memiliki ID yang sama.
     */
    @PrimaryKey
    @Required
    private String restaurantId;

    /**
     * Nama resmi dari restoran.
     * Wajib diisi (@Required) dan akan ditampilkan kepada pengguna.
     */
    @Required
    private String name;

    /**
     * Alamat lengkap lokasi fisik restoran.
     * Wajib diisi (@Required).
     */
    @Required
    private String address;

    /**
     * Nomor telepon restoran yang dapat dihubungi. Bersifat opsional.
     */
    private String phoneNumber;

    /**
     * Deskripsi singkat mengenai restoran, bisa berisi jenis masakan,
     * jam operasional, atau informasi lain yang relevan. Bersifat opsional.
     */
    private String description;

    /**
     * Konstruktor kosong yang wajib ada untuk Realm.
     * Realm menggunakan konstruktor ini di belakang layar saat membuat objek
     * dari data yang ada di database.
     */
    public Restaurant() {
    }

    /**
     * Konstruktor untuk membuat objek Restaurant baru dengan data awal.
     * ID unik untuk restoran akan dibuat secara otomatis di dalam konstruktor ini.
     *
     * @param name Nama restoran.
     * @param address Alamat lengkap restoran.
     * @param phoneNumber Nomor telepon yang bisa dihubungi.
     * @param description Deskripsi singkat tentang restoran.
     */
    public Restaurant(String name, String address, String phoneNumber, String description) {
        this.restaurantId = UUID.randomUUID().toString(); // ID unik dibuat otomatis
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.description = description;
    }

    // --- GETTERS ---
    // Kumpulan method di bawah ini berfungsi untuk mengambil nilai dari setiap field.

    public String getRestaurantId() { return restaurantId; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getDescription() { return description; }

    // --- SETTERS ---
    // Kumpulan method di bawah ini berfungsi untuk menetapkan nilai baru ke setiap field.
    // Perubahan ini harus dilakukan di dalam sebuah transaksi tulis Realm.

    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setDescription(String description) { this.description = description; }
}
