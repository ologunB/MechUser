package com.mechanics.mechapp.customer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.mechanics.mechapp.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ServicesItemActivity extends AppCompatActivity implements OnMapReadyCallback {

    String title, position;
    private RecyclerView serviceRecycler;
    private ServiceItemAdapter adapter;
    private LinearLayout noMechanicLayout;
    private ProgressDialog dialog;

    private List<MechanicModel> mechanicModels;
    ServicesData servicesData = new ServicesData();
    FusedLocationProviderClient mFusedLocationProviderClient;
    private CollectionReference driverCollection;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_services);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.serviceMap);
        mapFragment.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        Intent intent = getIntent();
        if (intent.hasExtra("pos22")) {
            position = intent.getStringExtra("pos22");
            title = servicesData.getList().get(Integer.valueOf(position)).getServiceName();
        }

        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setElevation(0.0f);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        serviceRecycler = findViewById(R.id.serviceRecycler);
        noMechanicLayout = findViewById(R.id.noMechanicLayout);

        serviceRecycler.setHasFixedSize(true);
        serviceRecycler.setLayoutManager(new LinearLayoutManager(this));

        mechanicModels = new ArrayList();
        driverCollection = FirebaseFirestore.getInstance().collection("All");

        getUserLocationAndStartLoadingMechanics();
    }

    private void getUserLocationAndStartLoadingMechanics() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    10);

        } else {
            dialog.setMessage("Retrieving your location");
            dialog.show();
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isComplete()) {
                        Location gottenLocation = (Location) task.getResult();
                        if (gottenLocation != null) {
                            LatLng userLatLng = new LatLng(gottenLocation.getLatitude(), gottenLocation.getLongitude());
                            loadMechanicsFromDatabaseAndMapLocations();
                        }

                    } else {
                        dialog.dismiss();
                        Toast.makeText(ServicesItemActivity.this, "An error occurred! Check your internet connection and try again", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void loadMechanicsFromDatabaseAndMapLocations() {
        dialog.setMessage("Retrieving relevant mechanics..");
        driverCollection.whereArrayContains("Categories", title)
                .addSnapshotListener(
                        new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                if (queryDocumentSnapshots != null) {
                                    queryDocumentSnapshots.getDocuments();
                                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                        if (document != null) {
                                            MechanicModel model = MechanicData.convertDocumentToDriverModel(document);
                                            mechanicModels.add(model);
                                        }
                                    }

                                    if (mechanicModels.isEmpty()) {
                                        dialog.dismiss();
                                        noMechanicLayout.setVisibility(View.VISIBLE);
                                        serviceRecycler.setVisibility(View.GONE);
                                    }

                                    adapter = new ServiceItemAdapter(mechanicModels, ServicesItemActivity.this);
                                    serviceRecycler.setAdapter(adapter);
                                    loadMechanicLocations(mechanicModels);

                                } else {
                                    dialog.dismiss();
                                    noMechanicLayout.setVisibility(View.VISIBLE);
                                    serviceRecycler.setVisibility(View.GONE);
                                }
                            }
                        }
                );
    }

    private void loadMechanicLocations(final List<MechanicModel> mechanicModels) {
        dialog.setMessage("Finding mechanics location");

        if (mMap != null) {
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    LatLngBounds.Builder bounds = new LatLngBounds.Builder();
                    if (!mechanicModels.isEmpty()) {
                        for (MechanicModel mechanicModel : mechanicModels) {
                            LatLng currentDriverPosition = new LatLng(mechanicModel.getLatitude(), mechanicModel.getLongitude());

                            mMap.addMarker(new MarkerOptions().position(currentDriverPosition).title(mechanicModel.getCompanyName() + ". " + mechanicModel.getCity() + ", " + mechanicModel.getLocality()));
                            bounds.include(currentDriverPosition);
                        }

                        LatLngBounds mapBound = bounds.build();
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(mapBound, 100);
                        mMap.moveCamera(cameraUpdate);
                        dialog.dismiss();
                    } else {

                        dialog.dismiss();
                        noMechanicLayout.setVisibility(View.VISIBLE);
                        serviceRecycler.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            finish();
        } catch (Exception e) {
            Toast.makeText(ServicesItemActivity.this, "What's happening!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            try {
                finish();
            } catch (Exception e) {
                Toast.makeText(ServicesItemActivity.this, "What's happening!", Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserLocationAndStartLoadingMechanics();
                }
            }
        }
    }
}