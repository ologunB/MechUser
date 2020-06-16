package com.mechanics.mechapp.customer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mechanics.mechapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DrawerMyGarageFragment extends Fragment {
    private DatabaseReference databaseReference;
    private ArrayList<CarRecyclerModel> arr;
    private ArrayList<String> keyArray;
    private ListView lv;
    private MyGarageAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("My Garage");
        super.onCreate(savedInstanceState);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_garage_fragment, container, false);

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Getting Cars...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (!isOnline()) {
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Car Collection").child(uid);
        LinearLayout addCarButton = view.findViewById(R.id.garage_frag_add_car_layout);

        addCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getContext(), AddCarActivity.class);
                in.putExtra("title", "ADD CAR");
                startActivity(in);
            }
        });

        final LinearLayout empty_list = view.findViewById(R.id.garage_frag_no_cars_layout);

        lv = view.findViewById(R.id.garage_frag_recycler);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arr = new ArrayList<>();
                keyArray = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    String t1 = dataSnapshot1.child("car_brand").getValue(String.class);
                    String t2 = dataSnapshot1.child("car_model").getValue(String.class);
                    String t3 = dataSnapshot1.child("car_date").getValue(String.class);
                    String t4 = dataSnapshot1.child("car_num").getValue(String.class);
                    String t5 = dataSnapshot1.child("car_image").getValue(String.class);

                    arr.add(new CarRecyclerModel(t1, t2, t3, t4, t5));
                    keyArray.add(dataSnapshot1.getKey());
                }
                progressDialog.dismiss();

                if (arr.size() > 0) {
                    adapter = new MyGarageAdapter(getContext(), arr);
                    lv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    lv.setVisibility(View.INVISIBLE);
                    empty_list.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

    public class MyGarageAdapter extends BaseAdapter {
        private final Context context;
        private final ArrayList<CarRecyclerModel> values;

        private MyGarageAdapter(Context context, ArrayList<CarRecyclerModel> values) {
            this.context = context;
            this.values = values;
        }

        @Override
        public int getCount() {
            return values.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.cars_viewholder, parent, false);
            TextView carBrand = rowView.findViewById(R.id.sd_car_brand);
            TextView carModel = rowView.findViewById(R.id.sd_car_model);
            TextView carDate = rowView.findViewById(R.id.sd_car_year);
            ImageView carImage = rowView.findViewById(R.id.sd_car_image);
            ImageView close = rowView.findViewById(R.id.remove_car);

            carBrand.setText(values.get(position).getCar_brand());
            carModel.setText(values.get(position).getCar_model());
            carDate.setText(values.get(position).getCar_date());

            if (values.get(position).getCar_image().isEmpty()) {
                carImage.setImageResource(R.drawable.ic_car);
            } else {
                Picasso.get().load(values.get(position).getCar_image()).placeholder(R.drawable.ic_car).into(carImage);
            }

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Confirmation");
                    builder.setMessage("Are you sure you want to delete the car?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            databaseReference.child(keyArray.get(position)).removeValue();
                            adapter.notifyDataSetChanged();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });
            return rowView;
        }
    }
}