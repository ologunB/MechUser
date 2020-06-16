package com.mechanics.mechapp.mechanic;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mechanics.mechapp.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.mechanics.mechapp.RandomStringHelper.randomString;

public class DrawerMechProfileFragment extends Fragment {

    private View view;
    private ImageView preWorks2, preWorks1, my_image;

    private final ArrayList<String> selectedItems1 = new ArrayList<>();
    private final ArrayList<String> selectedItems2 = new ArrayList<>();

    private final String[] myCars = {"Audi", "BMW", "Chrysler", "Dodge", "Ford", "Honda", "Hyundai",
            "Jeep", "Kia", "Mazda", "Mercedes Benz", "Nissan", "Peugeot", "Porsche",
            "RAM", "Range Rover", "Suzuki", "Toyota", "Volkswagen"};

    private final String[] myCategories = {"Oil & Filter Change", "Engine Expert", "Call Us", "Locking & Keys/Security", "Brake System",
            "Exhaust System", "Air Conditioner", "Wheel Balancing & Alignment", "Brake pad replacement", "Electrician",
            "Accidented Vehicle", "Painter", "Upholstery & Interior", "Tow Truck", "Panel Beater", "Tow trucks", "Car Scan", "Car Tint"};

    private ProgressDialog progressDialog;

    private FirebaseFirestore db;
    private DatabaseReference databaseReference;
    private StorageReference storageRef;

    private TextInputEditText mCompa_Web, mDescrpt, mNumber, mBankName, mBankNum, mBankMyName;

    private String name_, number_, email_, streetname_, city_, locality_, description_, web_url_, type_,
            image_url, preImage_, preImage2_, bank_name, bank_acc, bank_num, mech_image = "em";

    private static String aa = "em", bb = "em", cc = "em";

    private TextView choose_spe, choose_cat, comp_name, mMail, mStreetname, mCity, mlocality;
    private ArrayList<String> item1, item2;
    private Button update_values;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((MechMainActivity) getActivity()).getSupportActionBar().setTitle("Profile");
        super.onCreate(savedInstanceState);
    }

    private void BindView() {
        choose_spe = view.findViewById(R.id.uv_choose_specification);
        choose_cat = view.findViewById(R.id.uv_choose_category);

        mStreetname = view.findViewById(R.id.uv_streetname);
        mCity = view.findViewById(R.id.uv_cityname);
        mlocality = view.findViewById(R.id.uv_locality);

        preWorks1 = view.findViewById(R.id.uv_previous_works1);
        preWorks2 = view.findViewById(R.id.uv_previous_works2);
        my_image = view.findViewById(R.id.uv_profile_image);

        comp_name = view.findViewById(R.id.uv_company_name);
        mMail = view.findViewById(R.id.uv_emailaddress);

        mDescrpt = view.findViewById(R.id.uv_description);
        mNumber = view.findViewById(R.id.uv_number);
        mCompa_Web = view.findViewById(R.id.uv_website_url);
        update_values = view.findViewById(R.id.uv_mech_update);
        mBankMyName = view.findViewById(R.id.uv_account_name);
        mBankName = view.findViewById(R.id.uv_account_bank);
        mBankNum = view.findViewById(R.id.uv_account_num);
    }

    private Uri imageUri, pre1Uri, pre2Uri;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            imageUri = data.getData();
            Bitmap bitmap1 = null;
            try {
                bitmap1 = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            my_image.setImageBitmap(bitmap1);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            pre1Uri = data.getData();
            Bitmap bitmap1 = null;
            try {
                bitmap1 = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), pre1Uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            preWorks1.setImageBitmap(bitmap1);
        } else if (requestCode == 3 && resultCode == RESULT_OK) {
            pre2Uri = data.getData();
            Bitmap bitmap1 = null;
            try {
                bitmap1 = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), pre2Uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            preWorks2.setImageBitmap(bitmap1);
        }
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        final String Uid_ = FirebaseAuth.getInstance().getUid();

        view = inflater.inflate(R.layout.activity_mech_profile, container, false);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Getting Informations");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (!isOnline()) {
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }

        BindView();

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        assert Uid_ != null;
        db.collection("All").document(Uid_).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isComplete()) {
                            progressDialog.dismiss();
                            name_ = task.getResult().getString("Company Name");
                            item1 = (ArrayList<String>) task.getResult().get("Specifications");
                            item2 = (ArrayList<String>) task.getResult().get("Categories");
                            number_ = task.getResult().getString("Phone Number");
                            email_ = task.getResult().getString("Email");
                            streetname_ = task.getResult().getString("Street Name");
                            city_ = task.getResult().getString("City");
                            locality_ = task.getResult().getString("Locality");
                            description_ = task.getResult().getString("Description");
                            web_url_ = task.getResult().getString("Website Url");
                            image_url = task.getResult().getString("Image Url");
                            preImage_ = task.getResult().getString("PreviousImage1 Url");
                            preImage2_ = task.getResult().getString("PreviousImage2 Url");
                            bank_acc = task.getResult().getString("Bank Account Name");
                            bank_name = task.getResult().getString("Bank Name");
                            bank_num = task.getResult().getString("Bank Account Number");
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Information not gotten due to bad connection", Toast.LENGTH_LONG).show();
                        }
                        // To display the Specs and Categories
                        final boolean[] checkedItems1 = new boolean[myCars.length];
                        final boolean[] checkedItems2 = new boolean[myCategories.length];

                        ArrayList<Boolean> b1 = new ArrayList<>();
                        ArrayList<Boolean> b2 = new ArrayList<>();

                        List<String> specList = new ArrayList<>(Arrays.asList(myCars));
                        List<String> catList = new ArrayList<>(Arrays.asList(myCategories));

                        for (int h = 0; h < myCars.length; h++) {
                            if (item1.contains(specList.get(h))) {
                                b1.add(true);
                            } else {
                                b1.add(false);
                            }
                        }

                        for (int h = 0; h < myCategories.length; h++) {
                            if (item2.contains(catList.get(h))) {
                                b2.add(true);
                            } else {
                                b2.add(false);
                            }
                        }

                        for (int h = 0; h < myCars.length; h++) {
                            checkedItems1[h] = b1.get(h);
                        }

                        for (int h = 0; h < myCategories.length; h++) {
                            checkedItems2[h] = b2.get(h);
                        }

                        selectedItems1.addAll(item1);
                        selectedItems2.addAll(item2);

                        comp_name.setText(name_);
                        mMail.setText(email_);
                        mNumber.setText(number_);
                        mStreetname.setText(streetname_);
                        mCity.setText(city_);
                        mlocality.setText(locality_);
                        mCompa_Web.setText(web_url_);
                        mDescrpt.setText(description_);

                        mBankMyName.setText(bank_acc);
                        mBankName.setText(bank_name);
                        mBankNum.setText(bank_num);

                        update_values.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                progressDialog.setMessage("Updating values...");
                                progressDialog.show();


                                if (imageUri != null) {
                                    final StorageReference s = storageRef.child("photos/").child(randomString() + "11");
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
                                                aa = task.getResult().toString();

                                                Map<String, Object> mm = new HashMap<>();
                                                mm.put("Image Url", aa);

                                                db.collection("Mechanics").document(Uid_).update(mm);
                                                db.collection("All").document(Uid_).update(mm);
                                                databaseReference.child("Mechanic Collection").child(Uid_).updateChildren(mm);
                                            }
                                        }
                                    });
                                }
                                if (pre1Uri != null) {
                                    final StorageReference s = storageRef.child("photos/").child(randomString() + "22");
                                    UploadTask uploadTask = s.putFile(pre1Uri);

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
                                                bb = task.getResult().toString();

                                                Map<String, Object> mm = new HashMap<>();
                                                mm.put("PreviousImage1 Url", bb);

                                                db.collection("Mechanics").document(Uid_).update(mm);
                                                db.collection("All").document(Uid_).update(mm);
                                                databaseReference.child("Mechanic Collection").child(Uid_).updateChildren(mm);
                                            }
                                        }
                                    });
                                }
                                if (pre2Uri != null) {
                                    final StorageReference s = storageRef.child("photos/").child(randomString() + "33");
                                    UploadTask uploadTask = s.putFile(pre2Uri);

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
                                                cc = task.getResult().toString();

                                                Map<String, Object> mm = new HashMap<>();
                                                mm.put("PreviousImage2 Url", cc);

                                                db.collection("Mechanics").document(Uid_).update(mm);
                                                db.collection("All").document(Uid_).update(mm);
                                                databaseReference.child("Mechanic Collection").child(Uid_).updateChildren(mm);
                                            }
                                        }
                                    });
                                }

                                final Map<String, Object> m = new HashMap<>();
                                m.put("Bank Account Name", mBankMyName.getText().toString());
                                m.put("Bank Account Number", mBankNum.getText().toString());
                                m.put("Bank Name", mBankName.getText().toString());
                                m.put("Phone Number", mNumber.getText().toString());
                                m.put("Description", mDescrpt.getText().toString());
                                m.put("Website Url", mCompa_Web.getText().toString());

                                db.collection("Mechanics").document(Uid_).update(m);
                                db.collection("All").document(Uid_).update(m);
                                databaseReference.child("Mechanic Collection").child(Uid_).updateChildren(m);

                                Toast.makeText(getContext(), "Updated", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        });

                        my_image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent galleryIn = new Intent(Intent.ACTION_GET_CONTENT);
                                galleryIn.setType("image/*");
                                startActivityForResult(galleryIn, 1);
                            }
                        });

                        if (image_url.isEmpty()) {
                            my_image.setImageResource(R.drawable.add_camera);
                        } else {
                            Picasso.get().load(image_url).placeholder(R.drawable.add_camera).into(my_image);
                        }

                        if (preImage_.isEmpty()) {
                            preWorks1.setImageResource(R.drawable.photo);
                        } else {
                            Picasso.get().load(preImage_).placeholder(R.drawable.photo).into(preWorks1);
                        }
                        if (preImage2_.isEmpty()) {
                            preWorks2.setImageResource(R.drawable.photo);
                        } else {
                            Picasso.get().load(preImage2_).placeholder(R.drawable.photo).into(preWorks2);
                        }

                        preWorks1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent galleryIn = new Intent(Intent.ACTION_GET_CONTENT);
                                galleryIn.setType("image/*");
                                startActivityForResult(galleryIn, 2);
                            }
                        });

                        preWorks2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent galleryIn = new Intent(Intent.ACTION_GET_CONTENT);
                                galleryIn.setType("image/*");
                                startActivityForResult(galleryIn, 3);
                            }
                        });

                        choose_spe.setText(selectedItems1.toString());
                        choose_spe.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder aDialog = new AlertDialog.Builder(getContext());
                                aDialog.setTitle("Select a Specialisation");

                                aDialog.setMultiChoiceItems(myCars, checkedItems1, new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                        if (b) {
                                            if (!selectedItems1.contains(myCars[i])) {

                                                selectedItems1.add(myCars[i]);
                                            }
                                        } else selectedItems1.remove(myCars[i]);
                                    }
                                });

                                aDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        choose_spe.setText("");
                                        for (i = 0; i < selectedItems1.size(); i++) {
                                            choose_spe.append(selectedItems1.get(i) + ", ");
                                        }
                                    }
                                });

                                aDialog.setNeutralButton("", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

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


                        choose_cat.setText(selectedItems2.toString());
                        choose_cat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder aDialog = new AlertDialog.Builder(getContext());
                                aDialog.setTitle("Select a Category");

                                aDialog.setMultiChoiceItems(myCategories, checkedItems2, new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                        if (b) {
                                            if (!selectedItems2.contains(myCategories[i])) {
                                                selectedItems2.add(myCategories[i]);
                                            }

                                        } else selectedItems2.remove(myCategories[i]);
                                    }
                                });

                                aDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        choose_cat.setText("");
                                        for (i = 0; i < selectedItems2.size(); i++) {
                                            choose_cat.append(selectedItems2.get(i) + ", ");
                                        }
                                    }
                                });

                                aDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        onResume();
                                    }
                                });


                                aDialog.setNeutralButton("", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });

                                AlertDialog dialog = aDialog.create();
                                dialog.show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Information not gotten due to bad connection", Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }
}