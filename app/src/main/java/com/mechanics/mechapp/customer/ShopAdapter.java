package com.mechanics.mechapp.customer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mechanics.mechapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.MyViewHolder> {

    private Context context;
    private final ArrayList<ShopItemModel> mData;

    ShopAdapter(Context context, ArrayList<ShopItemModel> mArrayL) {
        this.context = context;
        this.mData = mArrayL;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.shop_item_viewholder, parent, false);
        MyViewHolder myViewHolder;
        myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.shopItemName.setText(mData.get(position).getShop_item_name());
        holder.shopItemSeller.setText(mData.get(position).getShop_item_seller());
        holder.shopItemPrice.setText(String.format("₦%s", mData.get(position).getShop_item_price()));

        if (mData.get(position).getShop_item_image() == null) {
            holder.shopItemImage.setImageResource(R.drawable.placeholder);
        } else {
            Picasso.get().load(mData.get(position).getShop_item_image().get(0)).placeholder(R.drawable.placeholder).into(holder.shopItemImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(context, ShopItemActivity.class);

                String[] stockArr = {mData.get(position).getShop_item_name(),
                        mData.get(position).getShop_item_price(),
                        mData.get(position).getShop_item_seller(),
                        mData.get(position).getShop_item_email(),
                        mData.get(position).getShop_item_phoneNo(),
                        mData.get(position).getShop_item_descrpt(),
                         mData.get(position).getShop_item_ID()
                };

                String[] b = new String[mData.get(position).getShop_item_image().size()];
                b = mData.get(position).getShop_item_image().toArray(b);

                in.putExtra("arr", stockArr);
                in.putExtra("images", b);

                context.startActivity(in);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView shopItemName, shopItemSeller, shopItemPrice;

        ImageView shopItemImage;

        private MyViewHolder(View itemView) {

            super(itemView);
            shopItemName = itemView.findViewById(R.id.shop_item_name);
            shopItemPrice = itemView.findViewById(R.id.shop_item_price);
            shopItemSeller = itemView.findViewById(R.id.shop_item_seller);
            shopItemImage = itemView.findViewById(R.id.shop_item_image);
        }
    }
}