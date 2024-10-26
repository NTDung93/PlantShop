package com.company.plantshop_nguyentiendung_se171710.Activity;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.company.plantshop_nguyentiendung_se171710.Adapter.CategoryAdapter;
import com.company.plantshop_nguyentiendung_se171710.Adapter.PopularAdapter;
import com.company.plantshop_nguyentiendung_se171710.Adapter.SliderAdapter;
import com.company.plantshop_nguyentiendung_se171710.Model.CategoryDomain;
import com.company.plantshop_nguyentiendung_se171710.Model.ProductDomain;
import com.company.plantshop_nguyentiendung_se171710.Model.SliderItems;
import com.company.plantshop_nguyentiendung_se171710.R;
import com.company.plantshop_nguyentiendung_se171710.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends BaseActivity {
    FirebaseAuth auth;
    Button loginOrLogoutBtn;
    TextView textView;
    FirebaseUser user;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeFirebaseAuth();
        setupViews();
        displayUserInfo();
        setupLoginOrLogoutButton();

        initBanner();
        initCategory();
        initPopular();
        bottomNavigation();
    }

    private void initializeFirebaseAuth() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    private void setupViews() {
        loginOrLogoutBtn = binding.btnLogout;
        textView = binding.userDetails;
    }

    private void displayUserInfo() {
        if (user == null) {
            textView.setText(R.string.welcome_to_plant_shop);
            loginOrLogoutBtn.setText(R.string.login_btn);
        } else if (user != null && user.getEmail().contains("admin")) {
            auth.signOut();
            textView.setText(R.string.welcome_to_plant_shop);
            loginOrLogoutBtn.setText(R.string.login_btn);
        } else {
            int index = Objects.requireNonNull(user.getEmail()).indexOf('@');
            String name = user.getEmail().substring(0, index);
            textView.setText(String.format("%s%s", getString(R.string.welcome), name));
            loginOrLogoutBtn.setText(R.string.logout_btn);
        }
    }

    private void setupLoginOrLogoutButton() {
        loginOrLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLoginOrLogout();
            }
        });
    }

    private void handleLoginOrLogout() {
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
            return;
        }
        if (user != null) {
            auth.signOut();
        }
        Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
        startActivity(intent);
        finish();
    }

    private void initBanner() {
        DatabaseReference myRef = database.getReference("Banner");
        binding.progressBarBanner.setVisibility(View.VISIBLE);
        ArrayList<SliderItems> items = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        items.add(issue.getValue(SliderItems.class));
                    }
                    banners(items);
                    binding.progressBarBanner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void banners(ArrayList<SliderItems> items) {
        binding.viewPagerSlider.setAdapter(new SliderAdapter(items, binding.viewPagerSlider));
        binding.viewPagerSlider.setClipToPadding(false);
        binding.viewPagerSlider.setClipChildren(false);
        binding.viewPagerSlider.setOffscreenPageLimit(3);
        binding.viewPagerSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));

        binding.viewPagerSlider.setPageTransformer(compositePageTransformer);

    }

    private void initCategory() {
        DatabaseReference myref = database.getReference("Category");
        binding.progressBarCategories.setVisibility(View.VISIBLE);

        ArrayList<CategoryDomain> items = new ArrayList<>();
        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        items.add(issue.getValue(CategoryDomain.class));
                    }
                    if (!items.isEmpty()) {
                        binding.recyclerViewCategories.setLayoutManager(new LinearLayoutManager(
                                MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        binding.recyclerViewCategories.setAdapter(new CategoryAdapter(items));
                        binding.recyclerViewCategories.setNestedScrollingEnabled(true);
                    }
                    binding.progressBarCategories.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initPopular() {
        DatabaseReference myref = database.getReference("Items");
        binding.progressBarPopular.setVisibility(View.VISIBLE);
        ArrayList<ProductDomain> items = new ArrayList<>();

        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        items.add(issue.getValue(ProductDomain.class));
                    }
                    if (!items.isEmpty()) {
                        binding.recyclerViewPopular.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
                        binding.recyclerViewPopular.setAdapter(new PopularAdapter(items));
                        binding.recyclerViewPopular.setNestedScrollingEnabled(true);
                    }
                    binding.progressBarPopular.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void bottomNavigation() {
        binding.cartBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CartActivity.class)));
        binding.mapBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MapActivity.class)));
        binding.chatBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ChatActivity.class)));
    }
}
