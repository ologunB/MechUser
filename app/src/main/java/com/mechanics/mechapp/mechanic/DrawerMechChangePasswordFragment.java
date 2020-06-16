package com.mechanics.mechapp.mechanic;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mechanics.mechapp.R;

public class DrawerMechChangePasswordFragment extends Fragment {

    public DrawerMechChangePasswordFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((MechMainActivity) getActivity()).getSupportActionBar().setTitle("Change Password");
        super.onCreate(savedInstanceState);
    }

    public boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

        @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_changepassw, container, false);

        final TextInputEditText text1 = view.findViewById(R.id.ab_old_password);
        final TextInputEditText text2 = view.findViewById(R.id.ab_new_password1);
        final ProgressBar progressBar = view.findViewById(R.id.changePassProgress);
        final Button c = view.findViewById(R.id.change1);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(text1.getText().toString().isEmpty() || text2.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Empty Field", Toast.LENGTH_LONG).show();
                    return;
                }if(text2.getText().toString().length() < 6){
                    Toast.makeText(getContext(), "Password must be more than 6 characters", Toast.LENGTH_LONG).show();
                    return;
                }if(!isOnline()){
                    Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
                    return;
                }

                c.setText("Updating...");
                progressBar.setVisibility(View.VISIBLE);

                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), text1.getText().toString());

                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(text2.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Password Updated", Toast.LENGTH_LONG).show();
                                        text1.setText("");
                                        text2.setText("");
                                        progressBar.setVisibility(View.GONE);
                                        c.setText("Change Password");
                                    } else {
                                        Log.d("", "Error password not updated");
                                        Toast.makeText(getContext(), "Error! Password not updated", Toast.LENGTH_LONG).show();
                                        text1.setText("");
                                        text2.setText("");
                                        progressBar.setVisibility(View.GONE);
                                        c.setText("Change Password");                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Wrong Old Password", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            c.setText("Change Password");                        }
                    }
                });
            }
        });
        return view;
    }

}
