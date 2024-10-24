package com.company.plantshop_nguyentiendung_se171710.Activity;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import com.company.plantshop_nguyentiendung_se171710.Adapter.SliderAdapter;
import com.company.plantshop_nguyentiendung_se171710.Model.ProductDomain;
import com.company.plantshop_nguyentiendung_se171710.Model.SliderItems;
import com.company.plantshop_nguyentiendung_se171710.Utils.ManagmentCart;
import com.company.plantshop_nguyentiendung_se171710.databinding.ActivityProductDetailBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ProductDetailActivity extends BaseActivity {

    private ActivityProductDetailBinding binding;
    private ProductDomain object;
    private int numberOrder = 1;
    private ManagmentCart managmentCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);

        getBundles();
        initBanners();
//        initSize();
//        initColor();
    }

//    private void initColor() {
//        ArrayList<String> list = new ArrayList<>();
//        list.add("#006fc4");
//        list.add("#daa048");
//        list.add("#398d41");
//        list.add("#0c3c72");
//        list.add("#829db5");
//
//        binding.recyclerColor.setAdapter(new ColorAdapter(list));
//        binding.recyclerColor.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//    }
//
//    private void initSize() {
//        ArrayList<String> list = new ArrayList<>();
//        list.add("S");
//        list.add("M");
//        list.add("L");
//        list.add("XL");
//        list.add("XXL");
//
//        binding.recyclerSize.setAdapter(new SizeAdapter(list));
//        binding.recyclerSize.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//    }

    private void initBanners() {
        ArrayList<SliderItems> sliderItems = new ArrayList<>();
        for (int i = 0; i < object.getPicUrl().size(); i++) {
            sliderItems.add(new SliderItems(object.getPicUrl().get(i)));
        }
        binding.viewpagerSlider.setAdapter(new SliderAdapter(sliderItems, binding.viewpagerSlider));
        binding.viewpagerSlider.setClipToPadding(false);
        binding.viewpagerSlider.setClipChildren(false);
        binding.viewpagerSlider.setOffscreenPageLimit(3);
        binding.viewpagerSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
    }

    private void getBundles() {
        object = (ProductDomain) getIntent().getSerializableExtra("object");

        DecimalFormat formatter = new DecimalFormat("#,###");

        binding.titleTxt.setText(object.getTitle());
        binding.priceTxt.setText(formatter.format(object.getPrice()) + " VND");
        binding.ratingBar.setRating((float) object.getRating());
        binding.ratingTxt.setText(object.getRating() + " Rating");
        binding.descriptionTxt.setText(object.getDescription());

        binding.AddtoCartBtn.setOnClickListener(v -> {
            object.setNumberInCart(numberOrder);
            managmentCart.insertItem(object);
        });

        binding.backBtn.setOnClickListener(v -> finish());
    }
}