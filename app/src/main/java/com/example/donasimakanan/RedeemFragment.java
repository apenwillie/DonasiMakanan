package com.example.donasimakanan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.donasimakanan.manager.RewardManager;
import com.example.donasimakanan.model.Reward;
import io.realm.Realm;
import io.realm.RealmResults;


public class RedeemFragment extends Fragment {

    private Realm realm;
    private RecyclerView rvRewards;
    private RewardAdapter adapter;
    private RewardManager rewardManager;

    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Membuka instance Realm yang akan digunakan selama siklus hidup Fragment ini
        realm = Realm.getDefaultInstance();
        // Menginisialisasi manager yang diperlukan untuk mengambil data hadiah
        rewardManager = new RewardManager(requireContext());
    }

    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_redeem, container, false);
    }

    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvRewards = view.findViewById(R.id.rv_rewards);

        // Mengambil semua hadiah yang aktif (stok > 0) dari database
        RealmResults<Reward> activeRewards = rewardManager.getAllActiveRewards();

        // Menyiapkan Adapter dan RecyclerView untuk menampilkan data
        // Menggunakan RealmRecyclerViewAdapter (diasumsikan) untuk efisiensi dan update otomatis
        adapter = new RewardAdapter(requireContext(), activeRewards);
        rvRewards.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRewards.setAdapter(adapter);
    }

    
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Menutup instance Realm adalah langkah krusial untuk manajemen memori yang baik
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }
}
