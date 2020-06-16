package com.mechanics.mechapp.customer;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mechanics.mechapp.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AddCarActivity extends AppCompatActivity {
    ImageView CarImage;
    CardView cardView;
    String brand_edit, model_edit, date_edit, reg_num_edit;
    String position;
    Uri imageUri;
    String title_value, car_image_url;
    TextInputEditText Model_edit, Brand_edit, Date_edit, Reg_num_edit;

    ProgressDialog progressDialog;

    StorageReference storageReference;
    DatabaseReference databaseReference;
    FirebaseFirestore db;
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Car Collection");
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        setContentView(R.layout.activity_add_car);

        Model_edit = findViewById(R.id.add_car_model_edit);
        Brand_edit = findViewById(R.id.add_car_brand_edit);
        Date_edit = findViewById(R.id.add_car_date_edit);
        Reg_num_edit = findViewById(R.id.add_car_reg_number_edit);

        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model_edit = Model_edit.getText().toString().trim();
                brand_edit = Brand_edit.getText().toString().trim();
                date_edit = Date_edit.getText().toString().trim();
                reg_num_edit = Reg_num_edit.getText().toString().trim();

                if (model_edit.isEmpty() &&
                        brand_edit.isEmpty() &&
                        date_edit.isEmpty() &&
                        reg_num_edit.isEmpty()) {
                    finish();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddCarActivity.this);
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
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Car");
        getSupportActionBar().setElevation(0.0f);

        cardView = findViewById(R.id.add_car_image_container);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIn = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIn.setType("image/*");
                startActivityForResult(galleryIn, 10);
            }
        });

        CarImage = findViewById(R.id.add_car_image);


        Intent intent = getIntent();
        if (intent.hasExtra("pos")) {
            position = intent.getStringExtra("pos");
            title_value = "UPDATE VALUES";

            Model_edit.setText("");
            Brand_edit.setText("");
            Date_edit.setText("");
            Reg_num_edit.setText("");
            CarImage.setImageResource(R.drawable.ic_car);
        } else if (intent.hasExtra("title")) {
            title_value = intent.getStringExtra("title");
        }

        Button btnAdd = findViewById(R.id.add_car_button);
        btnAdd.setText(title_value);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                model_edit = Model_edit.getText().toString().trim();
                brand_edit = Brand_edit.getText().toString().trim();
                date_edit = Date_edit.getText().toString().trim();
                reg_num_edit = Reg_num_edit.getText().toString().trim();

                if (TextUtils.isEmpty(model_edit)) {
                    Toast.makeText(AddCarActivity.this, "Car model is empty", Toast.LENGTH_LONG).show();
                    return;
                } else if (TextUtils.isEmpty(brand_edit)) {
                    Toast.makeText(AddCarActivity.this, "Car Brand is empty", Toast.LENGTH_LONG).show();
                    return;
                } else if (TextUtils.isEmpty(date_edit)) {
                    Toast.makeText(AddCarActivity.this, "Car Date is empty", Toast.LENGTH_LONG).show();
                    return;
                } else if (TextUtils.isEmpty(reg_num_edit)) {
                    Toast.makeText(AddCarActivity.this, "Car Registration Number is empty", Toast.LENGTH_LONG).show();
                    return;
                }

                progressDialog.setMessage("Updating Garage...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                if (imageUri != null) {
                    int n = 10;
                    String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";

                    StringBuilder sb = new StringBuilder(n);

                    for (int i = 0; i < n; i++) {
                        int index = (int) (AlphaNumericString.length() * Math.random());
                        sb.append(AlphaNumericString.charAt(index));
                    }

                    final StorageReference s = storageReference.child("Car Images/").child(sb.toString());
                    UploadTask uploadTask = s.putFile(imageUri);

                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return s.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                car_image_url = task.getResult().toString();

                                Map<String, Object> carValues = new HashMap<>();
                                carValues.put("car_model", model_edit);
                                carValues.put("car_brand", brand_edit);
                                carValues.put("car_num", reg_num_edit);
                                carValues.put("car_date", date_edit);
                                carValues.put("car_image", car_image_url);

                                databaseReference.child(uid).child(model_edit + reg_num_edit).setValue(carValues).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(AddCarActivity.this, "Garage Updated", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                        finish();

                                    }
                                });
                            } else {
                                Toast.makeText(AddCarActivity.this, "A problem occured!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } else {
                    Map<String, Object> carValues = new HashMap<>();
                    carValues.put("car_model", model_edit);
                    carValues.put("car_brand", brand_edit);
                    carValues.put("car_num", reg_num_edit);
                    carValues.put("car_date", date_edit);
                    carValues.put("car_image", "nothing");

                    databaseReference.child(uid).child(model_edit + reg_num_edit).setValue(carValues).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(AddCarActivity.this, "Garage Updated", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            finish();
                        }
                    });
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 10 && resultCode == RESULT_OK) {
            imageUri = data.getData();
            Bitmap bitmap1 = null;
            try {
                bitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            CarImage.setImageBitmap(bitmap1);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        model_edit = Model_edit.getText().toString().trim();
        brand_edit = Brand_edit.getText().toString().trim();
        date_edit = Date_edit.getText().toString().trim();
        reg_num_edit = Reg_num_edit.getText().toString().trim();

        if (item.getItemId() == android.R.id.home) {
            if (model_edit.isEmpty() &&
                    brand_edit.isEmpty() &&
                    date_edit.isEmpty() &&
                    reg_num_edit.isEmpty()) {
                finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddCarActivity.this);
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
}