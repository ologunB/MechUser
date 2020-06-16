package com.mechanics.mechapp.customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.mechanics.mechapp.R;
import com.squareup.picasso.Picasso;

public class ShopCarousel extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipe_fragment_one, container, false);
        ImageView lv = view.findViewById(R.id.swipe1_image);
        lv.setImageResource(R.drawable.cc1);
        lv.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Bundle args = getArguments();
        String string = args.getString("img", "img");
        if (string == null) {
            lv.setImageResource(R.drawable.placeholder);
        } else {
            Picasso.get().load(string).placeholder(R.drawable.placeholder).into(lv);
        }
        return view;
    }
}
