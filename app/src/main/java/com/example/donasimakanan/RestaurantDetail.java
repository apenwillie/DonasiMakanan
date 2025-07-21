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

import com.example.donasimakanan.manager.FoodManager;
import com.example.donasimakanan.manager.RestaurantManager;
import com.example.donasimakanan.model.Food;
import com.example.donasimakanan.model.Restaurant;

import java.util.List;


public class RestaurantDetail extends Fragment {
    // Kunci untuk menyimpan dan mengambil ID restoran dari Bundle arguments.
    private static final String ARG_RESTAURANT_ID = "restaurant_id";
    private String restaurantId;

    // Manager untuk mengelola data restoran dan makanan
    private RestaurantManager restaurantManager = new RestaurantManager();
    private FoodManager foodManager = new FoodManager();

    
    public static RestaurantDetail newInstance(String restaurantId) {
        RestaurantDetail fragment = new RestaurantDetail();
        Bundle args = new Bundle();
        args.putString(ARG_RESTAURANT_ID, restaurantId);
        fragment.setArguments(args);
        return fragment;
    }

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            restaurantId = getArguments().getString(ARG_RESTAURANT_ID);
        }
    }

    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout untuk fragment ini
        View view = inflater.inflate(R.layout.fragment_restaurant_detail, container, false);

        // Inisialisasi komponen UI dari layout
        TextView tvName = view.findViewById(R.id.tv_restaurant_name);
        TextView tvAddress = view.findViewById(R.id.tv_restaurant_address);
        TextView tvDescription = view.findViewById(R.id.tv_restaurant_description);
        RecyclerView rvFoodList = view.findViewById(R.id.rv_food_list);
        rvFoodList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Mengambil data detail restoran berdasarkan ID yang diterima
        Restaurant restaurant = restaurantManager.getRestaurantById(restaurantId);

        // Memastikan objek restoran tidak null sebelum digunakan
        if (restaurant != null) {
            // Mengisi komponen UI dengan data dari objek restoran
            tvName.setText(restaurant.getName());
            tvAddress.setText(restaurant.getAddress());
            tvDescription.setText(restaurant.getDescription());

            // Mengambil daftar makanan yang terkait dengan restoran ini
            List<Food> foodList = foodManager.getFoodByRestaurantId(restaurantId);
            // Menyiapkan dan mengatur adapter untuk menampilkan daftar makanan
            FoodAdapter adapter = new FoodAdapter(requireContext(), foodList, this.restaurantId);
            rvFoodList.setAdapter(adapter);
        }

        return view;
    }
}
