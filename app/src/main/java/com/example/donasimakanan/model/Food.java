package com.example.donasimakanan.model;

import java.util.UUID;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Merepresentasikan objek Makanan (Food) yang tersedia untuk donasi di sebuah restoran.
 * Kelas ini menyimpan detail spesifik dari setiap item makanan, seperti nama, stok,
 * harga, dan poin yang didapat dari donasi. Objek ini dikelola oleh Realm
 * sebagai sebuah tabel di dalam database.
 */
public class Food extends RealmObject {

    /**
     * Kunci utama (Primary Key) yang unik untuk setiap item makanan.
     * ID ini dibuat secara otomatis menggunakan UUID untuk memastikan setiap item
     * dapat diidentifikasi secara individual.
     */
    @PrimaryKey
    private String foodId;

    /**
     * Nama dari item makanan yang akan ditampilkan kepada pengguna.
     * Contoh: "Nasi Ayam Geprek", "Roti Cokelat".
     */
    private String name;

    /**
     * Deskripsi singkat mengenai makanan, bisa berisi komposisi atau detail lainnya.
     */
    private String description;

    /**
     * Jumlah stok atau ketersediaan dari makanan ini di restoran terkait.
     * Nilai ini akan berkurang setiap kali ada donasi yang berhasil dilakukan.
     */
    private int stock;

    /**
     * Harga satuan dari makanan ini dalam Rupiah.
     */
    private int price;

    /**
     * ID dari restoran yang menyediakan makanan ini.
     * Berfungsi sebagai kunci asing (foreign key) yang menghubungkan makanan ini
     * dengan objek {@link Restaurant} yang sesuai.
     */
    private String restaurantId;

    /**
     * Jumlah poin yang akan diterima oleh pengguna jika berhasil mendonasikan
     * satu unit dari makanan ini.
     */
    private int point;

    /**
     * Konstruktor kosong yang wajib ada untuk Realm.
     * Realm menggunakan konstruktor ini secara internal untuk membuat objek proxy
     * yang terhubung dengan data di database.
     */
    public Food() {
        // Realm requires an empty constructor
    }

    /**
     * Konstruktor untuk membuat objek Food baru dengan data awal.
     * ID unik untuk makanan akan dibuat secara otomatis di dalam konstruktor ini.
     *
     * @param name Nama makanan.
     * @param description Deskripsi singkat makanan.
     * @param stock Jumlah stok awal yang tersedia.
     * @param restaurantId ID dari restoran yang menyediakan makanan ini.
     * @param point Jumlah poin yang didapat dari donasi item ini.
     */
    public Food(String name, String description, int stock, String restaurantId, int point) {
        this.foodId = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.stock = stock;
        this.restaurantId = restaurantId;
        this.point = point;
    }

    // --- GETTERS ---
    // Kumpulan method di bawah ini berfungsi untuk mengambil nilai dari setiap field.

    public String getFoodId() { return foodId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getStock() { return stock; }
    public String getRestaurant() { return restaurantId; }
    public int getPrice() { return price; }
    public int getPoint() { return point; }

    // --- SETTERS ---
    // Kumpulan method di bawah ini berfungsi untuk menetapkan nilai baru ke setiap field.
    // Perubahan ini harus dilakukan di dalam sebuah transaksi tulis Realm.

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setRestaurant(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
