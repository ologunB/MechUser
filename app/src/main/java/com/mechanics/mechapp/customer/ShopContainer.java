package com.mechanics.mechapp.customer;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.mechanics.mechapp.R;

import java.util.ArrayList;
import java.util.List;

public class ShopContainer extends AppCompatActivity {
    static ArrayList<ShopItemModel> allShopItems;
    ProgressDialog progressDialog;
    ViewPager viewPager;
    RecyclerView searchRecyclerContainer;
    TabLayout tabLayout;
    TextView emptytext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_shop);
        getSupportActionBar().setTitle("Shop");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0.0f);

        findViewById(R.id.shopfab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShopContainer.this, CartActivity.class));
            }
        });

        emptytext = findViewById(R.id.emptytext);

        searchRecyclerContainer = findViewById(R.id.searchRecycler);
        searchRecyclerContainer.setLayoutManager(new GridLayoutManager(ShopContainer.this, 2));

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting Shop Items...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Shop Collection");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> keyArr = new ArrayList<>();
                for (DataSnapshot dataSnapshot7 : dataSnapshot.getChildren()) {
                    keyArr.add(dataSnapshot7.getKey());
                }

                allShopItems = new ArrayList<>();
                final GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {
                };

                for (int i = 0; i < keyArr.size(); i++) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.child(keyArr.get(i)).getChildren()) {
                        String t1 = dataSnapshot1.child("shop_item_name").getValue(String.class);
                        String t2 = dataSnapshot1.child("shop_item_price").getValue(String.class);
                        String t3 = dataSnapshot1.child("shop_item_seller").getValue(String.class);
                        String t4 = dataSnapshot1.child("shop_item_email").getValue(String.class);
                        String t5 = dataSnapshot1.child("shop_item_phoneNo").getValue(String.class);
                        String t6 = dataSnapshot1.child("shop_item_descrpt").getValue(String.class);
                        List<String> t7 = dataSnapshot1.child("shop_item_images").getValue(t);
                        String t8 = dataSnapshot1.child("shop_item_ID").getValue(String.class);
                        String t9 = dataSnapshot1.child("shop_item_type").getValue(String.class);

                        ShopItemModel item = new ShopItemModel(t1, t2, t3, t4, t5, t6, t7, t8, t9);
                        allShopItems.add(item);
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());

        pagerAdapter.addFrag(new ToolShopFrag(), "Tools");
        pagerAdapter.addFrag(new PartShopFrag(), "Parts");
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(Color.parseColor("#B6B0B0"), Color.parseColor("#ffffff"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        try {
            SearchView.OnQueryTextListener textChangeListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {

                    String changedText = newText.trim();
                    ArrayList<ShopItemModel> foundItems = new ArrayList();

                    if (!changedText.isEmpty()) {
                        searchRecyclerContainer.setVisibility(View.VISIBLE);
                        tabLayout.setVisibility(View.GONE);
                        viewPager.setVisibility(View.GONE);
                        emptytext.setVisibility(View.GONE);

                        if(allShopItems != null){
                            for (ShopItemModel item : allShopItems) {
                                if (item.getShop_item_name().toLowerCase().contains(changedText.toLowerCase())) {
                                    foundItems.clear();
                                    foundItems.add(item);
                                }
                            }
                        }

                        if (foundItems.isEmpty()) {
                            foundItems.clear();
                            ShopAdapter adapter = new ShopAdapter(ShopContainer.this, foundItems);
                            searchRecyclerContainer.setAdapter(adapter);

                            emptytext.setVisibility(View.VISIBLE);
                            tabLayout.setVisibility(View.GONE);
                            viewPager.setVisibility(View.GONE);
                         } else {

                            ShopAdapter adapter = new ShopAdapter(ShopContainer.this, foundItems);
                            searchRecyclerContainer.setAdapter(adapter);
                              searchRecyclerContainer.setVisibility(View.VISIBLE);
                        }
                    } else {
                        searchRecyclerContainer.setVisibility(View.GONE);
                        tabLayout.setVisibility(View.VISIBLE);
                        emptytext.setVisibility(View.GONE);

                        viewPager.setVisibility(View.VISIBLE);
                        closeKeyboard();
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }
            };

            searchView.findViewById(androidx.appcompat.R.id.search_close_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchView.setQuery("", false);
                    searchView.clearFocus();
                     searchRecyclerContainer.setVisibility(View.GONE);
                    tabLayout.setVisibility(View.VISIBLE);
                    emptytext.setVisibility(View.GONE);

                    viewPager.setVisibility(View.VISIBLE);
                    closeKeyboard();
                }
            });
            searchView.setOnQueryTextListener(textChangeListener);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
         return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cart) {
            startActivity(new Intent(ShopContainer.this, CartActivity.class));
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager)  getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}