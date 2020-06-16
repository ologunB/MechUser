package com.mechanics.mechapp.customer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mechanics.mechapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ServiceItemAdapter extends RecyclerView.Adapter<ServiceItemAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder{

        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private List<MechanicModel> listModel;
    private Context context;

    ServiceItemAdapter(List<MechanicModel> listModel, Context context) {
        this.listModel = listModel;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.service_item_viewholder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MechanicModel currentModel = listModel.get(position);

        TextView driverNameText = holder.itemView.findViewById(R.id.service_item_vh_driver_name);
        TextView driverNumberText= holder.itemView.findViewById(R.id.service_item_vh_number_text);
        ImageView driverImage = holder.itemView.findViewById(R.id.service_item_vh_image);

        driverNameText.setText(currentModel.getCompanyName());
        driverNumberText.setText(currentModel.getMechanicPhoneNumber());

        if(currentModel.getImageUrl().isEmpty()){
            driverImage.setImageResource(R.drawable.engineer);
        }else {
            Picasso.get().load(currentModel.getImageUrl()).placeholder(R.drawable.engineer).into(driverImage);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(context, CustomerViewMechProfile.class);
                String[] b = currentModel.getCategoryList().toArray(new String[0]);

                String[] a = {currentModel.getImageUrl(), currentModel.getCompanyName(), currentModel.getDescription(),
                         currentModel.getStreetName(), currentModel.getLocality(), currentModel.getCity(),
                         currentModel.getMechanicPhoneNumber(), currentModel.getJobs_done(), currentModel.getRating(),
                         currentModel.getReview(), currentModel.getMechUID()};

                in.putExtra("map", a);
                in.putExtra("category", b);

                context.startActivity(in);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listModel.size();
    }
}