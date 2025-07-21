package com.example.donasimakanan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.donasimakanan.manager.UserManager;
import com.example.donasimakanan.model.User;
import com.google.android.material.textfield.TextInputEditText;

import java.util.UUID;

/**
 * Sebuah Activity yang menangani proses registrasi pengguna baru.
 * Kelas ini menyediakan antarmuka bagi pengguna untuk memasukkan data diri (nama, email, password),
 * melakukan validasi input secara komprehensif, dan mendaftarkan pengguna baru ke dalam sistem
 * melalui UserManager.
 */
public class RegisterActivity extends AppCompatActivity {

    // Komponen UI untuk input data pengguna
    private TextInputEditText etFullName, etEmail, etPassword, etConfirmPassword;
    // Tombol untuk memicu aksi registrasi
    private Button btnRegister;
    // Teks yang berfungsi sebagai link untuk kembali ke halaman login
    private TextView tvLoginLink;

    // Manager untuk mengelola operasi database terkait pengguna
    private UserManager userManager;

    /**
     * Dipanggil saat Activity pertama kali dibuat.
     * Method ini adalah titik awal untuk inisialisasi layout, komponen UI,
     * dan UserManager.
     *
     * @param savedInstanceState Jika activity dibuat ulang dari state sebelumnya, ini adalah Bundlenya.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi UserManager yang akan digunakan untuk mendaftarkan pengguna
        userManager = new UserManager(getApplicationContext());

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
        etFullName = findViewById(R.id.et_register_fullname);
        etEmail = findViewById(R.id.et_register_email);
        etPassword = findViewById(R.id.et_register_password);
        etConfirmPassword = findViewById(R.id.et_register_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLoginLink = findViewById(R.id.tv_login_link);
    }

    /**
     * Menetapkan semua listener untuk interaksi pengguna pada komponen UI.
     */
    private void setupListeners() {
        // Listener untuk tombol registrasi, akan memanggil method performRegister saat diklik
        btnRegister.setOnClickListener(v -> performRegister());

        // Listener untuk link login, akan membuka LoginActivity saat diklik
        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Menutup activity ini agar tidak bisa kembali
        });
    }

    /**
     * Menjalankan proses registrasi setelah tombol ditekan.
     * Method ini mengambil semua input, memvalidasinya, dan jika valid,
     * akan memanggil UserManager untuk membuat pengguna baru.
     */
    private void performRegister() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Memvalidasi semua input sebelum melanjutkan
        if (!validateInputs(fullName, email, password, confirmPassword)) {
            return; // Hentikan proses jika validasi gagal
        }

        // Memanggil UserManager untuk mendaftarkan pengguna
        User newUser = userManager.registerUser(email, password, fullName);

        if (newUser != null) {
            // Jika registrasi berhasil
            Toast.makeText(this, "Registrasi berhasil! Silakan login.", Toast.LENGTH_LONG).show();

            // Arahkan pengguna ke halaman login
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Jika registrasi gagal (kemungkinan email sudah terdaftar)
            Toast.makeText(this, "Email sudah terdaftar", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Memvalidasi semua field input dari form registrasi.
     *
     * @param fullName Nama lengkap yang dimasukkan.
     * @param email Email yang dimasukkan.
     * @param password Password yang dimasukkan.
     * @param confirmPassword Konfirmasi password yang dimasukkan.
     * @return {@code true} jika semua input valid, {@code false} jika ada satu atau lebih input yang tidak valid.
     */
    private boolean validateInputs(String fullName, String email, String password, String confirmPassword) {
        // Validasi nama lengkap
        if (fullName.isEmpty()) {
            etFullName.setError("Nama lengkap harus diisi");
            return false;
        }

        // Validasi email
        if (email.isEmpty()) {
            etEmail.setError("Email harus diisi");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Format email tidak valid");
            return false;
        }

        // Validasi password
        if (password.isEmpty()) {
            etPassword.setError("Password harus diisi");
            return false;
        }
        if (password.length() < 6) {
            etPassword.setError("Password minimal 6 karakter");
            return false;
        }

        // Validasi konfirmasi password
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Password tidak cocok");
            return false;
        }

        return true; // Semua input valid
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
