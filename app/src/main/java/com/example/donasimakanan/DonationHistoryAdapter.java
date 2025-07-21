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


public class DonationHistoryAdapter extends RecyclerView.Adapter<DonationHistoryAdapter.ViewHolder> {

    private List<Donation> donationList;
    private Context context;
    private FoodManager foodManager;

    
    public DonationHistoryAdapter(Context context, List<Donation> donationList) {
        this.context = context;
        this.donationList = donationList;
        // Menginisialisasi FoodManager untuk mengambil detail makanan
        this.foodManager = new FoodManager();
    }

    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_donation_history, parent, false);
        return new ViewHolder(view);
    }

    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Donation donation = donationList.get(position);
        if (donation != null) {
            
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

    
    @Override
    public int getItemCount() {
        return donationList != null ? donationList.size() : 0;
    }

   
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName, tvQuantity, tvDate;

        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tv_history_food_name);
            tvQuantity = itemView.findViewById(R.id.tv_history_quantity);
            tvDate = itemView.findViewById(R.id.tv_history_date);
        }
    }
}
