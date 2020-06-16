package com.mechanics.mechapp.customer;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mechanics.mechapp.LoginActivity;
import com.mechanics.mechapp.R;
import com.mechanics.mechapp.mechanic.MechMainActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Fragment f = null;

    public static String getCurrentUserName() {
        return currentUserName;
    }

    public static String currentUserName;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void checkLocationON(final Activity activity) {
        try {
            int gpsOff = Settings.Secure.getInt(activity.getContentResolver(), Settings.Secure.LOCATION_MODE);
            if (gpsOff == 0) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
                builder.setMessage("Location must be ON!");
                builder.setPositiveButton("PUT ON", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });

                android.app.AlertDialog dialog = builder.create();
                dialog.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferencesUtil util = new SharedPreferencesUtil(this);
        if (!util.hasUserClickedYes()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("By using this app, you agree to it's terms and conditions");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    if (util.clickYes()) dialogInterface.dismiss();
                }
            });
            builder.setNegativeButton("VIEW MORE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    util.clickYes();
                    dialogInterface.dismiss();
                    Uri termsAndConditionsUri = Uri.parse("https://fabat.com.ng/TermsAndCondition.pdf");
                    Intent intent = new Intent(Intent.ACTION_VIEW, termsAndConditionsUri);
                    startActivity(intent);
                }
            });
            builder.setTitle("Terms And Conditions Agreement");
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("TypeFile", Context.MODE_PRIVATE);
        currentUserName = sharedPreferences.getString("Name", "null");

        String a = sharedPreferences.getString("Type", null);
        FirebaseUser uid = FirebaseAuth.getInstance().getCurrentUser();
        Context c = MainActivity.this;

        Intent intent;
        if (a != null && a.equals("Mechanic")) {
            finish();
            intent = new Intent(c, MechMainActivity.class);
            startActivity(intent);

        } else if (uid == null) {
            finish();
            intent = new Intent(c, LoginActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_main);
        f = new DrawerHomeFragment();
        loadFragment(f);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setElevation(0.0f);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View v = navigationView.getHeaderView(0);
        TextView uerName = v.findViewById(R.id.header_name);

        uerName.setText(currentUserName);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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

        if (id == R.id.drawer_home) {
            f = new DrawerHomeFragment();
            loadFragment(f);
        } else if (id == R.id.drawer_my_garage) {
            f = new DrawerMyGarageFragment();
            loadFragment(f);
        } else if (id == R.id.drawer_mechanics_nearby) {
            startActivity(new Intent(this, DrawerNearbyFragment.class));
        } else if (id == R.id.drawer_cart) {
            startActivity(new Intent(this, CartActivity.class));
        } else if (id == R.id.drawer_shop) {
            startActivity(new Intent(this, ShopContainer.class));
        } else if (id == R.id.drawer_notification) {
            f = new DrawerNotificationsFragment();
            loadFragment(f);
        } else if (id == R.id.drawer_help) {
            f = new DrawerHelpFragment();
            loadFragment(f);
        } else if (id == R.id.drawer_my_jobs) {
            f = new DrawerJOBFragment();
            loadFragment(f);
        } else if (id == R.id.drawer_change_password) {
            f = new DrawerChangePasswordFragment();
            loadFragment(f);
        } else if (id == R.id.drawer_contact) {
            f = new DrawerContactUsFragment();
            loadFragment(f);
        } else if (id == R.id.drawer_logout) {
            _BuildLogoutDialog();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("Type").apply();

                startActivity(new Intent(MainActivity.this, LoginActivity.class));

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
