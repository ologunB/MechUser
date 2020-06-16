package com.mechanics.mechapp.customer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.squareup.picasso.Picasso;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.mechanics.mechapp.RandomStringHelper.presentTimeString;
import static com.mechanics.mechapp.RandomStringHelper.randomString;

public class DrawerJOBFragment extends Fragment {

    private JobAdapter adapter;
    private float givenRate = Float.parseFloat("3");
    private DatabaseReference databaseReference;
    private ArrayList<JobsRecyclerModel> arr;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private String a_, b_, c_, d_, f_, g_, h_, selectedUID;
    private String preRating, preReview;
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Jobs");
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

        databaseReference = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Getting Jobs...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (!isOnline()) {
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }

        recyclerView = view.findViewById(R.id.grid_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference.child("Jobs Collection").child("Customer").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        arr = new ArrayList<>();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            String t1 = dataSnapshot1.child("Mech Image").getValue(String.class);
                            String t2 = dataSnapshot1.child("Trans Amount").getValue(String.class);
                            String t3 = dataSnapshot1.child("Trans Time").getValue(String.class);
                            String t4 = dataSnapshot1.child("Car Type").getValue(String.class);
                            String t6 = dataSnapshot1.child("Trans Description").getValue(String.class);
                            String t7 = dataSnapshot1.child("Mech Name").getValue(String.class);
                            String t8 = dataSnapshot1.child("Mech Number").getValue(String.class);
                            String t9 = dataSnapshot1.child("Customer Name").getValue(String.class);
                            String t10 = dataSnapshot1.child("Trans ID").getValue(String.class);
                            String t11 = dataSnapshot1.child("Trans Confirmation").getValue(String.class);
                            String t12 = dataSnapshot1.child("Mech UID").getValue(String.class);
                            String t13 = dataSnapshot1.child("Mech Confirmation").getValue(String.class);
                            String t14 = dataSnapshot1.child("hasReviewed").getValue(String.class);
                            String t15 = dataSnapshot1.child("Server Confirmation").getValue(String.class);

                            arr.add(new JobsRecyclerModel(t2, t4, t3, t6, t1, t7, t8, t9, t10, t11, t12, t13, t14, t15));
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

        private final ArrayList<JobsRecyclerModel> mData;
        private Context context;

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

            if (mData.get(position).getImage() == null) {
                holder.oppImage.setImageResource(R.drawable.engineer);
            } else {
                Picasso.get().load(mData.get(position).getImage()).placeholder(R.drawable.engineer).into(holder.oppImage);
            }

            if (mData.get(position).getCusConfirmation().equals("Confirmed")
                    && mData.get(position).getMechConfirmation().equals("Confirmed")) {
                if (mData.get(position).getHasReviewed().equals("True")) {
                    holder.confirm.setText("COMPLETED!");
                    holder.confirm.setBackgroundColor(getResources().getColor(R.color.gray));
                    holder.confirm.setEnabled(false);
                    Toast.makeText(getContext(), "You have reviewed mechanic before!", Toast.LENGTH_LONG).show();
                } else {
                    holder.confirm.setText("RATE MECH.");
                    holder.confirm.setBackgroundColor(getResources().getColor(R.color.gray));
                }

            } else if (mData.get(position).getCusConfirmation().equals("Confirmed")) {
                holder.confirm.setText("PENDING!");
                holder.confirm.setEnabled(false);
                holder.confirm.setBackgroundColor(getResources().getColor(R.color.red_dark));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            holder.confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mData.get(position).getHasReviewed().equals("True")
                            && mData.get(position).getCusConfirmation().equals("Confirmed")
                            && mData.get(position).getMechConfirmation().equals("Confirmed")) {
                        selectedUID = mData.get(position).getUID();

                        databaseReference.child("Mechanic Collection").child(selectedUID)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot3) {
                                        preRating = dataSnapshot3.child("Rating").getValue(String.class);
                                        preReview = dataSnapshot3.child("Reviews").getValue(String.class);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        TextView title = new TextView(getContext());
                        title.setPadding(5, 10, 5, 0);
                        title.setText("Rate your dealing with " + mData.get(position).getName());
                        title.setGravity(Gravity.CENTER_HORIZONTAL);
                        title.setTextSize(22);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setTextColor(getResources().getColor(R.color.navy_blue_dark));

                        builder.setCustomTitle(title);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            View view1 = getLayoutInflater().inflate(R.layout.dialog_rating, null);
                            final TextView textReview = view1.findViewById(R.id.ab_review);
                            builder.setView(view1);

                            ScaleRatingBar ratingBar = view1.findViewById(R.id.ratingBar);
                            ratingBar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
                                @Override
                                public void onRatingChange(BaseRatingBar ratingBar, float rate, boolean fromUser) {
                                    givenRate = rate;
                                }
                            });

                            builder.setPositiveButton("SEND REVIEW", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    final Map<String, Object> review = new HashMap<>();
                                    review.put("review_message", textReview.getText().toString());
                                    review.put("review_time", presentTimeString());
                                    review.put("Timestamp",  Calendar.getInstance().getTimeInMillis());

                                    final Map<String, Object> cusVal = new HashMap<>();
                                    cusVal.put("hasReviewed", "True");

                                    progressDialog.setMessage("Submitting review...");
                                    progressDialog.setCanceledOnTouchOutside(false);
                                    progressDialog.show();

                                    try {
                                        double _rate = Double.parseDouble(preRating);
                                        int _review = Integer.parseInt(preReview);

                                        int presentReview = _review + 1;
                                        String presentRate = String.valueOf(((_rate * _review) + givenRate) / presentReview);
                                        final Map<String, Object> updateMech = new HashMap<>();
                                        updateMech.put("Rating", presentRate.substring(0, 3));
                                        updateMech.put("Reviews", String.valueOf(presentReview));

                                        databaseReference.child("Mechanic Collection").child(selectedUID).updateChildren(updateMech)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        progressDialog.setMessage("Almost done...");

                                                        databaseReference.child("Mechanic Reviews").child("Mechanic").child(selectedUID).child(randomString())
                                                                .setValue(review).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                databaseReference.child("Jobs Collection").child("Customer").child(uid).
                                                                        child(mData.get(position).getTransactionID()).updateChildren(cusVal).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(getContext(), "Thanks for the review", Toast.LENGTH_LONG).show();
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });
                                    } catch (Exception e) {
                                        Toast.makeText(getContext(), "Still getting mechanics value, try again!", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });

                            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    onResume();
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }
                    } else {
                        databaseReference.child("All Jobs Collection").child(mData.get(position).getUID())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                        a_ = dataSnapshot1.child("Total Job").getValue(String.class);
                                        b_ = dataSnapshot1.child("Total Amount").getValue(String.class);
                                        c_ = dataSnapshot1.child("Pending Job").getValue(String.class);
                                        d_ = dataSnapshot1.child("Pending Amount").getValue(String.class);
                                        //  e_ = dataSnapshot1.child("Pay pending Job").getValue(String.class);
                                        f_ = dataSnapshot1.child("Pay pending Amount").getValue(String.class);
                                        //  g_ = dataSnapshot1.child("Completed Job").getValue(String.class);
                                        h_ = dataSnapshot1.child("Cash Payment Debt").getValue(String.class);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });

                        try {
                            final String amount_ = mData.get(position).getAmount_paid();
                            final String MechUID = mData.get(position).getUID();

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Confirmation");
                            builder.setMessage("Is the Mechanic done with your Job?");
                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    onResume();
                                }
                            });

                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    int aa = Integer.parseInt(a_) + 1;// Total Jobs
                                    int bb = Integer.parseInt(b_) + Integer.parseInt(amount_); // Total Amount
                                    int cc = Integer.parseInt(c_) - 1; // Pending Jobs
                                    int dd = Integer.parseInt(d_) - Integer.parseInt(amount_); // Pending Amount
                                    int hh = Integer.parseInt(h_) + (Integer.parseInt(amount_) / 5); //  Cash Payment Debt
                                    int ff = Integer.parseInt(f_) + Integer.parseInt(amount_); // Pay pending Amount

                                    final Map<String, Object> updateJobs = new HashMap<>();
                                    updateJobs.put("Total Job", String.valueOf(aa));
                                    updateJobs.put("Total Amount", String.valueOf(bb));
                                    updateJobs.put("Pending Job", String.valueOf(cc));
                                    updateJobs.put("Pending Amount", String.valueOf(dd));
                                    if (mData.get(position).getServerData().equals("By Cash")) {
                                        updateJobs.put("Cash Payment Debt", String.valueOf(hh));
                                    } else {
                                        updateJobs.put("Pay pending Amount", String.valueOf(ff));
                                    }

                                    final String transactID = mData.get(position).getTransactionID();
                                    String carType_ = mData.get(position).getCar_type();
                                    String NameOfMech = mData.get(position).getName();


                                    String made = "Your payment of ₦" + amount_ + " to " + NameOfMech + " for " + carType_ +
                                            " has been confirmed by you. Thanks for using FABAT";

                                    String received = "You have a confirmed payment of ₦" + amount_ + " by " + mData.get(position).getMyOwnName() +
                                            " and shall be available in the next 5 days. Thanks for using FABAT";

                                    final Map<String, Object> sentMessage = new HashMap<>();
                                    sentMessage.put("notification_message", made);
                                    sentMessage.put("notification_time", presentTimeString());
                                    sentMessage.put("Timestamp",  Calendar.getInstance().getTimeInMillis());

                                    final Map<String, Object> receivedMessage = new HashMap<>();
                                    receivedMessage.put("notification_message", received);
                                    receivedMessage.put("notification_time", presentTimeString());
                                    receivedMessage.put("Timestamp",  Calendar.getInstance().getTimeInMillis());

                                    Map<String, Object> valuesToMech = new HashMap<>();
                                    valuesToMech.put("Trans Confirmation", "Confirmed");

                                    final Map<String, Object> valuesToCustomer = new HashMap<>();
                                    valuesToCustomer.put("Trans Confirmation", "Confirmed");

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
                                                                                            databaseReference.child("All Jobs Collection").child(MechUID).updateChildren(updateJobs);

                                                                                            progressDialog.dismiss();
                                                                                            holder.confirm.setText("COMPLETED!");
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