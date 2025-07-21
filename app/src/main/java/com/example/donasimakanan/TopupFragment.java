package com.example.donasimakanan;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.donasimakanan.manager.UserManager;
import com.google.android.material.textfield.TextInputEditText;


public class TopupFragment extends Fragment {
    private UserManager userManager;

    
    public TopupFragment() {
    }

    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userManager = new UserManager(getContext());
    }

    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout untuk fragment ini
        View view = inflater.inflate(R.layout.fragment_topup, container, false);

        // Inisialisasi komponen UI dari layout
        Button btnConfirmTopup = view.findViewById(R.id.btn_confirm_topup);
        TextInputEditText topupAmount = view.findViewById(R.id.et_topup_amount);

        // Menetapkan listener untuk tombol konfirmasi
        btnConfirmTopup.setOnClickListener(v -> {
            String amount = topupAmount.getText().toString().trim();

            // Validasi untuk memastikan input tidak kosong
            if (amount.isEmpty()) {
                Toast.makeText(getContext(), "Jumlah top-up tidak boleh kosong", Toast.LENGTH_SHORT).show();
            } else {
                int parsedAmount = Integer.parseInt(amount);
                // Validasi untuk memastikan jumlah lebih besar dari 0
                if (parsedAmount <= 0) {
                    Toast.makeText(getContext(), "Jumlah top-up harus lebih dari 0", Toast.LENGTH_SHORT).show();
                    return; // Hentikan proses jika tidak valid
                }

                // Memanggil UserManager untuk menambahkan saldo ke akun pengguna
                userManager.addBalance(parsedAmount);

                // Memberikan feedback kepada pengguna bahwa top-up berhasil
                Toast.makeText(getContext(), "Top-up berhasil sebesar Rp " + parsedAmount, Toast.LENGTH_SHORT).show();

                // Kembali ke fragment sebelumnya setelah top-up berhasil
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        return view;
    }
}
