package com.example.donasimakanan;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donasimakanan.manager.RewardManager; // ✅ 1. Import RewardManager
import com.example.donasimakanan.manager.UserManager;
import com.example.donasimakanan.model.Reward;
import com.example.donasimakanan.model.User;

import java.util.List;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.RewardViewHolder> {

    private final List<Reward> rewards;
    private final UserManager userManager;
    private final RewardManager rewardManager; // ✅ 2. Tambahkan RewardManager

    public RewardAdapter(Context context, List<Reward> rewards) {
        this.rewards = rewards;
        this.userManager = new UserManager(context);
        this.rewardManager = new RewardManager(context);
    }

    @NonNull
    @Override
    public RewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reward, parent, false);
        return new RewardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardViewHolder holder, int position) {
        final Reward reward = rewards.get(position);

        holder.tvName.setText(reward.getName());
        holder.tvDescription.setText(reward.getDescription());
        holder.tvPoints.setText(reward.getPointsRequired() + " Poin");
        holder.tvStock.setText("Stok: " + reward.getStock());

        holder.btnRedeem.setOnClickListener(v -> {
            // ✅ 4. LENGKAPI LOGIKA DI SINI
            try {
                // Langkah A: Dapatkan pengguna saat ini
                User currentUser = userManager.getCurrentUser();
                if (currentUser == null) {
                    Toast.makeText(v.getContext(), "Sesi tidak ditemukan, silakan login ulang.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Langkah B: Validasi poin pengguna
                if (currentUser.getTotalPoints() < reward.getPointsRequired()) {
                    Toast.makeText(v.getContext(), "Poin Anda tidak cukup!", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean success = rewardManager.redeemReward(reward.getRewardId());

                if (success) {
                    Toast.makeText(v.getContext(), "Berhasil menukarkan: " + reward.getName(), Toast.LENGTH_SHORT).show();
                    notifyItemChanged(holder.getAdapterPosition());
                } else {
                    Toast.makeText(v.getContext(), "Gagal menukar. Stok mungkin habis.", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Toast.makeText(v.getContext(), "Terjadi kesalahan.", Toast.LENGTH_SHORT).show();
                Log.e("RedeemError", "Gagal menukar hadiah", e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rewards != null ? rewards.size() : 0;
    }

    public static class RewardViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvPoints, tvStock;
        Button btnRedeem;

        public RewardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_reward_name);
            tvDescription = itemView.findViewById(R.id.tv_reward_description);
            tvPoints = itemView.findViewById(R.id.tv_reward_points);
            tvStock = itemView.findViewById(R.id.tv_reward_stock);
            btnRedeem = itemView.findViewById(R.id.btn_redeem);
        }
    }
}