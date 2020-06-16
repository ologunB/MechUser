package com.mechanics.mechapp.customer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
 import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.mechanics.mechapp.customer.MainActivity.checkLocationON;

public class DrawerNearbyFragment extends AppCompatActivity implements OnMapReadyCallback {

    private LinearLayout noNearbyMechanic;
    private RecyclerView nearbyRecycler;
    private ProgressDialog dialog;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private List<MechanicModel> modelList;
    private CollectionReference mechanicCollection;

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_nearby);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.nearbyMap);
        nearbyRecycler = findViewById(R.id.nearbyRecycler);
        noNearbyMechanic = findViewById(R.id.noNearbyMechanic);
        dialog = new ProgressDialog(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mechanicCollection = FirebaseFirestore.getInstance().collection("Mechanics");
        modelList = new ArrayList();

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("NearBy Mechanics");
            getSupportActionBar().setElevation(0.0f);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dialog.setCanceledOnTouchOutside(false);
        nearbyRecycler.setHasFixedSize(true);
        nearbyRecycler.setLayoutManager(new LinearLayoutManager(this));

        setupPermissionsAndLoadMechanics();

        if (!isOnline()) {
            Toast.makeText(DrawerNearbyFragment.this, "No Internet Connection", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    private void setupPermissionsAndLoadMechanics() {
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
                            getRelevantMechanics(userLatLng);
                        }

                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "An error occurred! Check your internet connection snd try again", Toast.LENGTH_LONG).show();
                    }
                }
            });        }
    }

    private void getRelevantMechanics(final LatLng userLatLng) {
        dialog.setMessage("Finding Mechanics");
        mechanicCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        if (document != null) {
                            MechanicModel model = MechanicData.convertDocumentToDriverModel(document);
                            if (distanceBetween(userLatLng, new LatLng(model.getLatitude(), model.getLongitude())) <= 2000) {
                                modelList.add(model);
                            }
                        }
                    }

                    if (modelList.isEmpty()) {
                        dialog.dismiss();
                        nearbyRecycler.setVisibility(View.GONE);
                        noNearbyMechanic.setVisibility(View.VISIBLE);
                    } else {
                        fillRecyclerAndMap(modelList, userLatLng);
                    }

                } else {
                    dialog.dismiss();
                    nearbyRecycler.setVisibility(View.GONE);
                    noNearbyMechanic.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void fillRecyclerAndMap(final List<MechanicModel> modelList, LatLng userLatLng) {
        dialog.setMessage("Compiling Mechanics and Their Locations..");
        NearByAdapter adapter = new NearByAdapter(modelList, getApplicationContext(), userLatLng);
        nearbyRecycler.setAdapter(adapter);

        if (mMap != null) {
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    LatLngBounds.Builder bounds = new LatLngBounds.Builder();
                    if (!modelList.isEmpty()) {
                        for (MechanicModel mechanicModel : modelList) {
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
                        noNearbyMechanic.setVisibility(View.VISIBLE);
                        nearbyRecycler.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupPermissionsAndLoadMechanics();
            }
        }
    }

    private float distanceBetween(LatLng latLng1, LatLng latLng2) {
        Location loc1 = new Location(LocationManager.GPS_PROVIDER);
        Location loc2 = new Location(LocationManager.GPS_PROVIDER);

        loc1.setLatitude(latLng1.latitude);
        loc1.setLongitude(latLng1.longitude);

        loc2.setLatitude(latLng2.latitude);
        loc2.setLongitude(latLng2.longitude);

        return loc1.distanceTo(loc2) / 1000;
    }

}