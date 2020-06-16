package com.mechanics.mechapp.customer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mechanics.mechapp.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.mechanics.mechapp.customer.MainActivity.getCurrentUserName;

public class CustomerPayMech extends AppCompatActivity {
    DatabaseReference databaseReference;
    ArrayList<CarRecyclerModel> arr;
    ArrayList<String> keyArray;
    ListView lv;
    MyGarageAdapter1 adapter;
    ProgressDialog progressDialog;
    ImageView car_image;
    ArrayList<String> sel = new ArrayList<>();
    EditText descrpt_edit, amount_edit;
    TextView select_car;
    Context context = CustomerPayMech.this;
    String[] k_val, k_cat;
    StringBuilder TransactionID;
    String   c_, d_,  uid, MechUId, NameOfMech, NameOfCus, NumberOfMech, NumberOfCus, MEchImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setView(R.layout.paystack_accredit);
        }
        builder.setCancelable(false);
        builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onResume();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();

        Intent intent = getIntent();
        if (intent.hasExtra("map")) {
            k_val = intent.getStringArrayExtra("map");
        }
        if (intent.hasExtra("category")) {
            k_cat = intent.getStringArrayExtra("category");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_cus_pay_mech);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        final boolean[] checkedItem = new boolean[k_cat.length];
        Arrays.fill(checkedItem, false);

        progressDialog = new ProgressDialog(context);

        getSupportActionBar().setTitle("Pay Mechanic");
        getSupportActionBar().setElevation(0.0f);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ScrollView v = findViewById(R.id.scrollView);
        v.post(new Runnable() {
            @Override
            public void run() {
                v.scrollTo(0, v.getBottom());
            }
        });

        TextView mechNAme = findViewById(R.id.cd_mech_name);
        ImageView mechImage = findViewById(R.id.cd_mech_image);

        mechNAme.setText(k_val[1]);
        MechUId = k_val[10];

        if (k_val[0].isEmpty()) {
            mechImage.setImageResource(R.drawable.engineer);
        } else {
            Picasso.get().load(k_val[0]).placeholder(R.drawable.engineer).into(mechImage);
        }
        select_car = findViewById(R.id.cd_select_car);
        Button select_descrpt = findViewById(R.id.cd_select_descrpt);
        descrpt_edit = findViewById(R.id.cd_descrpt);
        car_image = findViewById(R.id.cd_car_image);
        amount_edit = findViewById(R.id.cd_amount);
        Button PayMech = findViewById(R.id.cd_paymech);

        select_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                view1 = getLayoutInflater().inflate(R.layout.my_bottom_garage_fragment, null);
                BottomSheetDialog bsd1 = new BottomSheetDialog(CustomerPayMech.this);
                final LinearLayout no_car_layout = view1.findViewById(R.id.garage_frag_no_cars_layout);

                final ProgressBar progressBar = view1.findViewById(R.id.progress);
                progressBar.setVisibility(View.VISIBLE);

                LinearLayout addCarButton = view1.findViewById(R.id.garage_frag_add_car_layout);

                addCarButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view2) {
                        Intent in = new Intent(context, AddCarActivity.class);
                        in.putExtra("title", "ADD CAR");
                        startActivity(in);
                    }
                });

                lv = view1.findViewById(R.id.garage_frag_recycler);
                lv.setClickable(true);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String a = arr.get(i).getCar_brand();
                        String b = arr.get(i).getCar_model();
                        String c = arr.get(i).getCar_date();

                        select_car.setText(String.format("%s %s, %s", a, b, c));

                        view.setBackgroundColor(getResources().getColor(R.color.light_gray));

                        if (arr.get(i).getCar_image().isEmpty()) {
                            car_image.setImageResource(R.drawable.ic_car);
                        } else {
                            Picasso.get().load(arr.get(i).getCar_image()).placeholder(R.drawable.ic_car).into(car_image);
                        }
                    }
                });

                databaseReference.child("Car Collection").child(uid).addValueEventListener(new ValueEventListener() {
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
                        progressBar.setVisibility(View.INVISIBLE);

                        if (arr.size() > 0) {
                            adapter = new MyGarageAdapter1(context, arr);
                            lv.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            lv.setVisibility(View.INVISIBLE);
                            no_car_layout.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                bsd1.setCancelable(true);
                bsd1.setContentView(view1);
                bsd1.show();

                databaseReference.child("All Jobs Collection").child(MechUId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                          c_ = dataSnapshot1.child("Pending Job").getValue(String.class);
                        d_ = dataSnapshot1.child("Pending Amount").getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                databaseReference.child("Mechanic Collection").child(MechUId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot3) {
                                NameOfMech = dataSnapshot3.child("Company Name").getValue(String.class);
                                NumberOfMech = dataSnapshot3.child("Phone Number").getValue(String.class);
                                MEchImage = dataSnapshot3.child("Image Url").getValue(String.class);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                databaseReference.child("Customer Collection").child(uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot3) {
                                NameOfCus = dataSnapshot3.child("Company Name").getValue(String.class);
                                NumberOfCus = dataSnapshot3.child("Phone Number").getValue(String.class);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

            }
        });

        select_descrpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                AlertDialog.Builder aDialog = new AlertDialog.Builder(CustomerPayMech.this);
                aDialog.setTitle("Select Description");

                aDialog.setMultiChoiceItems(k_cat, checkedItem, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if (b) {
                            if (!sel.contains(k_cat[i])) {
                                sel.add(k_cat[i]);
                            }
                        } else {
                            sel.remove(k_cat[i]);
                        }
                    }
                });

                aDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        descrpt_edit.setText("");
                        for (i = 0; i < sel.size(); i++) {
                            descrpt_edit.append(sel.get(i) + ", ");
                        }
                    }
                });

                aDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onResume();
                    }
                });

                AlertDialog dialog = aDialog.create();
                dialog.show();

            }
        });

        // To get a specific transaction ID
        int n = 16;
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";

        TransactionID = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            TransactionID.append(AlphaNumericString.charAt(index));
        }

        PayMech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String carType_ = select_car.getText().toString();
                final String amount_ = amount_edit.getText().toString();

                if (carType_.isEmpty()) {
                    Toast.makeText(context, "Car Field is Empty!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (amount_.isEmpty()) {
                    Toast.makeText(context, "Amount cannot be Empty!", Toast.LENGTH_LONG).show();
                    return;
                }


                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setTitle("Confirmation");

                builder1.setPositiveButton("Pay with Cash", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                        builder2.setTitle("Are you sure you have paid the mechanic?");

                        builder2.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DoAfterSuccess("By Cash");
                            }
                        });

                        builder2.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onResume();
                            }
                        });
                        final AlertDialog dialog2 = builder2.create();
                        dialog2.show();

                    }
                });
                builder1.setNegativeButton("Other Options", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        new RavePayManager(CustomerPayMech.this)
                                .setAmount(Double.parseDouble(amount_))
                                .setCountry("NG")
                                .setCurrency("NGN")
                                .setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                                .setfName(getCurrentUserName())
                                .setlName("")
                                .setNarration("FABAT MANAGEMENT")
                                .setPublicKey("FLWPUBK-37eaceebb259b1537c67009339575c01-X")
                                .setEncryptionKey("ab5cfe0059e5253250eb68a4")
                                .setTxRef(TransactionID.toString())
                                //  .acceptAccountPayments(true)
                                .acceptCardPayments(true)
                                /// .acceptUssdPayments(true)
                                // .acceptBankTransferPayments(true)
                                .onStagingEnv(false)
                                .shouldDisplayFee(true)
                                .showStagingLabel(false)
                                .withTheme(R.style.CustomThemeForRave)
                                .initialize();
                    }
                });
                final AlertDialog dialog = builder1.create();
                dialog.show();
            }

        });
    }


    private void DoAfterSuccess(String serverData) {
        progressDialog.setMessage("Finalizing...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        final String carType_ = select_car.getText().toString();
        final String amount_ = amount_edit.getText().toString();
        String description_ = descrpt_edit.getText().toString();

        progressDialog.setMessage("Paying now and clarifying things...");

        // To get the current time
        Date currentTime = Calendar.getInstance().getTime();
        String now = DateFormat.getDateTimeInstance().format(currentTime);

        //updates jobs on both side
        final Map<String, Object> valuesToCustomer = new HashMap<>();
        valuesToCustomer.put("Mech Name", NameOfMech);
        valuesToCustomer.put("Customer Name", NameOfCus);
        valuesToCustomer.put("Mech UID", MechUId);
        valuesToCustomer.put("Mech Number", NumberOfMech);
        valuesToCustomer.put("Mech Image", MEchImage);
        valuesToCustomer.put("Trans Amount", amount_);
        valuesToCustomer.put("Trans Time", now);
        valuesToCustomer.put("Car Type", carType_);
        valuesToCustomer.put("Server Confirmation", serverData);
        valuesToCustomer.put("Trans Description", description_);
        valuesToCustomer.put("Trans ID", TransactionID.toString());
        valuesToCustomer.put("Trans Confirmation", "Unconfirmed");
        valuesToCustomer.put("Mech Confirmation", "Unconfirmed");
        valuesToCustomer.put("hasReviewed", "False");
        valuesToCustomer.put("Timestamp",  Calendar.getInstance().getTimeInMillis());

        Map<String, Object> valuesToMech = new HashMap<>();
        valuesToMech.put("Customer UID", uid);
        valuesToMech.put("Customer Name", NameOfCus);
        valuesToMech.put("Customer Number", NumberOfCus);
        valuesToMech.put("Trans Amount", amount_);
        valuesToMech.put("Trans Time", now);
        valuesToMech.put("Server Confirmation", serverData);
        valuesToMech.put("Car Type", carType_);
        valuesToMech.put("Trans Description", description_);
        valuesToMech.put("Trans ID", TransactionID.toString());
        valuesToMech.put("Trans Confirmation", "Unconfirmed");
        valuesToMech.put("Mech Confirmation", "Unconfirmed");
        valuesToMech.put("Timestamp",  Calendar.getInstance().getTimeInMillis());

        int aa = Integer.parseInt(c_) + 1;
        int bb = Integer.parseInt(d_) + Integer.parseInt(amount_);

        final Map<String, Object> updateJobs = new HashMap<>();
        updateJobs.put("Pending Job", String.valueOf(aa));
        updateJobs.put("Pending Amount", String.valueOf(bb));

        String received = "You have a pending payment of ₦" + amount_ + " by " + NameOfCus +
                " and shall be available if confirmed by the customer. Thanks for using FABAT";

        String made = "You have made a payment of ₦" + amount_ + " to " + NameOfMech + " for " + carType_ +
                " and has been withdrawn from your Card. Thanks for using FABAT";

        final Map<String, Object> sentMessage = new HashMap<>();
        sentMessage.put("notification_message", made);
        sentMessage.put("notification_time", now);
        sentMessage.put("Timestamp",  Calendar.getInstance().getTimeInMillis());

        final Map<String, Object> receivedMessage = new HashMap<>();
        receivedMessage.put("notification_message", received);
        receivedMessage.put("notification_time", now);
        receivedMessage.put("Timestamp",  Calendar.getInstance().getTimeInMillis());

        databaseReference.child("Jobs Collection").child("Mechanic").child(MechUId).child(TransactionID.toString()).
                setValue(valuesToMech).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.setMessage("Action 1/3...");
                databaseReference.child("Notification Collection").child("Mechanic").child(MechUId).push().setValue(receivedMessage).
                        addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.setMessage("Action 2/3...");
                                databaseReference.child("Jobs Collection").child("Customer").child(uid).child(TransactionID.toString()).
                                        setValue(valuesToCustomer).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.setMessage("Almost done...");
                                        //progressDialog.dismiss();
                                        databaseReference.child("Notification Collection").child("Customer").child(uid).push().setValue(sentMessage).
                                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        progressDialog.setMessage("Finalizing...");

                                                        databaseReference.child("All Jobs Collection").child(MechUId).updateChildren(updateJobs);

                                                        progressDialog.dismiss();

                                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                            builder.setView(R.layout.confirmed_xml);
                                                        }
                                                        builder.setCancelable(false);

                                                        builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                startActivity(new Intent(context, MainActivity.class));
                                                            }
                                                        });
                                                        final AlertDialog dialog = builder.create();
                                                        dialog.show();
                                                        finish();
                                                        Toast.makeText(context, "Successful Transaction", Toast.LENGTH_LONG).show();

                                                    }
                                                });
                                    }
                                });
                            }
                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                DoAfterSuccess(message);
            } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this, "ERROR ", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(this, "CANCELLED ", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (descrpt_edit.getText().toString().isEmpty() &&
                    amount_edit.getText().toString().isEmpty() &&
                    select_car.getText().toString().isEmpty()) {
                finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirmation");
                builder.setMessage("Do you want to discard the fields? ");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onResume();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public class MyGarageAdapter1 extends BaseAdapter {
        private final Context context1;
        private final ArrayList<CarRecyclerModel> values;

        private MyGarageAdapter1(Context context, ArrayList<CarRecyclerModel> values) {
            this.context1 = context;
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
            LayoutInflater inflater = (LayoutInflater) context1.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.cars_viewholder, parent, false);
            TextView carBrand = rowView.findViewById(R.id.sd_car_brand);
            TextView carModel = rowView.findViewById(R.id.sd_car_model);
            TextView carDate = rowView.findViewById(R.id.sd_car_year);
            ImageView carImage = rowView.findViewById(R.id.sd_car_image);
            ImageView close = rowView.findViewById(R.id.remove_car);

            String a = values.get(position).getCar_brand();
            String b = values.get(position).getCar_model();
            String c = values.get(position).getCar_date();

            Picasso.get().load(values.get(position).getCar_image()).placeholder(R.drawable.ic_car).into(carImage);
            close.setVisibility(View.INVISIBLE);
            carBrand.setText(a);
            carModel.setText(b);
            carDate.setText(c);

            return rowView;
        }
    }
}