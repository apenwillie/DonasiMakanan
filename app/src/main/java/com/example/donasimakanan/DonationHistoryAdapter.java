package com.example.donasimakanan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donasimakanan.manager.FoodManager;
import com.example.donasimakanan.model.Donation;
import com.example.donasimakanan.model.Food;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter untuk menampilkan daftar riwayat donasi pengguna dalam sebuah RecyclerView.
 * Kelas ini bertanggung jawab untuk mengambil daftar objek {@link Donation},
 * mengubahnya menjadi tampilan visual per item, dan menampilkannya kepada pengguna.
 * Adapter ini menggunakan List standar, yang berarti data tidak akan ter-update secara otomatis.
 */
public class DonationHistoryAdapter extends RecyclerView.Adapter<DonationHistoryAdapter.ViewHolder> {

    private List<Donation> donationList;
    private Context context;
    private FoodManager foodManager;

    /**
     * Konstruktor untuk DonationHistoryAdapter.
     *
     * @param context Context dari Fragment atau Activity yang memanggil, diperlukan untuk LayoutInflater.
     * @param donationList Daftar (List) dari objek {@link Donation} yang akan ditampilkan.
     */
    public DonationHistoryAdapter(Context context, List<Donation> donationList) {
        this.context = context;
        this.donationList = donationList;
        // Menginisialisasi FoodManager untuk mengambil detail makanan
        this.foodManager = new FoodManager();
    }

    /**
     * Dipanggil saat RecyclerView membutuhkan ViewHolder baru untuk sebuah item.
     * Method ini meng-inflate layout XML untuk satu item (R.layout.item_donation_history)
     * dan mengembalikannya dalam sebuah instance ViewHolder baru.
     *
     * @param parent ViewGroup tempat view baru akan ditambahkan setelah di-bind.
     * @param viewType Tipe view dari item baru.
     * @return Sebuah instance {@link ViewHolder} baru yang menampung View untuk setiap item.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_donation_history, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Dipanggil oleh RecyclerView untuk menampilkan data pada posisi tertentu.
     * Method ini mengambil data dari objek {@link Donation} pada posisi yang diberikan
     * dan mengisi komponen-komponen View di dalam ViewHolder dengan data tersebut.
     *
     * @param holder ViewHolder yang akan diperbarui untuk merepresentasikan konten item.
     * @param position Posisi item di dalam dataset adapter.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Donation donation = donationList.get(position);
        if (donation != null) {
            // Mengambil detail makanan berdasarkan foodId dari setiap donasi.
            // Catatan: Melakukan query database di dalam onBindViewHolder bisa mempengaruhi performa
            // jika daftar sangat panjang. Pendekatan yang lebih baik adalah menyimpan nama makanan
            // langsung di objek Donation saat dibuat.
            Food food = foodManager.getFoodById(donation.getFoodId());
            if (food != null) {
                holder.tvFoodName.setText("Donasi: " + food.getName());
            } else {
                holder.tvFoodName.setText("Donasi: Makanan tidak ditemukan");
            }

            holder.tvQuantity.setText("Jumlah: " + donation.getQuantity() + " Porsi");

            // Memformat objek Date menjadi string yang lebih mudah dibaca oleh pengguna
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
            String formattedDate = sdf.format(donation.getDonationDate());
            holder.tvDate.setText("Tanggal: " + formattedDate);
        }
    }

    /**
     * Mengembalikan jumlah total item di dalam dataset yang dipegang oleh adapter.
     *
     * @return Jumlah total donasi dalam donationList.
     */
    @Override
    public int getItemCount() {
        return donationList != null ? donationList.size() : 0;
    }

    /**
     * Kelas internal yang merepresentasikan dan menampung (hold) komponen View
     * untuk setiap item dalam RecyclerView.
     * Ini meningkatkan performa dengan menghindari pemanggilan `findViewById()` yang berulang.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName, tvQuantity, tvDate;

        /**
         * Konstruktor untuk ViewHolder.
         * Mencari dan menyimpan referensi ke setiap sub-view (seperti TextView)
         * dari layout item.
         *
         * @param itemView View tunggal yang merepresentasikan satu item dalam daftar.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tv_history_food_name);
            tvQuantity = itemView.findViewById(R.id.tv_history_quantity);
            tvDate = itemView.findViewById(R.id.tv_history_date);
        }
    }
}
