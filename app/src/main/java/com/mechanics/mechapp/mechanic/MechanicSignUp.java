package com.mechanics.mechapp.mechanic;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mechanics.mechapp.LoginActivity;
import com.mechanics.mechapp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.mechanics.mechapp.RandomStringHelper.randomString;
import static com.mechanics.mechapp.customer.MainActivity.checkLocationON;

public class MechanicSignUp extends AppCompatActivity {
    final ArrayList<String> selectedItems1 = new ArrayList<>();
    final ArrayList<String> selectedItems2 = new ArrayList<>();
    boolean[] checkedItems1 = {false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false};
    boolean[] checkedItems2 = {false, false, false, false, false, false,
            false, false, false, false, false, false, false, false,
            false, false, false, false, false};
    Button sign_up_btn, cancel_btn;
    TextInputEditText name_text, number_text, email_text, password_text, web_url_text, description_text, et_locality_;
    TextView streetname_text, city_text, Choose_Specification, Choose_Category;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    ProgressDialog mDialog;
    StorageReference storageRef;
    Uri Imageuri, ImageCAC, ImagePre1, ImagePre2;
    double myLat, myLong;
    int p = 0, q = 0 ;
    DatabaseReference databaseReference;
    Context c = MechanicSignUp.this;
    private ImageView cacCerti, preWorks1, preWorks2;
    private String Name;
    private String Email;
    private String downloadUrl1 = "em";
    private String downloadUri2 = "em";
    private String downloadUri3 = "em";
    private String downloadUri4 = "em";
    private String Web_url;
    private String Phone_no;
    private String Locality;
    private String City;
    private String Streetname;
    private String Description;

    private void userRegister() {
        Name = name_text.getText().toString().trim();
        Email = email_text.getText().toString().trim();
        String password = password_text.getText().toString().trim();
        Phone_no = number_text.getText().toString().trim();
        Locality = et_locality_.getText().toString().trim();
        City = city_text.getText().toString().trim();
        Streetname = streetname_text.getText().toString().trim();
        Description = description_text.getText().toString().trim();
        Web_url = web_url_text.getText().toString().trim();

        if (TextUtils.isEmpty(Email)) {
            Toast.makeText(MechanicSignUp.this, "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(Name)) {
            Toast.makeText(MechanicSignUp.this, "Enter Name", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(Phone_no)) {
            Toast.makeText(MechanicSignUp.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(MechanicSignUp.this, "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        } else if (password.length() < 6) {
            Toast.makeText(MechanicSignUp.this, "Password must be greater then 6 digit", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(City)) {
            Toast.makeText(MechanicSignUp.this, "Enter City", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(Locality)) {
            Toast.makeText(MechanicSignUp.this, "Enter Locality", Toast.LENGTH_SHORT).show();
            return;
        } else if (selectedItems1.isEmpty()) {
            Toast.makeText(MechanicSignUp.this, "Enter Specialization", Toast.LENGTH_SHORT).show();
            return;
        } else if (selectedItems2.isEmpty()) {
            Toast.makeText(MechanicSignUp.this, "Enter Category", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(Streetname)) {
            Toast.makeText(MechanicSignUp.this, "Enter the Street name", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(String.valueOf(getMyLat())) || getMyLat() == 0) {
            Toast.makeText(MechanicSignUp.this, "Coordinates Not gotten, Click on Street name", Toast.LENGTH_SHORT).show();
            return;
        } else if (ImageCAC == null) {
            Toast.makeText(MechanicSignUp.this, "Include the CAC Certificate", Toast.LENGTH_SHORT).show();
            return;
        }

        mDialog.setMessage("Creating Mechanic Profile...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        mAuth.createUserWithEmailAndPassword(Email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> Ptask) {
                if (Ptask.isSuccessful()) {
                    try {
                        final StorageReference s = storageRef.child("Photos/").child(randomString());
                        UploadTask uploadTask = s.putFile(ImageCAC);

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
                                    downloadUri4 = task.getResult().toString();
                                    sendEmailVerification();
                                } else {
                                    Toast.makeText(MechanicSignUp.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    } catch (Exception e) {
                        mDialog.dismiss();
                        Toast.makeText(MechanicSignUp.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                    }

                } else {
                    Toast.makeText(MechanicSignUp.this, "User with email exists", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
            }
        });
    }

    public double getMyLat() {
        return myLat;
    }

    public void setMyLat(double myLat) {
        this.myLat = myLat;
    }

    public double getMyLong() {
        return myLong;
    }

    public void setMyLong(double myLong) {
        this.myLong = myLong;
    }

    private void sendEmailVerification() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        createAnewUser(user.getUid());
                    }
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createAnewUser(final String uid) {
        if (TextUtils.isEmpty(Description)) {
            Description = "empty";
        }
        if (TextUtils.isEmpty(Web_url)) {
            Web_url = "No Url";
        }
        String sample = "https://firebasestorage.googleapis.com/v0/b/mechanics-b3612.appspot.com/o/photos%2Fimage%3A55039?alt=media&token=e25a7e4c-fa06-452a-b630-2b89bae6f7b4";

        if (downloadUrl1.isEmpty()) {
            downloadUrl1 = sample;
        }
        if (downloadUri2.isEmpty()) {
            downloadUri2 = sample;
        }
        if (downloadUri3.isEmpty()) {
            downloadUri3 = sample;
        }
        if (downloadUri4.isEmpty()) {
            downloadUri4 = sample;
        }
        final Map<String, Object> m = new HashMap<>();
        m.put("Company Name", Name);
        m.put("Specifications", selectedItems1);
        m.put("Categories", selectedItems2);
        m.put("Phone Number", Phone_no);
        m.put("Email", Email);
        m.put("Street Name", Streetname);
        m.put("City", City);
        m.put("Locality", Locality);
        m.put("Description", Description);
        m.put("Website Url", Web_url);
        m.put("Loc Latitude", getMyLat());
        m.put("LOc Longitude", getMyLong());
        m.put("Image Url", downloadUrl1);
        m.put("CAC Image Url", downloadUri4);
        m.put("PreviousImage1 Url", downloadUri2);
        m.put("PreviousImage2 Url", downloadUri3);
        m.put("Bank Account Name", "");
        m.put("Bank Account Number", "");
        m.put("Bank Name", "");
        m.put("Type", "Mechanic");
        m.put("Jobs Done", "0");
        m.put("Rating", "0.00");
        m.put("Reviews", "0");
        m.put("Mech Uid", uid);
        m.put("State", "Review");
        m.put("Timestamp", Calendar.getInstance().getTimeInMillis());

        final Map<String, String> allJobs = new HashMap<>();
        allJobs.put("Total Job", "0");
        allJobs.put("Total Amount", "0");
        allJobs.put("Pending Job", "0");
        allJobs.put("Pending Amount", "0");
        allJobs.put("Pay pending Amount", "0");
        allJobs.put("Completed Amount", "0");
        allJobs.put("Payment Request", "0");
        allJobs.put("Cash Payment Debt", "0");

        db.collection("Mechanics").document(uid).set(m).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                db.collection("All").document(uid).set(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        databaseReference.child("Mechanic Collection").child(uid).setValue(m).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                databaseReference.child("All Jobs Collection").child(uid).setValue(allJobs).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(MechanicSignUp.this, "Check your Email for verification", Toast.LENGTH_SHORT).show();
                                        FirebaseAuth.getInstance().signOut();
                                        startActivity(new Intent(MechanicSignUp.this, LoginActivity.class));
                                    }
                                });

                            }
                        });

                    }
                });

            }
        });


    }

    private Uri getUriFromDrawable(Context context, int theDraw) {
        Uri mainURI = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context.getResources().getResourcePackageName(theDraw)
                + '/' + context.getResources().getResourceTypeName(theDraw)
                + '/' + context.getResources().getResourceEntryName(theDraw));
        return mainURI;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Imageuri = getUriFromDrawable(c, R.drawable.add_camera);
        // ImageCAC = getUriFromDrawable(c, R.drawable.photo);
        ImagePre1 = getUriFromDrawable(c, R.drawable.photo);
        ImagePre2 = getUriFromDrawable(c, R.drawable.photo);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mech_signup);
        mAuth = FirebaseAuth.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            checkLocationON(this);
        }

        //drop downs
        Choose_Specification = findViewById(R.id.et_choose_specification);
        Choose_Category = findViewById(R.id.et_choose_category);
        et_locality_ = findViewById(R.id.et_locality_);
        city_text = findViewById(R.id.et_cityname);

        //upload images
        cacCerti = findViewById(R.id.et_cac_certi);
        preWorks2 = findViewById(R.id.et_previous_works2);
        preWorks1 = findViewById(R.id.et_previous_works1);
        //  my_image = findViewById(R.id.et_profile_image);

        //input texts
        name_text = findViewById(R.id.et_name);
        number_text = findViewById(R.id.et_number);
        email_text = findViewById(R.id.et_emailaddress);
        password_text = findViewById(R.id.et_password);
        streetname_text = findViewById(R.id.et_streetname);
        description_text = findViewById(R.id.et_description);
        web_url_text = findViewById(R.id.et_website_uri);

        //Actions Button
        sign_up_btn = findViewById(R.id.et_mech_register);
        cancel_btn = findViewById(R.id.et_mech_cancel);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);

        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRegister();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
/**
 my_image.setOnClickListener(new View.OnClickListener() {
@Override public void onClick(View view) {
Intent galleryIn = new Intent(Intent.ACTION_GET_CONTENT);
galleryIn.setType("image/*");
startActivityForResult(galleryIn, 1);
}
});
 **/
        cacCerti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIn = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIn.setType("image/*");
                startActivityForResult(galleryIn, 2);
            }
        });

        preWorks1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIn = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIn.setType("image/*");
                startActivityForResult(galleryIn, 3);
            }
        });

        preWorks2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIn = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIn.setType("image/*");
                startActivityForResult(galleryIn, 4);
            }
        });

        Choose_Specification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder aDialog = new AlertDialog.Builder(MechanicSignUp.this);
                aDialog.setTitle("Select a Specialization");
                final String[] myCars = {"Audi", "BMW", "Chrysler", "Dodge", "Ford", "Honda", "Hyundai",
                        "Jeep", "Kia", "Mazda", "Mercedes Benz", "Nissan", "Peugeot", "Porsche",
                        "RAM", "Range Rover", "Suzuki", "Toyota", "Volkswagen"};

                aDialog.setMultiChoiceItems(myCars, checkedItems1, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if (b) {
                            if (!selectedItems1.contains(myCars[i])) {
                                selectedItems1.add(myCars[i]);
                            }
                        } else if (selectedItems1.contains(myCars[i])) {
                            selectedItems1.remove(myCars[i]);
                        }
                    }
                });

                aDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Choose_Specification.setText("");
                        for (i = 0; i < selectedItems1.size(); i++) {
                            Choose_Specification.append(selectedItems1.get(i) + ", ");
                        }
                    }
                });

                aDialog.setNeutralButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // ListView l = ((AlertDialog) dialog
                        boolean[] checkedItems3 = {true, true, true, true, true, true, true, true, true,
                                true, true, true, true, true, true, true, true, true, true};

                        aDialog.setMultiChoiceItems(myCars, checkedItems3, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                if (b) {
                                    if (!selectedItems1.contains(myCars[i])) {

                                        selectedItems1.add(myCars[i]);
                                    }
                                } else if (selectedItems1.contains(myCars[i])) {
                                    selectedItems1.remove(myCars[i]);
                                }
                            }
                        });

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

        Choose_Category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder aDialog = new AlertDialog.Builder(MechanicSignUp.this);
                aDialog.setTitle("Select a Category");
                final String[] myCategories = {"Oil & Filter Change", "Engine Expert", "Call Us", "Locking & Keys/Security", "Brake System",
                        "Exhaust System", "Air Conditioner", "Wheel Balancing & Alignment", "Brake pad replacement", "Electrician",
                        "Accidented Vehicle", "Painter", "Upholstery & Interior", "Panel Beater", "Tow trucks", "Car Scan", "Car Tint"};

                aDialog.setMultiChoiceItems(myCategories, checkedItems2, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if (b) {
                            if (!selectedItems2.contains(myCategories[i])) {
                                selectedItems2.add(myCategories[i]);
                            }

                        } else if (selectedItems2.contains(myCategories[i])) {
                            selectedItems2.remove(myCategories[i]);
                        }
                    }
                });

                aDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Choose_Category.setText("");
                        for (i = 0; i < selectedItems2.size(); i++) {
                            Choose_Category.append(selectedItems2.get(i) + ", ");
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

        final String[] myCity = {"Abia", "Adamawa", "Akwa Ibom", "Anambra", "Bauchi", "Bayelsa", "Benue", "Borno", "Cross River", "Delta", "Ebonyi", "Edo", "Ekiti", "Enugu", "FCT - Abuja", "Gombe", "Imo","Jigawa", "Kaduna", "Kano", "Katsina", "Kebbi", "Kogi", "Kwara", "Lagos", "Nasarawa", "Niger", "Ogun", "Ondo", "Osun", "Oyo", "Plateau", "Rivers", "Sokoto", "Taraba", "Yobe", "Zamfara"};
        //     final String[] lo1 = {"VI", "Ikorodu", "Agege", "Alausa", "Ikosi", "Badagry", "Mushin", "Oshodi", "Berger"};
        //    final String[] lo2 = {"Ibadan", "Bodija", "Egbeda", "Kajola", "Sepeteri", "Ado-Awaye"};

        final int[] a = new int[1];
        final int[] b = new int[1];

        city_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder aDialog = new AlertDialog.Builder(MechanicSignUp.this);
                aDialog.setTitle("Select your City");

                aDialog.setSingleChoiceItems(myCity, p, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, final int i) {
                        p = i;
                        a[0] = i;
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        city_text.setText(myCity[a[0]]);
                    }
                });

                AlertDialog dialog = aDialog.create();
                dialog.show();
            }
        });

 /*       locality_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder aDialog = new AlertDialog.Builder(MechanicSignUp.this);
                aDialog.setTitle("Select your Locality");

                if (a[0] == 0) {

                    aDialog.setSingleChoiceItems(lo1, q, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            q = i;
                            b[0] = i;
                        }
                    }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            locality_text.setText(lo1[b[0]]);
                        }
                    });
                    aDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onResume();
                        }
                    });

                } else if (a[0] == 1) {

                    aDialog.setSingleChoiceItems(lo2, q, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            q = i;
                            b[0] = i;
                        }
                    }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            locality_text.setText(lo2[b[0]]);
                        }
                    });
                    aDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onResume();
                        }
                    });

                } else {
                    Toast.makeText(MechanicSignUp.this, "Select a City...", Toast.LENGTH_LONG).show();
                }
                AlertDialog dialog = aDialog.create();
                dialog.show();
            }
        });*/

        streetname_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new GetLocationActivity().show(getSupportFragmentManager(), null);
            }
        });

        findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void doPositiveClick(String a) {
        // Do stuff here.
        streetname_text.setText(a);
    }

    public String getIt() {
        return streetname_text.getText().toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Imageuri = data.getData();
            Bitmap bitmap1 = null;
            try {
                bitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Imageuri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(MechanicSignUp.this, Imageuri.toString(), Toast.LENGTH_LONG).show();
            //my_image.setImageBitmap(bitmap1);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            ImageCAC = data.getData();
            Bitmap bitmap1 = null;
            try {
                bitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), ImageCAC);
            } catch (IOException e) {
                e.printStackTrace();
            }
            cacCerti.setImageBitmap(bitmap1);
        } else if (requestCode == 3 && resultCode == RESULT_OK) {
            ImagePre1 = data.getData();
            Bitmap bitmap1 = null;
            try {
                bitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), ImagePre1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            preWorks1.setImageBitmap(bitmap1);
        } else if (requestCode == 4 && resultCode == RESULT_OK) {
            assert data != null;
            ImagePre2 = data.getData();
            Bitmap bitmap1 = null;
            try {
                bitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), ImagePre2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            preWorks2.setImageBitmap(bitmap1);
        }
    }
}