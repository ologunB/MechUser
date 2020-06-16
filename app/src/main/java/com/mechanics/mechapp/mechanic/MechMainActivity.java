package com.mechanics.mechapp.mechanic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mechanics.mechapp.LoginActivity;
import com.mechanics.mechapp.R;
import com.mechanics.mechapp.customer.ShopContainer;

public class MechMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Fragment f = null;
    FirebaseFirestore db;
    DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.mech_activity_main);
        f = new DrawerMechHomeFragment();
        loadFragment(f);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0.0f);

        drawer = findViewById(R.id.mech_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.mech_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View v = navigationView.getHeaderView(0);

        TextView uerName = v.findViewById(R.id.header_name);
        ImageView uerPic = v.findViewById(R.id.header_pic);
        SharedPreferences sharedPreferences = getSharedPreferences("TypeFile", Context.MODE_PRIVATE);
        String currentUserName = sharedPreferences.getString("Name", null);

        uerName.setText(currentUserName);
        uerPic.setImageResource(R.drawable.engineer);
    }

    @Override
    public void onBackPressed() {
        drawer = findViewById(R.id.mech_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            String msg = "Are you sure You want to exit?";
            alert.setMessage(msg);

            alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                    System.exit(0);
                }
            });

            alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    onResume();
                }
            });
            alert.show();
        }
    }

     @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.drawer_mech_home) {
            f = new DrawerMechHomeFragment();
            loadFragment(f);

        } else if (id == R.id.drawer_mech_profile) {

            f = new DrawerMechProfileFragment();
            loadFragment(f);

        } else if (id == R.id.drawerm_mech_my_jobs) {
            f = new DrawerMechJobFragment();
            loadFragment(f);

        } else if (id == R.id.drawer_mech_notification) {
            f = new DrawerMechNotificationsFragment();
            loadFragment(f);

        } else if (id == R.id.drawer_make_payment) {
            startActivity(new Intent(this, MakePaymentActivity.class));

        } /*else if (id == R.id.drawer_mech_cart) {
            startActivity(new Intent(this, ShopContainer.class));
        } */else if (id == R.id.drawer_request_payment) {
            f = new DrawerRequestPaymentFragment();
            loadFragment(f);

        } else if (id == R.id.drawer_mech_help) {
            f = new DrawerMechHelpFragment();
            loadFragment(f);

        } else if (id == R.id.drawer_mech_change_password) {
            f = new DrawerMechChangePasswordFragment();
            loadFragment(f);
        } else if (id == R.id.drawer_mech_contact) {
            f = new DrawerMechContactUsFragment();
            loadFragment(f);
        } else if (id == R.id.drawer_mech_logout) {
            _BuildLogoutDialog();
        }

        DrawerLayout drawer = findViewById(R.id.mech_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void _BuildLogoutDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        String msg = "Are you sure you want to logout?";
        alert.setMessage(msg);
        alert.setTitle("Confirmation");

        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseAuth.getInstance().signOut();
                SharedPreferences sharedPreferences = getSharedPreferences("TypeFile", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.remove("Type").apply();

                startActivity(new Intent(MechMainActivity.this, LoginActivity.class));
            }
        });

        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onResume();
            }
        });
        alert.show();
    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
    }

}
