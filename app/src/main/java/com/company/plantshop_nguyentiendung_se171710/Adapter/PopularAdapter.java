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
import com.bumptech.glide.request.RequestOptions;
import com.company.plantshop_nguyentiendung_se171710.Activity.AdminActivity;
import com.company.plantshop_nguyentiendung_se171710.Activity.ProductDetailActivity;
import com.company.plantshop_nguyentiendung_se171710.Model.ProductDomain;
import com.company.plantshop_nguyentiendung_se171710.databinding.ViewholderPopularBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.Viewholder> {
    private final ArrayList<ProductDomain> items;
    private Context context;
    private AdminActivity adminActivity;
    private boolean isAdmin = false;

    public PopularAdapter(ArrayList<ProductDomain> items) {
        this.items = items;
    }

    public PopularAdapter(ArrayList<ProductDomain> items, AdminActivity adminActivity, boolean isAdmin) {
        this.items = items;
        this.adminActivity = adminActivity;
        this.isAdmin = isAdmin;
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

        if (isAdmin) {
            holder.itemView.setOnClickListener(v -> adminActivity.showAddUpdateDialog(product, true));
        } else {
            holder.itemView.setOnClickListener(v -> openDetailActivity(product));
        }

        if (isAdmin) {
            holder.itemView.setOnLongClickListener(view -> {
                adminActivity.showDeleteDialog(position);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void bindProductDetails(Viewholder holder, ProductDomain product) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.binding.titleTxt.setText(product.getTitle());
        holder.binding.reviewTxt.setText(String.valueOf(product.getReview()));
        holder.binding.priceTxt.setText(String.format("%sđ", formatter.format(product.getPrice())));
        holder.binding.oldPriceTxt.setText(String.format("%sđ", formatter.format(product.getOldPrice())));
        holder.binding.oldPriceTxt.setPaintFlags(holder.binding.oldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.binding.ratingTxt.setText(String.format("(%s)", product.getRating()));
        holder.binding.ratingBar.setRating((float) product.getRating());
    }

    private void bindProductImage(Viewholder holder, String imageUrl) {
        int cornerRadius = 25;

        Glide.with(context)
                .load(imageUrl)
                .apply(new RequestOptions()
                        .transform(new CenterCrop(),
                                new RoundedCornersTransformation(cornerRadius, 0,
                                        RoundedCornersTransformation.CornerType.TOP)))
                .into(holder.binding.pic);
    }

    private void openDetailActivity(ProductDomain product) {
        Intent intent = new Intent(context, ProductDetailActivity.class);
        intent.putExtra("object", product);
        context.startActivity(intent);
    }

    public static class Viewholder extends RecyclerView.ViewHolder {
        final ViewholderPopularBinding binding;

        public Viewholder(ViewholderPopularBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
