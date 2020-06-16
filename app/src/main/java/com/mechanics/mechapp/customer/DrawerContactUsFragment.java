package com.mechanics.mechapp.customer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;

import com.mechanics.mechapp.R;
import com.mechanics.mechapp.mechanic.MechMainActivity;

public class DrawerContactUsFragment extends Fragment {
    private String currentUserName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
         ((MainActivity) getActivity()).getSupportActionBar().setTitle("Contact Us");
         super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_contactus, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TypeFile", Context.MODE_PRIVATE);
        currentUserName = sharedPreferences.getString("Name", null);

        final EditText message = view.findViewById(R.id.ab_message);
        Button sendCon = view.findViewById(R.id.sendContact);

        ImageView fb = view.findViewById(R.id.fb1);
        ImageView in = view.findViewById(R.id.in1);
        ImageView tw = view.findViewById(R.id.tw1);

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                try {
                    getContext().getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/100039244757529"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } catch (Exception e) {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/fabat.mngt.1"));
                }
                startActivity(intent);
            }
        });
        tw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                try {
                    getContext().getPackageManager().getPackageInfo("com.twitter.android", 0);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=fabatmanagement"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } catch (Exception e) {
                     intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/fabatmanagement?s=09"));
                }
                 startActivity(intent);

            }
        });
        in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://instagram.com/_u/officialfabatmngt");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/officialfabatmngt")));
                }
            }
        });

        sendCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String c = message.getText().toString();
                String d = "info@fabatmanagement.com";

                if(c.isEmpty()){
                    Toast.makeText(getContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                ShareCompat.IntentBuilder.from(getActivity()).setType("message/rfc822")
                        .addEmailTo(d).setSubject("Message from " + currentUserName).setText(c)
                        .setChooserTitle("Send Email").startChooser();
            }
        });

        return view;
    }
}