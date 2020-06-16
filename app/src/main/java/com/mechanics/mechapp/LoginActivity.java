package com.mechanics.mechapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mechanics.mechapp.customer.CustomerSignUp;
import com.mechanics.mechapp.customer.MainActivity;
import com.mechanics.mechapp.mechanic.MechMainActivity;
import com.mechanics.mechapp.mechanic.MechanicSignUp;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;
    FirebaseUser mUser;
    FirebaseFirestore mFirestore;
    ProgressDialog dialog;
    ProgressBar loginProgress;
    EditText email, password;
    Button LoginToEither;
    TextView textView;
    ImageButton mechSU, userSU;
    Context c = LoginActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        LoginToEither = findViewById(R.id.mech_login);
        loginProgress = findViewById(R.id.LoginProgress);

        mechSU = findViewById(R.id.mech_signup);
        userSU = findViewById(R.id.user_signup);
        email = findViewById(R.id.editTextemail);
        password = findViewById(R.id.editText2passworrd);
        textView = findViewById(R.id.asEither);

        dialog = new ProgressDialog(c);
        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();

        LoginToEither.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline()) {
                    userSign();
                } else {
                    Toast.makeText(c, "No Internet Connection", Toast.LENGTH_LONG).show();
                }
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.setMessage("SignUp As");
                builder.setPositiveButton("Mechanic", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent _in = new Intent(c, MechanicSignUp.class);
                        startActivity(_in);
                    }
                });
                builder.setNegativeButton("Customer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent _in = new Intent(c, CustomerSignUp.class);
                        startActivity(_in);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        mechSU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _in = new Intent(c, MechanicSignUp.class);
                startActivity(_in);
            }
        });

        userSU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent _in = new Intent(c, CustomerSignUp.class);
                startActivity(_in);
            }
        });

        findViewById(R.id.forgot_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(c, ForgotPasswordActivity.class));
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.removeAuthStateListener(mAuthListner);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListner != null) {
            mAuth.removeAuthStateListener(mAuthListner);
        }
    }

    @Override
    public void onBackPressed() {
        LoginActivity.super.finish();
    }

    String _email, _password;

    private void userSign() {
        _email = email.getText().toString().trim();
        _password = password.getText().toString().trim();
        if (TextUtils.isEmpty(_email)) {
            Toast.makeText(c, "Enter the email field", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(_password)) {
            Toast.makeText(c, "Enter the password field", Toast.LENGTH_SHORT).show();
            return;
        }
        //dialog.setMessage("Logging in please wait...");
        LoginToEither.setText("Logging in...");
        //dialog.setIndeterminate(true);
        //dialog.show();
        loginProgress.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(_email, _password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    loginProgress.setVisibility(View.GONE);
                    LoginToEither.setText("LOGIN");
                    Toast.makeText(c, "Login details not found", Toast.LENGTH_SHORT).show();

                } else {
                    checkIfEmailVerified();
                }
            }
        });
    }

    void checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        boolean emailVerified = user.isEmailVerified();

        if (emailVerified) {
            LoginToEither.setText("Logged In...");
            mFirestore.collection("All").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            String type = documentSnapshot.getString("Type");
                            String name = documentSnapshot.getString("Company Name");
                            String state = documentSnapshot.getString("State");

                            assert type != null;

                            if (state.equals("Review")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                                builder.setMessage("Your Account has not been approved as it is under review. Check again in the next 24 hours");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        onResume();
                                        FirebaseAuth.getInstance().signOut();
                                    }
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();
                                LoginToEither.setText("LOGIN");
                                loginProgress.setVisibility(View.GONE);

                            } else if (state.equals("Blocked")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                                builder.setMessage("Your Account has been blocked for going against the FABAT rules. Check with the Admin through our various channels.");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        onResume();
                                        FirebaseAuth.getInstance().signOut();
                                    }
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();
                                LoginToEither.setText("LOGIN");
                                loginProgress.setVisibility(View.GONE);
                            } else if (type.equals("Mechanic")) {
                                SharedPreferences sharedPreferences = getSharedPreferences("TypeFile", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("Type", "Mechanic");
                                editor.putString("Name", name);
                                editor.apply();
                                startActivity(new Intent(c, MechMainActivity.class));
                            } else if (type.equals("Customer")) {
                                SharedPreferences sharedPreferences = c.getSharedPreferences("TypeFile", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("Type", "Customer");
                                editor.putString("Name", name);

                                editor.apply();
                                startActivity(new Intent(c, MainActivity.class));
                            }
                        }
                    }
                }
            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(c);
            builder.setMessage("Email hasn't been verified. Should the link be sent to the email again?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(LoginActivity.this, "Check your Email for verification Link", Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                            dialog.dismiss();
                        }
                    });
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    onResume();
                    Toast.makeText(LoginActivity.this, "Verify Email", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }
}
/**
 * void downloadAndSaveImage(Context context, String url){
 * <p>
 * String root = Environment.getExternalStorageDirectory().toString();
 * File myDir = new File(root + "/jjppy_images");
 * //myDir.mkdirs();
 * <p>
 * File file = new File(myDir, "jjppy.jpg");
 * <p>
 * try {
 * InputStream is = (InputStream) new URL(url).getContent();
 * Bitmap d = BitmapFactory.decodeStream(is);
 * is.close();
 * FileOutputStream fileOutputStream = new FileOutputStream(file);
 * d.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
 * fileOutputStream.close();
 * } catch (IOException e) {
 * e.printStackTrace();
 * }
 * <p>
 * }
 **/