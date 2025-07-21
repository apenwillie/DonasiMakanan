package com.example.donasimakanan;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Activity utama aplikasi yang berfungsi sebagai wadah (container) untuk fragment-fragment utama.
 * Kelas ini bertanggung jawab untuk mengatur layout utama dan menangani logika navigasi
 * menggunakan BottomNavigationView, memungkinkan pengguna untuk beralih antara halaman
 * Home, History, dan Settings.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Dipanggil saat Activity pertama kali dibuat.
     * Method ini adalah titik awal untuk inisialisasi layout, menangani tampilan edge-to-edge,
     * memuat fragment awal (HomeFragment), dan mengatur listener untuk BottomNavigationView.
     *
     * @param savedInstanceState Jika activity dibuat ulang dari state sebelumnya, ini adalah Bundlenya.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Mengaktifkan mode edge-to-edge agar aplikasi dapat digambar di bawah status dan navigation bar sistem
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Menambahkan listener untuk menangani window insets (area status bar dan navigation bar)
        // Ini memastikan bahwa komponen UI penting tidak tertutup oleh bar sistem.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Memuat HomeFragment sebagai tampilan default saat MainActivity pertama kali dibuka
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }

        // Inisialisasi BottomNavigationView dan atur listener untuk item yang dipilih
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            // Memilih fragment yang sesuai berdasarkan ID item menu yang diklik
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_history) {
                selectedFragment = new HistoryFragment();
            } else if (itemId == R.id.nav_settings) {
                selectedFragment = new SettingsFragment();
            }

            // Jika ada fragment yang terpilih, ganti fragment yang sedang tampil di container
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            // Mengembalikan true untuk menandakan bahwa item yang dipilih telah ditangani
            return true;
        });
    }
}
