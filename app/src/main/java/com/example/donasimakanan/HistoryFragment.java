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

/**
 * Sebuah Fragment yang bertanggung jawab untuk menampilkan daftar riwayat donasi
 * dari pengguna yang sedang login.
 * Kelas ini mengambil data dari database melalui DonationManager dan menampilkannya
 * dalam sebuah RecyclerView.
 */
public class HistoryFragment extends Fragment {

    private Realm realm;
    private RecyclerView rvDonationHistory;
    private DonationHistoryAdapter adapter;
    private List<Donation> donationList;
    private DonationManager donationManager;
    private SessionManager sessionManager;
    private TextView tvEmptyMessage; // Opsional, untuk pesan jika riwayat kosong

    /**
     * Konstruktor kosong yang wajib ada untuk setiap Fragment.
     */
    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Dipanggil saat Fragment pertama kali dibuat.
     * Method ini digunakan untuk inisialisasi awal yang tidak terkait dengan tampilan (View),
     * seperti membuka koneksi database dan menginisialisasi manager.
     *
     * @param savedInstanceState Jika fragment dibuat ulang dari state sebelumnya, ini adalah Bundlenya.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Membuka instance Realm yang akan digunakan selama siklus hidup Fragment ini
        realm = Realm.getDefaultInstance();
        // Menginisialisasi manager yang diperlukan
        sessionManager = new SessionManager(requireContext());
        donationManager = new DonationManager(requireContext());
    }

    /**
     * Dipanggil untuk membuat dan mengembalikan hierarki View yang terkait dengan Fragment.
     * Method ini meng-inflate layout XML (R.layout.fragment_history) yang akan menjadi
     * tampilan visual dari Fragment ini.
     *
     * @param inflater Objek yang dapat meng-inflate layout XML menjadi objek View.
     * @param container Parent View tempat layout fragment akan disisipkan.
     * @param savedInstanceState Jika non-null, fragment ini dibuat ulang dari state yang disimpan.
     * @return View untuk UI Fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    /**
     * Dipanggil segera setelah onCreateView() selesai.
     * Method ini adalah tempat yang tepat untuk melakukan inisialisasi akhir pada View,
     * seperti mencari referensi ke RecyclerView dan mengatur data.
     *
     * @param view View yang dikembalikan oleh onCreateView().
     * @param savedInstanceState Jika non-null, fragment ini dibuat ulang dari state yang disimpan.
     */
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

            // Opsional: Menampilkan pesan jika daftar riwayat kosong
            // if(donationList.isEmpty()) {
            //    tvEmptyMessage.setVisibility(View.VISIBLE);
            // } else {
            //    tvEmptyMessage.setVisibility(View.GONE);
            // }

        } else {
            // Menangani kasus di mana tidak ada pengguna yang login
            // tvEmptyMessage.setText("Silakan login untuk melihat riwayat donasi.");
            // tvEmptyMessage.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Dipanggil saat View yang terkait dengan Fragment akan dihancurkan.
     * Ini adalah tempat yang tepat untuk membersihkan sumber daya yang terkait dengan View.
     * Di sini, kita menutup instance Realm untuk mencegah kebocoran memori.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Menutup instance Realm adalah langkah krusial untuk manajemen memori yang baik
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }
}
