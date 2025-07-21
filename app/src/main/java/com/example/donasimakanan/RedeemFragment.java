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

/**
 * Sebuah Fragment yang bertanggung jawab untuk menampilkan daftar hadiah (Reward)
 * yang dapat ditukarkan oleh pengguna menggunakan poin mereka.
 * Kelas ini mengambil data dari database melalui RewardManager dan menampilkannya
 * dalam sebuah RecyclerView. Logika penukaran hadiah ditangani di dalam RewardAdapter.
 */
public class RedeemFragment extends Fragment {

    private Realm realm;
    private RecyclerView rvRewards;
    private RewardAdapter adapter;
    private RewardManager rewardManager;

    /**
     * Dipanggil saat Fragment pertama kali dibuat.
     * Method ini digunakan untuk inisialisasi awal yang tidak terkait dengan tampilan (View),
     * seperti membuka koneksi database dan menginisialisasi RewardManager.
     *
     * @param savedInstanceState Jika fragment dibuat ulang dari state sebelumnya, ini adalah Bundlenya.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Membuka instance Realm yang akan digunakan selama siklus hidup Fragment ini
        realm = Realm.getDefaultInstance();
        // Menginisialisasi manager yang diperlukan untuk mengambil data hadiah
        rewardManager = new RewardManager(requireContext());
    }

    /**
     * Dipanggil untuk membuat dan mengembalikan hierarki View yang terkait dengan Fragment.
     * Method ini meng-inflate layout XML (R.layout.fragment_redeem) yang akan menjadi
     * tampilan visual dari Fragment ini.
     *
     * @param inflater Objek yang dapat meng-inflate layout XML menjadi objek View.
     * @param container Parent View tempat layout fragment akan disisipkan.
     * @param savedInstanceState Jika non-null, fragment ini dibuat ulang dari state yang disimpan.
     * @return View untuk UI Fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_redeem, container, false);
    }

    /**
     * Dipanggil segera setelah onCreateView() selesai.
     * Method ini adalah tempat yang tepat untuk melakukan inisialisasi akhir pada View,
     * seperti mencari referensi ke RecyclerView, mengambil data hadiah, dan mengatur adapter.
     *
     * @param view View yang dikembalikan oleh onCreateView().
     * @param savedInstanceState Jika non-null, fragment ini dibuat ulang dari state yang disimpan.
     */
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
