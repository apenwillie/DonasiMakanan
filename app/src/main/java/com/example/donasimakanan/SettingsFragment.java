package com.example.donasimakanan;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donasimakanan.manager.UserManager;
import com.example.donasimakanan.model.User;
import com.example.donasimakanan.util.SessionManager;

import io.realm.Realm;

/**
 * Sebuah Fragment yang berfungsi sebagai halaman Pengaturan (Settings).
 * Halaman ini menampilkan informasi dasar pengguna yang sedang login, seperti nama dan email,
 * dan menyediakan fungsionalitas untuk keluar dari aplikasi (logout).
 */
public class SettingsFragment extends Fragment {
    private Realm realm;
    private SessionManager sessionManager;
    private TextView tv_user_name, tv_user_email;
    private Button btn_logout;
    private UserManager userManager;

    /**
     * Konstruktor kosong yang wajib ada untuk setiap Fragment.
     */
    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Dipanggil saat Fragment pertama kali dibuat.
     * Method ini digunakan untuk inisialisasi awal yang tidak terkait dengan tampilan (View),
     * seperti membuka koneksi database dan menginisialisasi manager yang diperlukan.
     *
     * @param savedInstanceState Jika fragment dibuat ulang dari state sebelumnya, ini adalah Bundlenya.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Membuka instance Realm yang akan digunakan selama siklus hidup Fragment ini
        realm = Realm.getDefaultInstance();
        // Menginisialisasi manager yang diperlukan untuk sesi dan data pengguna
        sessionManager = new SessionManager(requireContext());
        userManager = new UserManager(getContext());
    }

    /**
     * Dipanggil untuk membuat dan mengembalikan hierarki View yang terkait dengan Fragment.
     * Method ini meng-inflate layout XML (R.layout.fragment_settings) yang akan menjadi
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
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    /**
     * Dipanggil segera setelah onCreateView() selesai.
     * Method ini adalah tempat yang tepat untuk melakukan inisialisasi akhir pada View,
     * seperti mencari referensi ke komponen UI, menampilkan data pengguna, dan mengatur listener.
     *
     * @param view View yang dikembalikan oleh onCreateView().
     * @param savedInstanceState Jika non-null, fragment ini dibuat ulang dari state yang disimpan.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Inisialisasi komponen UI dari layout
        tv_user_email = view.findViewById(R.id.tv_user_email);
        tv_user_name = view.findViewById(R.id.tv_user_name);
        btn_logout = view.findViewById(R.id.btn_logout);

        // Mengambil data pengguna yang sedang login
        User user = userManager.getUserById(sessionManager.getUserId());

        // Memeriksa apakah pengguna ditemukan sebelum menampilkan data
        if (user != null) {
            tv_user_email.setText("Email: " + user.getEmail());
            tv_user_name.setText("Nama: " + user.getFullName());
        } else {
            // Menangani kasus jika data pengguna tidak ditemukan (misal: setelah dihapus)
            tv_user_email.setText("Email: Tidak tersedia");
            tv_user_name.setText("Nama: Pengguna tidak ditemukan");
        }

        // Menetapkan listener untuk tombol logout
        btn_logout.setOnClickListener(v -> {
            // Menghapus sesi login yang tersimpan
            sessionManager.logout();

            // Mengarahkan pengguna kembali ke halaman Login
            // Intent flags digunakan untuk membersihkan semua activity sebelumnya dari back stack
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            // Memberikan feedback kepada pengguna bahwa mereka telah logout
            Toast.makeText(getContext(), "Anda telah logout", Toast.LENGTH_SHORT).show();

            // Menutup activity saat ini (MainActivity) agar tidak bisa kembali
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

    /**
     * Dipanggil saat View yang terkait dengan Fragment akan dihancurkan.
     * Ini adalah tempat yang tepat untuk membersihkan sumber daya.
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
