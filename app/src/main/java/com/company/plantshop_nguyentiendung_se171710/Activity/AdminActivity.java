package com.company.plantshop_nguyentiendung_se171710.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;

import com.company.plantshop_nguyentiendung_se171710.Adapter.PopularAdapter;
import com.company.plantshop_nguyentiendung_se171710.Model.ProductDomain;
import com.company.plantshop_nguyentiendung_se171710.R;
import com.company.plantshop_nguyentiendung_se171710.databinding.ActivityAdminBinding;
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
    private DatabaseReference myref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        myref = database.getReference("Items");

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

        String REQUIRE = "Required";

        if (isUpdate && product != null) {
            etName.setText(product.getTitle());
            etDescription.setText(product.getDescription());
            etPrice.setText(String.valueOf(product.getPrice()));
            etOldPrice.setText(String.valueOf(product.getOldPrice()));
            etReview.setText(String.valueOf(product.getReview()));
            etRating.setText(String.valueOf(product.getRating()));
            if (product.getPicUrl() != null && !product.getPicUrl().isEmpty()) {
                etImageUrl.setText(product.getPicUrl().get(0));
            }
        }

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView customTitle = new TextView(this);
        customTitle.setText(isUpdate ? "Update Product" : "Add New Product");
        customTitle.setPadding(20, 20, 20, 0);
        customTitle.setTextSize(20);
        customTitle.setTextColor(getResources().getColor(R.color.darkGreen));
        customTitle.setGravity(Gravity.CENTER);

        builder.setCustomTitle(customTitle)
                .setView(dialogView)
                .setPositiveButton(isUpdate ? "Update" : "Add", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        // Customize button colors
        dialog.setOnShowListener(dialogInterface -> {
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            negativeButton.setTextColor(getResources().getColor(R.color.darkGreen));
        });

        dialog.show();

        Button savedButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        savedButton.setTextColor(getResources().getColor(R.color.darkGreen));
        savedButton.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String oldPriceStr = etOldPrice.getText().toString().trim();
            String reviewStr = etReview.getText().toString().trim();
            String ratingStr = etRating.getText().toString().trim();
            String imageUrl = etImageUrl.getText().toString().trim();

            // Validate inputs
            if (!name.isEmpty() && !description.isEmpty() && !priceStr.isEmpty() &&
                    !oldPriceStr.isEmpty() && !reviewStr.isEmpty() &&
                    !ratingStr.isEmpty() && !imageUrl.isEmpty()) {

                try {
                    double price = Double.parseDouble(priceStr);
                    double oldPrice = Double.parseDouble(oldPriceStr);
                    int review = Integer.parseInt(reviewStr);
                    double rating = Double.parseDouble(ratingStr);

                    if (isUpdate && product != null) {
                        String oldName = product.getTitle();
                        String oldDescription = product.getDescription();

                        product.setTitle(name);
                        product.setDescription(description);
                        product.setPrice(price);
                        product.setOldPrice(oldPrice);
                        product.setReview(review);
                        product.setRating(rating);
                        ArrayList<String> picUrl = new ArrayList<>();
                        picUrl.add(imageUrl);
                        product.setPicUrl(picUrl);

                        myref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot issue : snapshot.getChildren()) {
                                    ProductDomain productDomain = issue.getValue(ProductDomain.class);
                                    if (productDomain != null && productDomain.getTitle().equals(oldName) && productDomain.getDescription().equals(oldDescription)) {
                                        myref.child(issue.getKey()).setValue(product);
                                        int index = productList.indexOf(product);
                                        if (index != -1) {
                                            productList.set(index, product);
                                            popularAdapter.notifyItemChanged(index);
                                        }
                                        dialog.dismiss();
                                        Toast.makeText(AdminActivity.this, "Product updated successfully.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        ProductDomain newProduct = new ProductDomain();
                        newProduct.setTitle(name);
                        newProduct.setDescription(description);
                        newProduct.setPrice(price);
                        newProduct.setOldPrice(oldPrice);
                        newProduct.setReview(review);
                        newProduct.setRating(rating);
                        ArrayList<String> picUrl = new ArrayList<>();
                        picUrl.add(imageUrl);
                        newProduct.setPicUrl(picUrl);

                        myref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                long count = snapshot.getChildrenCount();
                                String newId = String.valueOf(count);

                                myref.child(newId).setValue(newProduct)
                                        .addOnSuccessListener(aVoid -> {
                                            productList.add(newProduct);
                                            popularAdapter.notifyItemInserted(productList.size() - 1);
                                            dialog.dismiss();
                                            Toast.makeText(AdminActivity.this, "Product added successfully.", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(AdminActivity.this, "Addition failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(AdminActivity.this, "Failed to retrieve item count.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(AdminActivity.this, "Please enter valid numbers for price, old price, review, and rating.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Set error messages for empty fields
                if (name.isEmpty()) etName.setError(REQUIRE);
                if (description.isEmpty()) etDescription.setError(REQUIRE);
                if (priceStr.isEmpty()) etPrice.setError(REQUIRE);
                if (oldPriceStr.isEmpty()) etOldPrice.setError(REQUIRE);
                if (reviewStr.isEmpty()) etReview.setError(REQUIRE);
                if (ratingStr.isEmpty()) etRating.setError(REQUIRE);
                if (imageUrl.isEmpty()) etImageUrl.setError(REQUIRE);
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

        productList = new ArrayList<>();

        popularAdapter = new PopularAdapter((ArrayList<ProductDomain>) productList, this, true);

        binding.recyclerViewPopular.setLayoutManager(new GridLayoutManager(AdminActivity.this, 2));
        binding.recyclerViewPopular.setAdapter(popularAdapter);
        binding.recyclerViewPopular.setNestedScrollingEnabled(true);

        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        ProductDomain product = issue.getValue(ProductDomain.class);
                        if (product != null) {
                            productList.add(product);
                        }
                    }
                    popularAdapter.notifyDataSetChanged();
                }
                binding.progressBarPopular.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressBarPopular.setVisibility(View.GONE);
                Toast.makeText(AdminActivity.this, "Failed to load products.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showDeleteDialog(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete plant")
                .setMessage("Are you sure you want to delete this plant ?")
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteItemInFirebase(position);
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    public void deleteItemInFirebase(int position) {
        ProductDomain deletedProduct = productList.get(position);
        myref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot issue : snapshot.getChildren()) {
                    ProductDomain productDomain = issue.getValue(ProductDomain.class);
                    if (productDomain != null && productDomain.getTitle().equals(deletedProduct.getTitle()) && productDomain.getDescription().equals(deletedProduct.getDescription())) {
                        myref.child(issue.getKey()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        productList.remove(position);
        popularAdapter.notifyDataSetChanged();
    }
}