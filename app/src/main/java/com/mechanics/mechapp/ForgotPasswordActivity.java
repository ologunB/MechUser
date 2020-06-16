package com.mechanics.mechapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgotpassw);
        findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final TextInputEditText email = findViewById(R.id.editTextemail);
        final Button reset = findViewById(R.id.reset_pass);
        final ProgressBar pr = findViewById(R.id.changePassProgress);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().isEmpty()){
                    Toast.makeText(ForgotPasswordActivity.this, "Input Email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                pr.setVisibility(View.VISIBLE);
                reset.setText("Resetting Password...");
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String emailaddress = email.getText().toString();
                auth.sendPasswordResetEmail(emailaddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this, "Email has been sent!", Toast.LENGTH_LONG).show();

                            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                        }else{
                            Toast.makeText(ForgotPasswordActivity.this, "Email Not Found!", Toast.LENGTH_LONG).show();

                            pr.setVisibility(View.GONE);
                            reset.setText("Reset Password");
                        }
                    }
                });
            }
        });
    }
}