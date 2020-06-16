package com.mechanics.mechapp.mechanic;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mechanics.mechapp.R;
import com.mechanics.mechapp.customer.SharedPreferencesUtil;

public class DrawerMechHomeFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((MechMainActivity) getActivity()).getSupportActionBar().setTitle("Home");
        super.onCreate(savedInstanceState);
    }
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_mech_home, container, false);

        if (getContext() != null){
            final SharedPreferencesUtil util = new SharedPreferencesUtil(getContext());
            if (!util.hasUserClickedYes()){
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("By using this app, you agree to it's terms and conditions");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        util.clickYes();
                    }
                });
                builder.setNegativeButton("VIEW MORE",  new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        util.clickYes();
                        Uri termsAndConditionsUri = Uri.parse("https://fabat.com.ng/TermsAndCondition.pdf");
                        Intent intent = new Intent(Intent.ACTION_VIEW, termsAndConditionsUri);
                        startActivity(intent);
                    }
                });
                builder.setTitle("Terms And Conditions Agreement");
                AlertDialog dialog  = builder.create();
                dialog.show();
            }
        }

        final TextView a = view.findViewById(R.id.AJob);
        final TextView b = view.findViewById(R.id.AAmount);
        final TextView c = view.findViewById(R.id.BJob);
        final TextView d = view.findViewById(R.id.BAmount);
        final TextView e = view.findViewById(R.id.cashDebt);
        final TextView f = view.findViewById(R.id.CAmount);
        final TextView g = view.findViewById(R.id.payRequest);
        final TextView h = view.findViewById(R.id.DAmount);
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("All Jobs Collection").child(uid);

        firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                String a_ = dataSnapshot1.child("Total Job").getValue(String.class);
                String b_ = dataSnapshot1.child("Total Amount").getValue(String.class);
                String c_ = dataSnapshot1.child("Pending Job").getValue(String.class);
                String d_ = dataSnapshot1.child("Pending Amount").getValue(String.class);
                String g_ = dataSnapshot1.child("Payment Request").getValue(String.class);
                String h_ = dataSnapshot1.child("Pay pending Amount").getValue(String.class);
                String e_ = dataSnapshot1.child("Cash Payment Debt").getValue(String.class);
                String f_ = dataSnapshot1.child("Completed Amount").getValue(String.class);

                a.setText(a_);
                b.setText(String.format("₦ %s", b_));
                c.setText(c_);
                d.setText(String.format("₦ %s", d_));
                e.setText(String.format("₦ %s", e_));
                f.setText(String.format("₦ %s", f_));
                g.setText(String.format("₦ %s", g_));
                h.setText(String.format("₦ %s", h_));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }
}
