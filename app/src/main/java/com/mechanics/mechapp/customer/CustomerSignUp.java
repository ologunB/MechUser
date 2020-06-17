package com.mechanics.mechapp.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mechanics.mechapp.LoginActivity;
import com.mechanics.mechapp.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CustomerSignUp extends AppCompatActivity {
    Button sign_up_user_btn;
    TextInputEditText name_text, number_text, email_text, password_text;
    FirebaseFirestore db;
    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    String Name, Email, Password, Phone_no;
    ProgressDialog mDialog;

    private void UserRegister() {
        Name = name_text.getText().toString().trim();
        Email = email_text.getText().toString().trim();
        Password = password_text.getText().toString().trim();
        Phone_no = number_text.getText().toString().trim();

        if (TextUtils.isEmpty(Email)) {
            Toast.makeText(CustomerSignUp.this, "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(Name)) {
            Toast.makeText(CustomerSignUp.this, "Enter Name", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(Phone_no)) {
            Toast.makeText(CustomerSignUp.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(Password)) {
            Toast.makeText(CustomerSignUp.this, "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        } else if (Password.length() < 6) {
            Toast.makeText(CustomerSignUp.this, "Password must be greater then 6 digit", Toast.LENGTH_SHORT).show();
            return;
        }
        mDialog.setMessage("Creating User please wait...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    try {
                        sendEmailVerification();

                    } catch (Exception e) {
                        mDialog.dismiss();
                        Toast.makeText(CustomerSignUp.this, "Does domain exists!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CustomerSignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final String uid = user.getUid();
        final Map<String, Object> m = new HashMap<>();
        m.put("Company Name", Name);
        m.put("Phone Number", Phone_no);
        m.put("Email", Email);
        m.put("Type", "Customer");
        m.put("Uid", uid);
        m.put("State", "Current");
        m.put("Timestamp", Calendar.getInstance().getTimeInMillis());

        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        db.collection("Customer").document(uid).set(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                db.collection("All").document(uid).set(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        databaseReference.child("Customer Collection").child(uid).setValue(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(CustomerSignUp.this, "Check your Email for verification", Toast.LENGTH_SHORT).show();
                                                FirebaseAuth.getInstance().signOut();
                                                startActivity(new Intent(CustomerSignUp.this, LoginActivity.class));
                                            }
                                        });
                                    }
                                });
                            }
                        });

                    } else {
                        Toast.makeText(CustomerSignUp.this, "An Error has occurred, try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(CustomerSignUp.this, "An Error has occurred, try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);
        mAuth = FirebaseAuth.getInstance();
        name_text = findViewById(R.id.et_user_name);
        number_text = findViewById(R.id.et_user_number);
        email_text = findViewById(R.id.et_user_emailaddress);
        password_text = findViewById(R.id.et_user_password);
        sign_up_user_btn = findViewById(R.id.sign_up_user_btn);
        Button cancel_user_btn = findViewById(R.id.cancel_user_btn);
        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);

        sign_up_user_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserRegister();
            }
        });

        cancel_user_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}