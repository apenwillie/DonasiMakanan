package com.example.donasimakanan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.donasimakanan.manager.UserManager;
import com.example.donasimakanan.model.User;
import com.example.donasimakanan.util.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Sebuah Activity yang menangani proses otentikasi pengguna (login).
 * Kelas ini menyediakan antarmuka bagi pengguna untuk memasukkan email dan password,
 * memvalidasi input, memverifikasi kredensial dengan database, dan mengelola sesi login.
 * Implementasi ini mencakup penanganan error dan navigasi yang tepat setelah login berhasil.
 */
public class LoginActivity extends AppCompatActivity {

    // Komponen UI untuk input email dan password
    private TextInputEditText etEmail, etPassword;
    // Tombol untuk memicu aksi login
    private Button btnLogin;
    // Teks yang berfungsi sebagai link untuk pindah ke halaman registrasi
    private TextView tvRegisterLink;

    // Manager untuk mengelola operasi database terkait pengguna
    private UserManager userManager;
    // Manager untuk mengelola sesi login pengguna
    private SessionManager sessionManager;

    /**
     * Dipanggil saat Activity pertama kali dibuat.
     * Method ini adalah titik awal untuk inisialisasi layout, komponen UI,
     * manager, dan listener.
     *
     * @param savedInstanceState Jika activity dibuat ulang dari state sebelumnya, ini adalah Bundlenya.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialisasi manager yang diperlukan
        userManager = new UserManager(getApplicationContext());
        sessionManager = new SessionManager(this);

        // Inisialisasi semua komponen View dan atur listener-nya
        initViews();
        setupListeners();
    }

    /**
     * Menginisialisasi semua komponen UI dari layout XML.
     * Method ini mencari dan menetapkan referensi untuk setiap View
     * menggunakan ID-nya masing-masing.
     */
    private void initViews() {
        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegisterLink = findViewById(R.id.tv_register_link);
    }

    /**
     * Menetapkan semua listener untuk interaksi pengguna pada komponen UI.
     */
    private void setupListeners() {
        // Listener untuk tombol login, akan memanggil method performLogin saat diklik
        btnLogin.setOnClickListener(v -> performLogin());

        // Listener untuk link registrasi, akan membuka RegisterActivity saat diklik
        tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Menjalankan proses login setelah tombol ditekan.
     * Method ini mengambil input dari pengguna, melakukan validasi dasar,
     * memverifikasi kredensial dengan database, dan membuat sesi jika berhasil.
     */
    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validasi dasar untuk memastikan input tidak kosong
        if (email.isEmpty()) {
            etEmail.setError("Email harus diisi");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password harus diisi");
            return;
        }

        // Mencari pengguna di database berdasarkan email
        User user = userManager.getUserByEmail(email);

        if (user == null) {
            Toast.makeText(this, "Email tidak terdaftar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Memverifikasi password yang dimasukkan dengan hash yang tersimpan
        if (user.verifyPassword(password)) {
            // Jika login berhasil, buat sesi untuk pengguna
            sessionManager.createLoginSession(
                    user.getUserId(),
                    user.getEmail(),
                    user.getFullName()
            );

            Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show();
            redirectToMain(); // Arahkan ke halaman utama
        } else {
            Toast.makeText(this, "Password salah", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Mengarahkan pengguna ke MainActivity setelah login berhasil.
     * Method ini menggunakan Intent Flags untuk membersihkan riwayat activity sebelumnya,
     * sehingga pengguna tidak bisa kembali ke halaman login dengan tombol "Back".
     */
    private void redirectToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Menutup LoginActivity
    }

    /**
     * Dipanggil saat Activity akan dihancurkan.
     * Method ini digunakan untuk membersihkan sumber daya, seperti menutup
     * koneksi database Realm untuk mencegah kebocoran memori.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userManager != null) {
            userManager.close();
        }
    }
}
