package com.example.donasimakanan;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donasimakanan.manager.DonationManager;
import com.example.donasimakanan.model.Food;
import com.example.donasimakanan.util.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Adapter untuk menampilkan daftar makanan yang tersedia untuk donasi dalam sebuah RecyclerView.
 * Kelas ini tidak hanya menampilkan data, tetapi juga menangani seluruh interaksi pengguna
 * untuk setiap item makanan, mulai dari memilih jumlah hingga mengonfirmasi donasi.
 */
public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private List<Food> foods;
    private DonationManager donationManager;
    private SessionManager sessionManager;
    private String restaurantId;

    /**
     * Konstruktor untuk FoodAdapter.
     * Menginisialisasi semua komponen yang diperlukan untuk operasi di dalam adapter.
     *
     * @param context Context dari Fragment atau Activity, diperlukan untuk SessionManager.
     * @param foods Daftar (List) dari objek {@link Food} yang akan ditampilkan.
     * @param restaurantId ID dari restoran tempat makanan ini berada, diperlukan saat membuat donasi.
     */
    public FoodAdapter(Context context, List<Food> foods, String restaurantId) {
        this.foods = foods;
        this.restaurantId = restaurantId;

        // Inisialisasi manager yang akan digunakan untuk logika bisnis
        this.donationManager = new DonationManager(context);
        this.sessionManager = new SessionManager(context);
    }

    /**
     * Dipanggil saat RecyclerView membutuhkan ViewHolder baru untuk sebuah item.
     * Method ini meng-inflate layout XML untuk satu item (R.layout.item_food)
     * dan mengembalikannya dalam sebuah instance ViewHolder baru.
     *
     * @param parent ViewGroup tempat view baru akan ditambahkan setelah di-bind.
     * @param viewType Tipe view dari item baru.
     * @return Sebuah instance {@link FoodViewHolder} baru yang menampung View untuk setiap item.
     */
    @Nonnull
    @Override
    public FoodViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    /**
     * Mengembalikan jumlah total item di dalam dataset yang dipegang oleh adapter.
     * @return Jumlah total makanan dalam list.
     */
    @Override
    public int getItemCount() {
        return foods.size();
    }

    /**
     * Dipanggil oleh RecyclerView untuk menampilkan data pada posisi tertentu.
     * Method ini adalah inti dari adapter, di mana data dari objek {@link Food} diikat ke
     * komponen View dan semua listener untuk interaksi pengguna diatur.
     *
     * @param holder ViewHolder yang akan diperbarui untuk merepresentasikan konten item.
     * @param position Posisi item di dalam dataset adapter.
     */
    @Override
    public void onBindViewHolder(@Nonnull FoodViewHolder holder, int position) {
        Food food = foods.get(position);

        // Mengisi data awal makanan ke dalam TextViews
        holder.tvFoodName.setText(food.getName());
        holder.tvFoodDescription.setText(food.getDescription());
        holder.tvFoodPoint.setText("Poin: " + food.getPoint());
        holder.tvFoodStock.setText("Stok: " + food.getStock());
        holder.tvFoodPrice.setText("Harga: Rp " + food.getPrice());

        // Listener untuk tombol "Donasikan makanan"
        holder.btnDonate.setOnClickListener(v -> {
            // Menampilkan panel untuk mengatur jumlah donasi
            holder.quantityLayout.setVisibility(View.VISIBLE);
            // Menyembunyikan tombol awal untuk menghindari duplikasi
            holder.btnDonate.setVisibility(View.GONE);
        });

        // Listener untuk tombol tambah (+) kuantitas
        holder.btnPlus.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.etQuantity.getText().toString());
            // Validasi agar jumlah tidak melebihi stok yang tersedia
            if (currentQuantity < food.getStock()) {
                currentQuantity++;
                holder.etQuantity.setText(String.valueOf(currentQuantity));
            }
        });

        // Listener untuk tombol kurang (-) kuantitas
        holder.btnMinus.setOnClickListener(v -> {
            int currentQuantity = Integer.parseInt(holder.etQuantity.getText().toString());
            // Validasi agar jumlah tidak kurang dari 1
            if (currentQuantity > 1) {
                currentQuantity--;
                holder.etQuantity.setText(String.valueOf(currentQuantity));
            }
        });

        // Listener untuk tombol "Konfirmasi Donasi"
        holder.btnConfirm.setOnClickListener(v -> {
            String quantityStr = holder.etQuantity.getText().toString();
            int quantity = Integer.parseInt(quantityStr);

            String description = holder.etDescription.getText().toString().trim();
            String foodId = food.getFoodId();
            String userId = sessionManager.getUserId(); // Mengambil ID pengguna dari sesi

            // Validasi sesi pengguna
            if (userId == null || userId.isEmpty()) {
                Toast.makeText(holder.itemView.getContext(), "Sesi tidak ditemukan, silakan login ulang.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validasi ketersediaan stok sebelum melanjutkan
            if (!donationManager.checkQuantity(foodId, quantity)) {
                Toast.makeText(holder.itemView.getContext(), "Jumlah donasi melebihi stok yang tersedia.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Memanggil method addDonation dari DonationManager untuk memproses transaksi
            try {
                donationManager.addDonation(userId, foodId, quantity, this.restaurantId, description);
                Toast.makeText(holder.itemView.getContext(), "Donasi berhasil!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(holder.itemView.getContext(), "Gagal melakukan donasi.", Toast.LENGTH_SHORT).show();
                Log.e("DonationError", "Error: ", e);
            }

            // Mengembalikan tampilan item ke kondisi semula setelah donasi
            holder.quantityLayout.setVisibility(View.GONE);
            holder.btnDonate.setVisibility(View.VISIBLE);
            holder.etQuantity.setText("1"); // Reset jumlah ke 1
            holder.etDescription.setText(""); // Mengosongkan field catatan
        });
    }

    /**
     * Kelas internal yang merepresentasikan dan menampung (hold) komponen View
     * untuk setiap item makanan dalam RecyclerView.
     * Ini meningkatkan performa dengan menghindari pemanggilan `findViewById()` yang berulang.
     */
    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName, tvFoodDescription, tvFoodPoint, tvFoodStock, tvFoodPrice;
        Button btnDonate, btnMinus, btnPlus, btnConfirm;
        LinearLayout quantityLayout;
        EditText etQuantity;
        TextInputEditText etDescription;

        /**
         * Konstruktor untuk ViewHolder.
         * Mencari dan menyimpan referensi ke setiap sub-view dari layout item
         * untuk digunakan nanti saat proses binding data.
         *
         * @param itemView View tunggal yang merepresentasikan satu item dalam daftar.
         */
        public FoodViewHolder(@Nonnull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tv_food_name);
            tvFoodDescription = itemView.findViewById(R.id.tv_food_description);
            tvFoodPoint = itemView.findViewById(R.id.tv_food_point);
            tvFoodStock = itemView.findViewById(R.id.tv_food_stock);
            tvFoodPrice = itemView.findViewById(R.id.tv_food_price);
            btnDonate = itemView.findViewById(R.id.btn_donate_food);
            quantityLayout = itemView.findViewById(R.id.layout_donate_quantity);
            btnMinus = itemView.findViewById(R.id.btn_quantity_minus);
            btnPlus = itemView.findViewById(R.id.btn_quantity_plus);
            etQuantity = itemView.findViewById(R.id.et_quantity);
            btnConfirm = itemView.findViewById(R.id.btn_confirm_donation);
            etDescription = itemView.findViewById(R.id.et_donation_notes);
        }
    }
}
