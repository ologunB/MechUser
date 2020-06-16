package com.mechanics.mechapp.customer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.mechanics.mechapp.R;

import java.util.ArrayList;
import java.util.List;

public class PartShopFrag extends Fragment {

    private ArrayList<ShopItemModel> arr;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private ShopAdapter adapter;

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_shop, container, false);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Shop Collection");

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Getting Shop Items...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (!isOnline()) {
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }

        recyclerView = view.findViewById(R.id.grid_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<String> keyArr = new ArrayList<>();
                for (DataSnapshot dataSnapshot7 : dataSnapshot.getChildren()) {
                    keyArr.add(dataSnapshot7.getKey());
                }

                arr = new ArrayList<>();
                final GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};

                for (int i = 0; i < keyArr.size(); i++) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.child(keyArr.get(i)).getChildren()) {
                        String t1 = dataSnapshot1.child("shop_item_name").getValue(String.class);
                        String t2 = dataSnapshot1.child("shop_item_price").getValue(String.class) ;
                        String t3 = dataSnapshot1.child("shop_item_seller").getValue(String.class);
                        String t4 = dataSnapshot1.child("shop_item_email").getValue(String.class);
                        String t5 = dataSnapshot1.child("shop_item_phoneNo").getValue(String.class);
                        String t6 = dataSnapshot1.child("shop_item_descrpt").getValue(String.class);
                        List<String>  t7 = dataSnapshot1.child("shop_item_images").getValue(t);
                        String t8 = dataSnapshot1.child("shop_item_ID").getValue(String.class);
                        String t9 = dataSnapshot1.child("shop_item_type").getValue(String.class);

                        if(t9.equals("Part")){
                            arr.add(new ShopItemModel(t1, t2, t3, t4, t5, t6, t7, t8, t9 ));
                        }
                    }
                }

                progressDialog.dismiss();

                if (arr != null) {
                    adapter = new  ShopAdapter(getContext(), arr);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    recyclerView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return view;
    }


}
