package com.mechanics.mechapp.customer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.mechanics.mechapp.Database.MyCartDatabase;
import com.mechanics.mechapp.R;
import com.squareup.picasso.Picasso;

public class ShopItemActivity extends AppCompatActivity {
    String itemName_, itemSeller_, itemPrice_, itemEmail_, itemNumber_, itemDescrpt_, itemID_;
    String[] val, imageValues;
    private MyCartDatabase myDatabase;
    Context c = ShopItemActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_item_details);
        final ScrollView dd = findViewById(R.id.dd);
        getSupportActionBar().setTitle("Item Details");
        getSupportActionBar().setElevation(0.0f);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDatabase = Room.databaseBuilder(c, MyCartDatabase.class, "cartDB").allowMainThreadQueries().build();

        TextView itemName = findViewById(R.id.item_details_item_name);
        TextView itemSeller = findViewById(R.id.item_details_item_seller);
        TextView itemPrice = findViewById(R.id.item_details_item_price);
        TextView itemDescript = findViewById(R.id.item_details_item_description);
        Button addToCart = findViewById(R.id.item_details_add_to_cart_btn);

        Intent intent = getIntent();
        if (intent.hasExtra("arr")) {
            val = intent.getStringArrayExtra("arr");

            itemName_ = val[0];
            itemPrice_ = val[1];
            itemSeller_ = val[2];
            itemEmail_ = val[3];
            itemNumber_ = val[4];
            itemDescrpt_ = val[5];
            itemID_ = val[6];
        }
        if (intent.hasExtra("images")) {
            imageValues = intent.getStringArrayExtra("images");
        }

        itemName.setText(itemName_);
        itemSeller.setText(itemSeller_);
        itemPrice.setText(String.format("₦%s", itemPrice_));
        itemDescript.setText(itemDescrpt_);

        ViewPager vi =  findViewById(R.id.home_frag_viewpager);
        CarouselAdapter pagerAdapter = new CarouselAdapter(getSupportFragmentManager());

        for (String s : imageValues){
            Bundle bundle = new Bundle();
            bundle.putString("img", s);
            ShopCarousel shopCarousel = new ShopCarousel();
            shopCarousel.setArguments(bundle);
            pagerAdapter.addFrag(shopCarousel, "");
        }
        vi.setAdapter(pagerAdapter);

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar.make(dd, "Added to Cart", Snackbar.LENGTH_LONG).setAction("CHECKOUT", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(c, CartActivity.class));
                    }
                });
                snackbar.show();
                myDatabase.myCartDao().addPost(new CartModel(itemID_, itemName_, itemPrice_, itemSeller_, imageValues[0]));
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
}