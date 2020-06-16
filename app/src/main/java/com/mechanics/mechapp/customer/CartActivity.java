package com.mechanics.mechapp.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mechanics.mechapp.R;

public class CartActivity extends AppCompatActivity {

    Fragment f1 = new CartFragment();
    Fragment f2 = new CartHistory();
    Fragment active = f1;
    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cart_activity);
        getSupportActionBar().setTitle("My Cart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentManager.beginTransaction().add(R.id.cartContainer, f1, "1").commit();
        fragmentManager.beginTransaction().add(R.id.cartContainer, f2, "1").hide(f2).commit();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        fragmentManager.beginTransaction().hide(active).show(f1).commit();
                        active = f1;
                        getSupportActionBar().setTitle("My Cart");
                        return true;
                    case R.id.navigation_dashboard:
                        fragmentManager.beginTransaction().hide(active).show(f2).commit();
                        active = f2;
                        getSupportActionBar().setTitle("My Orders");
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        f1.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}

