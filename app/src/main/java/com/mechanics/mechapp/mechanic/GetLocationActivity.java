package com.mechanics.mechapp.mechanic;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.mechanics.mechapp.R;

import java.util.List;
import java.util.Locale;

public class GetLocationActivity extends DialogFragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LatLng myCurrentLocation;
    private TextInputEditText a;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SupportMapFragment f = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.getLocMap);
        if (f != null)
            getFragmentManager().beginTransaction().remove(f).commit();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View rootView = inflater.inflate(R.layout.content_getlocation, null);
        a = rootView.findViewById(R.id.gh_streetname);
        a.setText(((MechanicSignUp) getActivity()).getIt());

        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.getLocMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        builder.setView(rootView);

        builder.setTitle("Get your location")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                onDestroyView();
                                ((MechanicSignUp) getActivity()).doPositiveClick(a.getText().toString());
                            }
                        }
                )
                .setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                onDestroyView();
                                dialog.dismiss();
                            }
                        }
                )
                .create();

        AlertDialog dialog = builder.create();
        dialog.show();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((MechanicSignUp) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        dialog.getWindow().setLayout((int) (width * 0.7), (int) (height * 0.7));

        return dialog;
    }

    private void moveCamera(LatLng latLng, float mZoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, mZoom));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(mZoom), 2000, null);
    }

    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
            try {

                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location myLocation = (Location) task.getResult();

                            myCurrentLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

                            moveCamera(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 12f);
                            ((MechanicSignUp) getActivity()).setMyLat(myCurrentLocation.latitude);
                            ((MechanicSignUp) getActivity()).setMyLong(myCurrentLocation.longitude);

                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                            List<Address> addresses;
                            try {
                                addresses = geocoder.getFromLocation(myLocation.getLatitude(), myLocation.getLongitude(), 1);
                                a.setText(addresses.get(0).getAddressLine(0));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(myCurrentLocation);
                            markerOptions.title("Current Position");
                            mMap.addMarker(markerOptions);

                        } else {
                            Toast.makeText(getContext(), "Location not gotten", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            } catch (SecurityException e) {
                Log.d("TAG", "getDeviceLocation: " + e.getMessage());
            }

        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    10);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Get the current location of the device and set the position of the map.

        getDeviceLocation();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[grantResults.length - 1] == PackageManager.PERMISSION_GRANTED) {
                    getDeviceLocation();
                }
            }
        }
    }
}