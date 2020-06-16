package com.mechanics.mechapp.customer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mechanics.mechapp.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.MyViewHolder> {

    private Context context;
    private final List<Models> mData;

    ServicesAdapter(Context context, List<Models> mArrayL) {
        this.context = context;
        this.mData = mArrayL;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.services_viewholder, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.text.setText(mData.get(position).getServiceName());
        holder.image.setImageResource(mData.get(position).getServiceLogo());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(context, ServicesItemActivity.class);
                in.putExtra("pos22", String.valueOf(position));

                context.startActivity(in);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        ImageView image;

        private MyViewHolder(View itemView) {

            super(itemView);
            text =  itemView.findViewById(R.id.service_text);
            image =  itemView.findViewById(R.id.service_image);
        }
    }

}