package com.example.donasimakanan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donasimakanan.model.Restaurant;

import java.util.List;

/**
 * Adapter untuk menampilkan daftar restoran dalam sebuah RecyclerView.
 * Kelas ini bertanggung jawab untuk mengambil daftar objek {@link Restaurant},
 * mengubahnya menjadi tampilan visual per item, dan menangani interaksi pengguna
 * seperti klik pada item untuk melihat detail restoran.
 */
public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {
    private List<Restaurant> restaurantList;

    /**
     * Konstruktor untuk RestaurantAdapter.
     *
     * @param restaurantList Daftar (List) dari objek {@link Restaurant} yang akan ditampilkan.
     */
    public RestaurantAdapter(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    /**
     * Kelas internal yang merepresentasikan dan menampung (hold) komponen View
     * untuk setiap item restoran dalam RecyclerView.
     * Ini meningkatkan performa dengan menghindari pemanggilan `findViewById()` yang berulang.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvAddress, tvStatus;

        /**
         * Konstruktor untuk ViewHolder.
         * Mencari dan menyimpan referensi ke setiap sub-view (seperti ImageView dan TextView)
         * dari layout item.
         *
         * @param itemView View tunggal yang merepresentasikan satu item dalam daftar.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_restaurant_image);
            tvName = itemView.findViewById(R.id.tv_restaurant_name);
            tvAddress = itemView.findViewById(R.id.tv_restaurant_address);
            tvStatus = itemView.findViewById(R.id.tv_restaurant_status);
        }
    }

    /**
     * Dipanggil saat RecyclerView membutuhkan ViewHolder baru untuk sebuah item.
     * Method ini meng-inflate layout XML untuk satu item (R.layout.item_restaurant_layout)
     * dan mengembalikannya dalam sebuah instance ViewHolder baru.
     *
     * @param parent ViewGroup tempat view baru akan ditambahkan setelah di-bind.
     * @param viewType Tipe view dari item baru.
     * @return Sebuah instance {@link ViewHolder} baru yang menampung View untuk setiap item.
     */
    @NonNull
    @Override
    public RestaurantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant_layout, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Dipanggil oleh RecyclerView untuk menampilkan data pada posisi tertentu.
     * Method ini mengambil data dari objek {@link Restaurant} pada posisi yang diberikan,
     * mengisi komponen-komponen View di dalam ViewHolder, dan menetapkan listener
     * untuk menangani klik pada item.
     *
     * @param holder ViewHolder yang akan diperbarui untuk merepresentasikan konten item.
     * @param position Posisi item di dalam dataset adapter.
     */
    @Override
    public void onBindViewHolder(@NonNull RestaurantAdapter.ViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);

        // Mengisi data restoran ke dalam komponen View
        holder.tvName.setText(restaurant.getName());
        holder.tvAddress.setText(restaurant.getAddress());
        holder.tvStatus.setText("Menerima Donasi"); // Status default
        holder.ivImage.setImageResource(R.drawable.ic_restaurant_placeholder); // Gambar placeholder

        // Menetapkan listener untuk setiap item view
        holder.itemView.setOnClickListener(v -> {
            // Membuat instance baru dari RestaurantDetail fragment, mengirimkan ID restoran
            Fragment fragment = RestaurantDetail.newInstance(restaurant.getRestaurantId());

            // Melakukan transaksi untuk mengganti fragment saat ini dengan fragment detail
            // Ini memungkinkan navigasi ke halaman detail saat item diklik.
            ((AppCompatActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null) // Menambahkan transaksi ke back stack
                    .commit();
        });
        // Catatan: Untuk memuat gambar dari URL, gunakan library seperti Glide atau Picasso di sini.
        // Contoh: Glide.with(holder.ivImage.getContext()).load(restaurant.getImageUrl()).into(holder.ivImage);
    }

    /**
     * Mengembalikan jumlah total item di dalam dataset yang dipegang oleh adapter.
     *
     * @return Jumlah total restoran dalam restaurantList.
     */
    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    /**
     * Memperbarui dataset yang digunakan oleh adapter dan memberi tahu RecyclerView
     * untuk me-refresh tampilannya.
     * Berguna saat data berubah dan perlu ditampilkan ulang.
     *
     * @param restaurantList List baru dari objek {@link Restaurant}.
     */
    public void updateData(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
        notifyDataSetChanged(); // Memberi tahu adapter bahwa seluruh data telah berubah
    }
}
