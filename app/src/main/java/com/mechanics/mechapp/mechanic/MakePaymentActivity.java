package com.mechanics.mechapp.mechanic;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mechanics.mechapp.R;
import com.mechanics.mechapp.customer.CustomerPayMech;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.mechanics.mechapp.RandomStringHelper.presentTimeString;
import static com.mechanics.mechapp.RandomStringHelper.randomString;

public class MakePaymentActivity extends AppCompatActivity {

    private TextView textView;
    private String a, b;
    DatabaseReference databaseReference;
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_payment_frag);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Make Payment");
        getSupportActionBar().setElevation(0.0f);


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting Data...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        textView = findViewById(R.id.myDebt);
        Button makePayButton = findViewById(R.id.makePayBtn);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("All Jobs Collection").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                        a = dataSnapshot1.child("Cash Payment Debt").getValue(String.class);
                        b = dataSnapshot1.child("Completed Amount").getValue(String.class);

                        textView.setText(String.format("Balance: ₦ %s", a));

                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        makePayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Double.parseDouble(a) < 100) {
                    Toast.makeText(MakePaymentActivity.this, "You cant pay less than 100 naira", Toast.LENGTH_SHORT).show();
                    return;
                }
                new RavePayManager(MakePaymentActivity.this)
                        .setAmount(Double.parseDouble(a))
                        .setCountry("NG")
                        .setCurrency("NGN")
                        .setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                        .setfName("Customer_FirstName")
                        .setlName("Customer_LastName")
                        .setNarration("Cash Payment from first name")
                        .setPublicKey("FLWPUBK-37eaceebb259b1537c67009339575c01-X")
                        .setEncryptionKey("ab5cfe0059e5253250eb68a4")
                        .setTxRef(randomString())
                       // .acceptAccountPayments(true)
                        .acceptCardPayments(true)
                      //  .acceptUssdPayments(true)
                      //  .acceptBankTransferPayments(true)
                        .onStagingEnv(false)
                        .shouldDisplayFee(true)
                        .showStagingLabel(false)
                        .withTheme(R.style.CustomThemeForRave)
                        .initialize();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                DoAfterSuccess( );
            } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this, "ERROR ", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(this, "CANCELLED ", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void  DoAfterSuccess( ) {
        final String ppA = "0";
        final String cA = String.valueOf((Double.parseDouble(b) * 5) + Double.parseDouble(b));

        final Map<String, Object> allJobs = new HashMap<>();
        allJobs.put("Cash Payment Debt", ppA);
        allJobs.put("Completed Amount", cA);

        String  made= "You sent a payment of " + a + "to the FABAT ADMIN, your debt has been cleared.";

        final Map<String, String> sentMessage = new HashMap<>();
        sentMessage.put("notification_message", made);
        sentMessage.put("notification_time",  presentTimeString());

        databaseReference.child("Notification Collection").child("Mechanic").child(uid).child(uid).setValue(sentMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                databaseReference.child("All Jobs Collection").child(uid).updateChildren(allJobs).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(MakePaymentActivity.this, MechMainActivity.class));
                        Toast.makeText(MakePaymentActivity.this, "Payment Complete", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}