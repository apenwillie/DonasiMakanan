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


public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {
    private List<Restaurant> restaurantList;

    
    public RestaurantAdapter(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvAddress, tvStatus;

        
        public ViewHolder(View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_restaurant_image);
            tvName = itemView.findViewById(R.id.tv_restaurant_name);
            tvAddress = itemView.findViewById(R.id.tv_restaurant_address);
            tvStatus = itemView.findViewById(R.id.tv_restaurant_status);
        }
    }

    
    @NonNull
    @Override
    public RestaurantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant_layout, parent, false);
        return new ViewHolder(view);
    }

    
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
        
    }

    
    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    
    public void updateData(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
        notifyDataSetChanged(); // Memberi tahu adapter bahwa seluruh data telah berubah
    }
}
