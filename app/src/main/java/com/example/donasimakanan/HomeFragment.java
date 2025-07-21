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
import android.widget.Button;
import android.widget.TextView;

import com.example.donasimakanan.manager.RestaurantManager;
import com.example.donasimakanan.manager.UserManager;
import com.example.donasimakanan.model.Restaurant;
import com.example.donasimakanan.model.User;

import java.util.List;


public class HomeFragment extends Fragment {

    // Komponen UI untuk menampilkan daftar restoran
    private RecyclerView rvRestaurant;
    // Adapter untuk menghubungkan data restoran dengan RecyclerView
    private RestaurantAdapter restaurantAdapter;
    // Manager untuk mengelola data pengguna
    private UserManager userManager;
    // Komponen UI untuk menampilkan informasi pengguna
    private TextView tvPoints, tvBalance, tvUsername;
    // Tombol untuk navigasi ke halaman tukar poin
    private Button btnRedeem;
    // Manager untuk mengelola data restoran
    private RestaurantManager restaurantManager = new RestaurantManager();

    
    public HomeFragment() {
    }

    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userManager = new UserManager(getContext());
    }

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inisialisasi semua komponen UI dari layout
        Button btn_topup = view.findViewById(R.id.btn_top_up_balance);
        btnRedeem = view.findViewById(R.id.btn_redeem_points);
        rvRestaurant = view.findViewById(R.id.rv_restaurants);
        tvPoints = view.findViewById(R.id.tv_points);
        tvBalance = view.findViewById(R.id.tv_balance);
        tvUsername = view.findViewById(R.id.tv_username);

        // Menyiapkan RecyclerView untuk menampilkan daftar restoran
        rvRestaurant.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Restaurant> restaurantList = restaurantManager.getAllRestaurants();
        restaurantAdapter = new RestaurantAdapter(restaurantList);
        rvRestaurant.setAdapter(restaurantAdapter);

        // Mengambil data pengguna yang sedang login dan memperbarui UI
        User currentUser = userManager.getCurrentUser();
        if (currentUser != null) {
            tvBalance.setText("Saldo Anda: Rp " + currentUser.getBalance());
            tvPoints.setText("Poin Anda: " + currentUser.getTotalPoints());
            tvUsername.setText("Halo, " + currentUser.getFullName());
        } else {
            // Menangani kasus jika tidak ada pengguna yang login
            tvPoints.setText("Login untuk melihat poin");
            tvBalance.setText("Saldo Anda: -");
            tvUsername.setText("Halo, Tamu");
        }

        // Menetapkan listener untuk tombol "Isi Saldo"
        btn_topup.setOnClickListener(v -> {
            // Membuat instance fragment tujuan
            TopupFragment topupFragment = new TopupFragment();
            // Melakukan transaksi untuk mengganti fragment saat ini dengan TopupFragment
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, topupFragment)
                        .addToBackStack(null) // Memungkinkan kembali dengan tombol back
                        .commit();
            }
        });

        // Menetapkan listener untuk tombol "Tukar Poin"
        btnRedeem.setOnClickListener(v->{
            // Membuat instance fragment tujuan
            RedeemFragment redeemPointsFragment = new RedeemFragment();
            // Melakukan transaksi untuk mengganti fragment saat ini dengan RedeemFragment
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, redeemPointsFragment)
                        .addToBackStack(null) // Memungkinkan kembali dengan tombol back
                        .commit();
            }
        });

        return view;
    }
}
