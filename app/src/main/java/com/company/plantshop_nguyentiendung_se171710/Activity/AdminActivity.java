package com.company.plantshop_nguyentiendung_se171710.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.company.plantshop_nguyentiendung_se171710.Adapter.PopularAdapter;
import com.company.plantshop_nguyentiendung_se171710.Model.ProductDomain;
import com.company.plantshop_nguyentiendung_se171710.R;
import com.company.plantshop_nguyentiendung_se171710.databinding.ActivityAdminBinding;
import com.company.plantshop_nguyentiendung_se171710.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminActivity extends BaseActivity {
    FirebaseAuth auth;
    Button loginOrLogoutBtn;
    TextView textView;
    FirebaseUser user;
    private ActivityAdminBinding binding;
    private PopularAdapter popularAdapter;
    private List<ProductDomain> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeFirebaseAuth();
        setupViews();
        displayUserInfo();
        setupLoginOrLogoutButton();

        initPopular();

        binding.addProductBtn.setOnClickListener(view -> showAddUpdateDialog(null, false));
    }

    public void showAddUpdateDialog(ProductDomain product, boolean isUpdate) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.popup_add_product, null);

        EditText etName = dialogView.findViewById(R.id.etTitleAdmin);
        EditText etDescription = dialogView.findViewById(R.id.etDescriptionAdmin);
        EditText etPrice = dialogView.findViewById(R.id.etPriceAdmin);
        EditText etOldPrice = dialogView.findViewById(R.id.etOldPriceAdmin);
        EditText etReview = dialogView.findViewById(R.id.etReviewAdmin);
        EditText etRating = dialogView.findViewById(R.id.etRatingAdmin);
        EditText etImageUrl = dialogView.findViewById(R.id.etImageUrlAdmin);

        String REQUIRE = "Require";

        //Map field
        if (isUpdate && product != null) {
            etName.setText(product.getTitle());
            etDescription.setText(product.getDescription());
            etPrice.setText(String.valueOf(product.getPrice()));
            etOldPrice.setText(String.valueOf(product.getOldPrice()));
            etReview.setText(String.valueOf(product.getReview()));
            etRating.setText(String.valueOf(product.getRating()));
            etImageUrl.setText(product.getPicUrl().get(0));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        TextView customTitle = new TextView(this);
        customTitle.setText(isUpdate ? "Update Product" : "Add New Product");
        customTitle.setPadding(20, 20, 20, 0);
        customTitle.setTextSize(20);  // Set desired text size
        customTitle.setTextColor(getResources().getColor(R.color.darkGreen));
        customTitle.setGravity(Gravity.CENTER);

        builder.setCustomTitle(customTitle)
                .setView(dialogView)
                .setPositiveButton(isUpdate ? "Update" : "Add", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            negativeButton.setTextColor(getResources().getColor(R.color.darkGreen));
        });

        dialog.show();

        Button savedButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        savedButton.setTextColor(getResources().getColor(R.color.darkGreen));
        savedButton.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String description = etDescription.getText().toString();
            String price = etPrice.getText().toString();
            String oldPrice = etOldPrice.getText().toString();
            String review = etReview.getText().toString();
            String rating = etRating.getText().toString();
            String imageUrl = etImageUrl.getText().toString();

            if (!name.isEmpty() && !description.isEmpty() && !price.isEmpty() && !oldPrice.isEmpty() && !review.isEmpty() && !rating.isEmpty() && !imageUrl.isEmpty()) {
                if (isUpdate && product != null) {
                    product.setTitle(name);
                    product.setDescription(description);
                    product.setPrice(Double.parseDouble(price));
                    product.setOldPrice(Double.parseDouble(oldPrice));
                    product.setReview(Integer.parseInt(review));
                    product.setRating(Double.parseDouble(rating));
                    ArrayList<String> picUrl = new ArrayList<>();
                    picUrl.add(imageUrl);
                    product.setPicUrl(picUrl);
                    productList.set(productList.indexOf(product), product);
//                    database.updateProduct(product);
                } else {
//                    database.addProduct(name, description, Double.parseDouble(price), Double.parseDouble(oldPrice), Integer.parseInt(review), Double.parseDouble(rating), imageUrl);
//                    ProductDomain newProduct = new ProductDomain(name, description, Double.parseDouble(price), Double.parseDouble(oldPrice), Integer.parseInt(review), Double.parseDouble(rating), picUrl);
//                    productList.add(newProduct);
                }
                popularAdapter.notifyDataSetChanged();
                dialog.dismiss();
            } else {
                etName.setError(REQUIRE);
                etDescription.setError(REQUIRE);
                etPrice.setError(REQUIRE);
                etOldPrice.setError(REQUIRE);
                etReview.setError(REQUIRE);
                etRating.setError(REQUIRE);
                etImageUrl.setError(REQUIRE);
            }
        });
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
                        binding.recyclerViewPopular.setLayoutManager(new GridLayoutManager(AdminActivity.this, 2));
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
}