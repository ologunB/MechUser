package com.mechanics.mechapp.customer;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.mechanics.mechapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NearByAdapter extends RecyclerView.Adapter<NearByAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder{

        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private List<MechanicModel> listModel;
    private Context context;
    private LatLng userLocation;

    NearByAdapter(List<MechanicModel> listModel, Context context, LatLng userLocation) {
        this.listModel = listModel;
        this.context = context;
        this.userLocation = userLocation;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.eachmechanic_viewholder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MechanicModel currentModel = listModel.get(position);

        TextView driverNameText = holder.itemView.findViewById(R.id.nearby_mechanic_name);
        TextView driverNumberText= holder.itemView.findViewById(R.id.nearby_mechanic_number);
        TextView driverDistanceText = holder.itemView.findViewById(R.id.how_far_mech);

        ImageView driverImage = holder.itemView.findViewById(R.id.nearby_mechanic_image);

        driverNameText.setText(currentModel.getCompanyName());
        driverNumberText.setText(currentModel.getMechanicPhoneNumber());
        Picasso.get().load(currentModel.getImageUrl()).placeholder(R.drawable.engineer).into(driverImage);

        float[] results = new float[1];
        Location.distanceBetween(userLocation.latitude, userLocation.longitude, currentModel.getLatitude(), currentModel.getLongitude(), results);
        int distanceInKm = (int)(results[0]) / 1000;
        driverDistanceText.setText(String.valueOf(distanceInKm));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(context, CustomerViewMechProfile.class);

                String[] a = {currentModel.getImageUrl(), currentModel.getCompanyName(), currentModel.getDescription(),
                        currentModel.getStreetName(), currentModel.getLocality(), currentModel.getCity(),
                        currentModel.getMechanicPhoneNumber(), currentModel.getJobs_done(), currentModel.getRating(),
                        currentModel.getReview(), currentModel.getMechUID()};
                String[] b = new String[currentModel.getCategoryList().size()];
                b = currentModel.getCategoryList().toArray(b);

                in.putExtra("map", a);
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

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