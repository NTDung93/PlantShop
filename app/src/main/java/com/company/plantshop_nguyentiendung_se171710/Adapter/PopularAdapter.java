package com.company.plantshop_nguyentiendung_se171710.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.company.plantshop_nguyentiendung_se171710.Model.ProductDomain;
import com.company.plantshop_nguyentiendung_se171710.databinding.ViewholderPopularBinding;

import java.util.ArrayList;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.Viewholder> {
    private final ArrayList<ProductDomain> items;
    private Context context;

    public PopularAdapter(ArrayList<ProductDomain> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderPopularBinding binding = ViewholderPopularBinding.inflate(LayoutInflater.from(context), parent, false);
        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        final ProductDomain product = items.get(position);

        bindProductDetails(holder, product);
        bindProductImage(holder, product.getPicUrl().get(0));

        holder.itemView.setOnClickListener(v -> openDetailActivity(product));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void bindProductDetails(Viewholder holder, ProductDomain product) {
        holder.binding.titleTxt.setText(product.getTitle());
        holder.binding.reviewTxt.setText(String.valueOf(product.getReview()));
        holder.binding.priceTxt.setText(String.format("$%s", product.getPrice()));
        holder.binding.ratingTxt.setText(String.format("(%s)", product.getRating()));
        holder.binding.oldPriceTxt.setText(String.format("$%s", product.getOldPrice()));
        holder.binding.oldPriceTxt.setPaintFlags(holder.binding.oldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.binding.ratingBar.setRating((float) product.getRating());
    }

    private void bindProductImage(Viewholder holder, String imageUrl) {
        Glide.with(context)
                .load(imageUrl)
                .transform(new CenterCrop())
                .into(holder.binding.pic);
    }

    private void openDetailActivity(ProductDomain product) {
//        Intent intent = new Intent(context, DetailActivity.class);
//        intent.putExtra("object", product);
//        context.startActivity(intent);
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        final ViewholderPopularBinding binding;

        public Viewholder(ViewholderPopularBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
