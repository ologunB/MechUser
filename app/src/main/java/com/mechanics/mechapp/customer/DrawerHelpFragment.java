package com.mechanics.mechapp.customer;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mechanics.mechapp.HelpFragmentAdapter;
import com.mechanics.mechapp.R;

import java.util.ArrayList;

public class DrawerHelpFragment extends Fragment {
    private ArrayList<Help_and_NotificationModel> arr;
    private ProgressDialog progressDialog;
    private HelpFragmentAdapter adapter;
    private ListView lv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Help");
         super.onCreate(savedInstanceState);
    }

    public boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Help Collection").child("Customer");

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Help Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(!isOnline()){
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }

        View view = inflater.inflate(R.layout.content_help, container, false);
        lv = view.findViewById(R.id.help_listView);
        final LinearLayout empty_list = view.findViewById(R.id.notifi_frag_no_card_layout);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arr = new ArrayList<>();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {

                    String t1 = dataSnapshot1.child("help_message").getValue(String.class);
                    String t2 = dataSnapshot1.child("help_time").getValue(String.class);

                    arr.add(new Help_and_NotificationModel(t1, t2));
                }
                progressDialog.dismiss();

                if(arr.size() >0) {
                    adapter = new HelpFragmentAdapter(getContext(), arr, R.drawable.ic_sync_black_24dp);
                    lv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }else {
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
}