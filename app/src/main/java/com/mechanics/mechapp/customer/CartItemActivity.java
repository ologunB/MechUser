package com.mechanics.mechapp.customer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.mechanics.mechapp.R;

import java.util.ArrayList;
import java.util.Arrays;

import static com.mechanics.mechapp.customer.CartHistory.bbb;
import static com.mechanics.mechapp.customer.CartHistory.ccc;

public class CartItemActivity extends AppCompatActivity {
    String itemPrice_, itemStreetname_, itemCusName_, itemUid_, itemNumber_, itemCity_, itemEmail_, itemID_, itemStatus_;
    String[]   itemNames, sellers, numbers, images;
    String[] otherDetails;
    Context c = CartItemActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartitem_details);
        getSupportActionBar().setTitle("Item Details");
        getSupportActionBar().setElevation(0.0f);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent.hasExtra("itemOtherDetails")) {
            otherDetails = intent.getStringArrayExtra("itemOtherDetails");
            //price
            itemPrice_ = otherDetails[0];
            // streetname
            itemStreetname_ = otherDetails[1];
            //cus name
            itemCusName_ = otherDetails[2];
            // cus uid
            itemUid_ = otherDetails[3];
            // cus number
            itemNumber_ = otherDetails[4];
            // cus email
            itemEmail_ = otherDetails[5];
            // transa id
            itemID_ = otherDetails[6];
            // status
            itemStatus_ = otherDetails[7];
            // city
            itemCity_ = otherDetails[8];
        }

          if (intent.hasExtra("itemsNames")) {
            itemNames = intent.getStringArrayExtra("itemsNames");
        }
           if (intent.hasExtra("itemSellers")) {
            sellers = intent.getStringArrayExtra("itemSellers");
        }
           if (intent.hasExtra("itemsNum")) {
            numbers = intent.getStringArrayExtra("itemsNum");
        }
           if (intent.hasExtra("itemsImages")) {
            images = intent.getStringArrayExtra("itemsImages");
        }

        TextView itemName = findViewById(R.id.item_details_item_name);
        TextView itemDescript = findViewById(R.id.item_details_item_description);

        TextView itemStatus = findViewById(R.id.item_details_item_status);
        TextView itemEmail = findViewById(R.id.item_details_item_email);
        TextView itemPhoneNo = findViewById(R.id.item_details_item_number);
        TextView itemPrice = findViewById(R.id.item_details_item_price);
        TextView itemStreetAdd = findViewById(R.id.item_details_item_address);
        TextView itemCity = findViewById(R.id.item_details_item_city);
        TextView itemSellers = findViewById(R.id.item_details_item_seller);

        itemName.setText(itemCusName_);
        itemStatus.setText(itemStatus_);
        itemEmail.setText(itemEmail_);
        itemPhoneNo.setText(itemNumber_);
        itemStreetAdd.setText(itemStreetname_);
        itemCity.setText(itemCity_);
        itemSellers.setText(Arrays.asList(sellers).toString());
         itemPrice.setText(String.format("₦%s", itemPrice_));

        ViewPager vi = findViewById(R.id.home_frag_viewpager);
        CarouselAdapter pagerAdapter = new CarouselAdapter(getSupportFragmentManager());

        for (String s : images) {
            Bundle bundle = new Bundle();
            bundle.putString("img", s);
            ShopCarousel shopCarousel = new ShopCarousel();
            shopCarousel.setArguments(bundle);
            pagerAdapter.addFrag(shopCarousel, "");
        }
        vi.setAdapter(pagerAdapter);

     ArrayAdapter adapter = new ArrayAdapter<>(CartItemActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, itemNames);

        ListView listView = findViewById(R.id._listView);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}