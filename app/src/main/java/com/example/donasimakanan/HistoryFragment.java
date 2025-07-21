package com.example.donasimakanan;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.donasimakanan.manager.DonationManager;
import com.example.donasimakanan.model.Donation;
import com.example.donasimakanan.util.SessionManager;

import java.util.List;

import io.realm.Realm;


public class HistoryFragment extends Fragment {

    private Realm realm;
    private RecyclerView rvDonationHistory;
    private DonationHistoryAdapter adapter;
    private List<Donation> donationList;
    private DonationManager donationManager;
    private SessionManager sessionManager;
    private TextView tvEmptyMessage; // Opsional, untuk pesan jika riwayat kosong

    
    public HistoryFragment() {
        
    }

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Membuka instance Realm yang akan digunakan selama siklus hidup Fragment ini
        realm = Realm.getDefaultInstance();
        // Menginisialisasi manager yang diperlukan
        sessionManager = new SessionManager(requireContext());
        donationManager = new DonationManager(requireContext());
    }

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvDonationHistory = view.findViewById(R.id.rv_donation_history);
        // tvEmptyMessage = view.findViewById(R.id.tv_empty_history); // Uncomment jika ada

        // Memeriksa apakah ada sesi pengguna yang aktif
        if (sessionManager.isLoggedIn()) {
            String userId = sessionManager.getUserId();

            // Mengambil daftar riwayat donasi untuk pengguna yang sedang login
            donationList = donationManager.getUserDonation(userId);

            // Menyiapkan Adapter dan RecyclerView untuk menampilkan data
            adapter = new DonationHistoryAdapter(getContext(), donationList);
            rvDonationHistory.setLayoutManager(new LinearLayoutManager(getContext()));
            rvDonationHistory.setAdapter(adapter);

            

        } else {
            
        }
    }

   
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }
}
