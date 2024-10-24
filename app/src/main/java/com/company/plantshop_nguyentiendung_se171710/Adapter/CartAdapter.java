package com.company.plantshop_nguyentiendung_se171710.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.company.plantshop_nguyentiendung_se171710.Model.ProductDomain;
import com.company.plantshop_nguyentiendung_se171710.Utils.ChangeNumberItemsListener;
import com.company.plantshop_nguyentiendung_se171710.Utils.ManagmentCart;
import com.company.plantshop_nguyentiendung_se171710.databinding.ViewholderCartBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.Viewholder> {
    ArrayList<ProductDomain> listItemSelected;
    ChangeNumberItemsListener changeNumberItemsListener;
    private ManagmentCart managmentCart;

    public CartAdapter(ArrayList<ProductDomain> listItemSelected, Context context, ChangeNumberItemsListener changeNumberItemsListener) {
        this.listItemSelected = listItemSelected;
        this.changeNumberItemsListener = changeNumberItemsListener;
        managmentCart = new ManagmentCart(context);
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderCartBinding binding = ViewholderCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new Viewholder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.binding.titleTxt.setText(listItemSelected.get(position).getTitle());

        holder.binding.feeEachItem.setText(formatter.format(listItemSelected.get(position).getPrice()) + " VND");
        double totalItemPrice = listItemSelected.get(position).getNumberInCart() * listItemSelected.get(position).getPrice();
        holder.binding.totalEachItem.setText(formatter.format(Math.round(totalItemPrice)) + " VND");
        holder.binding.numberItemTxt.setText(String.valueOf(listItemSelected.get(position).getNumberInCart()));

        Glide.with(holder.itemView.getContext())
                .load(listItemSelected.get(position).getPicUrl().get(0))
                .apply(RequestOptions.centerCropTransform())
                .into(holder.binding.pic);

        holder.binding.plusCartBtn.setOnClickListener(v -> managmentCart.plusItem(listItemSelected, position, () -> {
            notifyDataSetChanged();
            changeNumberItemsListener.changed();
        }));

        holder.binding.minusCartBtn.setOnClickListener(v -> managmentCart.minusItem(listItemSelected, position, () -> {
            notifyDataSetChanged();
            changeNumberItemsListener.changed();
        }));
    }

    @Override
    public int getItemCount() {
        return listItemSelected.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ViewholderCartBinding binding;

        public Viewholder(ViewholderCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
