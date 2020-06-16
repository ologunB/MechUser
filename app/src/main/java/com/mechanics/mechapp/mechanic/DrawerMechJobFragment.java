package com.mechanics.mechapp.mechanic;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mechanics.mechapp.R;
import com.mechanics.mechapp.customer.JobsRecyclerModel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.mechanics.mechapp.RandomStringHelper.presentTimeString;

public class DrawerMechJobFragment extends Fragment {

    private ArrayList<JobsRecyclerModel> arr;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private JobAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((MechMainActivity) getActivity()).getSupportActionBar().setTitle("Jobs");
        super.onCreate(savedInstanceState);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_shop, container, false);
        final LinearLayout empty_job = view.findViewById(R.id.jobs_frag_no_jobs_layout);

        if (!isOnline()) {
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Getting Jobs...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        recyclerView = view.findViewById(R.id.grid_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference.child("Jobs Collection").child("Mechanic").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        arr = new ArrayList<>();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            String t1 = dataSnapshot1.child("Trans Amount").getValue(String.class);
                            String t2 = dataSnapshot1.child("Trans Time").getValue(String.class);
                            String t3 = dataSnapshot1.child("Car Type").getValue(String.class);
                            String t4 = dataSnapshot1.child("Customer Number").getValue(String.class);
                            String t5 = dataSnapshot1.child("Customer Name").getValue(String.class);
                            String t6 = dataSnapshot1.child("Trans Description").getValue(String.class);
                            String t7 = dataSnapshot1.child("Trans ID").getValue(String.class);
                            String t8 = dataSnapshot1.child("Trans Confirmation").getValue(String.class);
                            String t9 = dataSnapshot1.child("Mech Confirmation").getValue(String.class);
                            String t10 = dataSnapshot1.child("Customer UID").getValue(String.class);
                            String t11 = dataSnapshot1.child("Server Confirmation").getValue(String.class);

                            arr.add(new JobsRecyclerModel(t1, t3, t2, t6, t5, t4, t7, t8, t9, t10, t11));
                        }
                        progressDialog.dismiss();

                        if (!arr.isEmpty()) {
                            adapter = new JobAdapter(getContext(), arr);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            recyclerView.setVisibility(View.INVISIBLE);
                            empty_job.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        return view;
    }

    public class JobAdapter extends RecyclerView.Adapter<JobAdapter.MyViewHolder> {

        private Context context;
        private final ArrayList<JobsRecyclerModel> mData;

        JobAdapter(Context context, ArrayList<JobsRecyclerModel> mArrayL) {
            this.context = context;
            this.mData = mArrayL;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(getLayoutInflater().inflate(R.layout.job_item_viewholder, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
            holder.oppName.setText(mData.get(position).getName());
            holder.thePrice.setText(String.format("₦%s", mData.get(position).getAmount_paid()));
            holder.oppNumber.setText(mData.get(position).getNumber());
            holder.tranxDate.setText(mData.get(position).getTransact_time());

            holder.oppImage.setVisibility(View.GONE);
            if (mData.get(position).getCusConfirmation().equals("Confirmed")
                    && mData.get(position).getMechConfirmation().equals("Confirmed")) {
                holder.confirm.setText("COMPLETED!");
                holder.confirm.setEnabled(false);
                holder.confirm.setBackgroundColor(getResources().getColor(R.color.cyan_dark));
            } else if (mData.get(position).getMechConfirmation().equals("Confirmed")) {
                holder.confirm.setText("PENDING!");
                holder.confirm.setEnabled(false);
                holder.confirm.setBackgroundColor(getResources().getColor(R.color.red_dark));
            }

            holder.confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        final String MechUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        final String uid = mData.get(position).getUID();

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Confirmation");
                        builder.setMessage("Has the customer paid?");
                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onResume();
                            }
                        });

                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                final String transactID = mData.get(position).getTransactionID();

                                String received = "Payment has been confirmed by you. Thanks for using FABAT";

                                String  made= "Your payment has been confirmed by the mechanic. Thanks for using FABAT";

                                final Map<String, Object> sentMessage = new HashMap<>();
                                sentMessage.put("notification_message", made);
                                sentMessage.put("notification_time",  presentTimeString());
                                sentMessage.put("Timestamp",  Calendar.getInstance().getTimeInMillis());

                                final Map<String, Object> receivedMessage = new HashMap<>();
                                receivedMessage.put("notification_message", received);
                                receivedMessage.put("notification_time",  presentTimeString());
                                receivedMessage.put("Timestamp",  Calendar.getInstance().getTimeInMillis());

                                Map<String, Object> valuesToMech = new HashMap<>();
                                valuesToMech.put("Mech Confirmation", "Confirmed");

                                final Map<String, Object> valuesToCustomer = new HashMap<>();
                                valuesToCustomer.put("Mech Confirmation", "Confirmed");


                                databaseReference.child("Jobs Collection").child("Mechanic").child(MechUID).child(transactID).updateChildren(valuesToMech)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressDialog.setMessage("Action 1/3...");
                                                progressDialog.show();
                                                databaseReference.child("Notification Collection").child("Mechanic").child(MechUID).child(transactID).setValue(receivedMessage).
                                                        addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                progressDialog.setMessage("Action 2/3...");
                                                                databaseReference.child("Jobs Collection").child("Customer").child(uid).child(transactID).
                                                                        updateChildren(valuesToCustomer).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        progressDialog.setMessage("Almost done...");
                                                                        //progressDialog.dismiss();
                                                                        databaseReference.child("Notification Collection").child("Customer").child(uid).push().setValue(sentMessage).
                                                                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        progressDialog.setMessage("Finalizing...");

                                                                                        progressDialog.dismiss();
                                                                                        holder.confirm.setText("COMPLETED");
                                                                                        holder.confirm.setBackgroundColor(getResources().getColor(R.color.light_gray));
                                                                                        holder.confirm.setEnabled(false);
                                                                                        onResume();

                                                                                        Toast.makeText(context, "Confirmed!", Toast.LENGTH_LONG).show();
                                                                                    }
                                                                                });
                                                                    }
                                                                });
                                                            }
                                                        });
                                            }
                                        });

                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                    } catch (Exception e) {
                        Toast.makeText(context, "Still getting mechanics value, try again!", Toast.LENGTH_LONG).show();
                    }
                }


            });
        }


        @Override
        public int getItemCount() {
            return mData.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView oppName, tranxDate, thePrice, oppNumber;
            ImageView oppImage;
            Button confirm;

            private MyViewHolder(View itemView) {

                super(itemView);
                oppName = itemView.findViewById(R.id.opposite_name);
                thePrice = itemView.findViewById(R.id.job_price);
                tranxDate = itemView.findViewById(R.id.tranx_dateTime);
                oppNumber = itemView.findViewById(R.id.opposite_phone_number);
                oppImage = itemView.findViewById(R.id.oppositePerson_image);
                confirm = itemView.findViewById(R.id.confirm_the_transaction);
            }
        }
    }
}