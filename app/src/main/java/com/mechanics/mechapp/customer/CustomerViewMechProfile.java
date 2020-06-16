package com.mechanics.mechapp.customer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mechanics.mechapp.R;
import com.squareup.picasso.Picasso;

public class CustomerViewMechProfile extends AppCompatActivity {
    String[] k_val, k_cat;
    String a_ = "--", b_ = "--";
    private Button ar_create_job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent.hasExtra("map")) {
            k_val = intent.getStringArrayExtra("map");
        }
        if (intent.hasExtra("category")) {
            k_cat = intent.getStringArrayExtra("category");
        }

        setContentView(R.layout.content_cus_view_mech_profile);
        getSupportActionBar().setTitle("Mechanic");
        getSupportActionBar().setElevation(0.0f);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView ar_image = findViewById(R.id.ar_mech_image);
        if (k_val[0].isEmpty()) {
            ar_image.setImageResource(R.drawable.engineer);
        } else {
            Picasso.get().load(k_val[0]).placeholder(R.drawable.engineer).into(ar_image);
        }
        // String mechLoc = k_val[3] + ", " + k_val[4] + ", " + k_val[5];
        String mechLoc = k_val[3];

        TextView ar_name = findViewById(R.id.ar_mech_name);
        ar_name.setText(k_val[1]);
        TextView ar_descpt = findViewById(R.id.ar_mech_description);
        ar_descpt.setText(k_val[2]);
        TextView ar_location = findViewById(R.id.ar_mech_location);
        ar_location.setText(mechLoc);
        final TextView ar_jobs = findViewById(R.id.ar_mech_jobs_done);
        final TextView ar_rating = findViewById(R.id.ar_mech_rating);

        FirebaseDatabase.getInstance().getReference().child("All Jobs Collection").child(k_val[10])
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                        a_ = dataSnapshot1.child("Total Job").getValue(String.class);

                        try {
                            ar_jobs.setText(a_);
                        } catch (Exception e) {
                            Log.d("", "onDataChange: ");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        FirebaseDatabase.getInstance().getReference().child("Mechanic Collection").child(k_val[10])
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                        b_ = dataSnapshot1.child("Rating").getValue(String.class);

                        try {
                            ar_rating.setText(b_);
                        } catch (Exception e) {
                            Log.d("", "onDataChange: ");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

        Button ar_callnow = findViewById(R.id.ar_mech_callnow);
        ar_create_job = findViewById(R.id.ar_mech_create_job);
        ar_create_job.isClickable();

        ar_callnow.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                try {
                    Toast.makeText(CustomerViewMechProfile.this, "Please wait while we place your call..", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + k_val[6]));
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CustomerViewMechProfile.this, new String[]{Manifest.permission.CALL_PHONE},
                                109);
                    } else {
                        startActivity(intent);
                        ar_create_job.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    Toast.makeText(CustomerViewMechProfile.this, "Call permission is not enabled..", Toast.LENGTH_SHORT).show();

                }
            }
        });

        ar_create_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomerViewMechProfile.this);
                builder.setTitle("Message!");
                builder.setMessage("Only create job after you have discussed with mechanic and agreed on price and job description.");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent in_ = new Intent(CustomerViewMechProfile.this, CustomerPayMech.class);
                        in_.putExtra("map", k_val);
                        in_.putExtra("category", k_cat);
                        startActivity(in_);
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
        });


    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 109) {
            if (grantResults.length > 0 && grantResults[grantResults.length - 1] == PackageManager.PERMISSION_GRANTED) {
                Intent intenT = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + k_val[6]));
                startActivity(intenT);
                ar_create_job.setVisibility(View.VISIBLE);
            } else {
                ActivityCompat.requestPermissions(CustomerViewMechProfile.this, new String[]{Manifest.permission.CALL_PHONE},
                        109);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}