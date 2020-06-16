package com.mechanics.mechapp.customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mechanics.mechapp.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class Carousel2 extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipe_fragment_one, container, false);
        ImageView lv = view.findViewById(R.id.swipe1_image);
        lv.setImageResource(R.drawable.cc2);
        return view;
    }
}
