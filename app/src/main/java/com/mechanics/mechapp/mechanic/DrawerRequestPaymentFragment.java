package com.mechanics.mechapp.mechanic;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mechanics.mechapp.R;

import java.util.HashMap;
import java.util.Map;

import static com.mechanics.mechapp.RandomStringHelper.presentTimeString;

public class DrawerRequestPaymentFragment extends Fragment {
    private EditText theAMount;
    private TextView myBalance, payPending, charge;
    private String a, b,    ppA , pR;
    private double  balanceAmount, chargeVal;
    private DatabaseReference databaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((MechMainActivity) getActivity()).getSupportActionBar().setTitle("Request Payment");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Getting Data...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        View view = inflater.inflate(R.layout.request_payment_frag, container, false);

        myBalance = view.findViewById(R.id.myBalance);
        payPending = view.findViewById(R.id.payPending);
        charge = view.findViewById(R.id.ppCharge);

        Button requestButton = view.findViewById(R.id.requestBtn);
        theAMount = view.findViewById(R.id.requestAmountField);

        databaseReference.child("All Jobs Collection").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                        a = dataSnapshot1.child("Pay pending Amount").getValue(String.class);
                        b = dataSnapshot1.child("Payment Request").getValue(String.class);

                        balanceAmount = Double.parseDouble(a) * 4 / 5;
                        chargeVal = Double.parseDouble(a) / 5;
                        payPending.setText(String.format("Pay Pending: ₦ %s", a));
                        myBalance.setText(String.format("Balance: ₦ %s", balanceAmount));
                        charge.setText(String.format("Charge: ₦ %s", chargeVal));

                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String givenA = theAMount.getText().toString();
                if (givenA.isEmpty()) {
                    Toast.makeText(getContext(), "Amount cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (givenA.length() < 3) {
                    Toast.makeText(getContext(), "You can't withdraw less than a thousand naira", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Double.parseDouble(givenA) > balanceAmount) {
                    Toast.makeText(getContext(), "You can't withdraw more than the balance", Toast.LENGTH_SHORT).show();
                    return;
                }

                databaseReference.child("All Jobs Collection").child(uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                a = dataSnapshot1.child("Pay pending Amount").getValue(String.class);
                                b = dataSnapshot1.child("Payment Request").getValue(String.class);

                                double _amnt = Integer.parseInt(givenA) * 1.25;
                                ppA = String.valueOf(Double.parseDouble(a) - Double.parseDouble(String.valueOf(_amnt)));
                                pR = String.valueOf(Double.parseDouble(b) + Double.parseDouble(givenA));

                                final Map<String, Object> allJobs = new HashMap<>();
                                allJobs.put("Pay pending Amount", ppA);
                                allJobs.put("Payment Request", pR);

                                Map<String, String> pRequest = new HashMap<>();
                                pRequest.put("amount", pR);
                                pRequest.put("uid", uid);
                                pRequest.put("date", presentTimeString());

                                progressDialog.setMessage("Requesting payment...");
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();
                                databaseReference.child("Payment Request").child("Pending").child(uid).setValue(pRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        databaseReference.child("All Jobs Collection").child(uid).updateChildren(allJobs).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                a = ppA;
                                                b = pR;

                                                balanceAmount = Double.parseDouble(a) * 4 / 5;
                                                chargeVal = Double.parseDouble(a) / 5;
                                                myBalance.setText(String.format("Balance: ₦%s", balanceAmount));
                                                charge.setText(String.format("Charge: ₦%s", chargeVal));
                                                payPending.setText(String.format("Balance: ₦%s", ppA));
                                                theAMount.setText("");
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }
                                });
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });



            }
        });
        return view;
    }
}