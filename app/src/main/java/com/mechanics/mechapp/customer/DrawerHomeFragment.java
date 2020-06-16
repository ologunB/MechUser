package com.mechanics.mechapp.customer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.mechanics.mechapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.mechanics.mechapp.customer.MainActivity.checkLocationON;

public class DrawerHomeFragment extends Fragment {
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private CollectionReference mechanicCollection;
    private ImageView cancelSearch;
    private EditText searchEdit;
    private RecyclerView searchRecycler;
    private RecyclerView serviceRecycler;
    private FrameLayout searchRecyclerContainer;
    private ProgressBar searchProgressBar;
    private LinearLayout noSearchResult;
    private TextView searchLabel;
    private List<MechanicModel> modelList;
    private TextView currentLocation;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
  // ((MainActivity) getActivity()).getSupportActionBar().setTitle("Home");
         super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
       ((MainActivity) getActivity()).getSupportActionBar().setTitle("Home");

        super.onViewCreated(view, savedInstanceState);
    }

    private void BindView() {
        currentLocation = view.findViewById(R.id.home_frag_current_location);
        serviceRecycler = view.findViewById(R.id.home_frag_service_recycler);
        searchRecycler = view.findViewById(R.id.home_frag_search_recycler);
        searchRecyclerContainer = view.findViewById(R.id.home_frag_search_recycler_container);
        searchProgressBar = view.findViewById(R.id.home_frag_search_progress_bar);
        noSearchResult = view.findViewById(R.id.home_frag_search_no_results);
        searchLabel = view.findViewById(R.id.home_frag_search_label);

        cancelSearch = view.findViewById(R.id.home_frag_cancel_search);
        searchEdit = view.findViewById(R.id.home_frag_search_edit_text);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.the_home_fragment, container, false);

        BindView();

        ViewPager vi = view.findViewById(R.id.home_frag_viewpager);
        CarouselAdapter pagerAdapter = new CarouselAdapter(getChildFragmentManager());
        pagerAdapter.addFrag(new Carousel1(), "");
        pagerAdapter.addFrag(new Carousel1(), "");
        pagerAdapter.addFrag(new Carousel2(), "");
        pagerAdapter.addFrag(new Carousel3(), "");
        pagerAdapter.addFrag(new Carousel4(), "");
        pagerAdapter.addFrag(new Carousel7(), "");
        pagerAdapter.addFrag(new Carousel5(), "");
        pagerAdapter.addFrag(new Carousel6(), "");
        pagerAdapter.addFrag(new Carousel7(), "");
        pagerAdapter.addFrag(new Carousel7(), "");
        vi.setAdapter(pagerAdapter);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        modelList = new ArrayList();

        mechanicCollection = FirebaseFirestore.getInstance().collection("Mechanics");

        searchRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        searchRecycler.setHasFixedSize(true);

        new SearchMech().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        cancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEdit.setText("");
                closeKeyboard();
            }
        });

        ServicesAdapter adapter = new ServicesAdapter(getContext(), new ServicesData().getList());
        serviceRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        serviceRecycler.setAdapter(adapter);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    109);
        } else {
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isComplete()) {
                        Location gottenLocation = (Location) task.getResult();
                        new GetLocationTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, gottenLocation);
                    } else {
                        Toast.makeText(getActivity(), "An error occurred! Check your internet connection and try again", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String changedText = s.toString().trim();
                List<MechanicModel> foundMechanics = new ArrayList();

                if (!changedText.isEmpty()) {
                    searchRecyclerContainer.setVisibility(View.VISIBLE);
                    searchProgressBar.setVisibility(View.VISIBLE);
                    searchRecycler.setVisibility(View.GONE);
                    serviceRecycler.setVisibility(View.GONE);
                    cancelSearch.setVisibility(View.VISIBLE);

                    for (MechanicModel mechanic : modelList) {
                        if (mechanic.getCompanyName().toLowerCase().contains(changedText.toLowerCase())) {
                            foundMechanics.add(mechanic);
                        }
                    }

                    if (foundMechanics.isEmpty()) {
                        searchLabel.setText("Services");

                        searchProgressBar.setVisibility(View.GONE);
                        noSearchResult.setVisibility(View.VISIBLE);
                        searchRecycler.setVisibility(View.GONE);
                        serviceRecycler.setVisibility(View.GONE);
                    } else {
                        searchLabel.setText("Matched Company Names");

                        ServiceItemAdapter adapter = new ServiceItemAdapter(foundMechanics, getContext());
                        searchRecycler.setAdapter(adapter);
                        searchRecycler.setVisibility(View.VISIBLE);

                        searchProgressBar.setVisibility(View.GONE);
                        noSearchResult.setVisibility(View.GONE);
                        serviceRecycler.setVisibility(View.GONE);
                    }

                } else {
                    searchLabel.setText("Services");

                    searchRecyclerContainer.setVisibility(View.GONE);
                    searchRecycler.setVisibility(View.GONE);
                    serviceRecycler.setVisibility(View.VISIBLE);
                    cancelSearch.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        checkLocationON(getActivity());

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[grantResults.length - 1] == PackageManager.PERMISSION_GRANTED) {

                @SuppressLint("MissingPermission")
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isComplete()) {
                            Location gottenLocation = (Location) task.getResult();
                            new GetLocationTask().execute(gottenLocation);

                        } else {
                            Toast.makeText(getActivity(), "An error occurred! Check your internet connection and try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        }
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getActivity().getCurrentFocus();
        if (view == null) {
            view = new View(getContext());
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    class GetLocationTask extends AsyncTask<Location, Void, String> {

        @Override
        protected String doInBackground(Location... objects) {
            Location gotten = objects[0];
            List<Address> addresses;

            if (gotten != null) {
                try {
                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    addresses = geocoder.getFromLocation(gotten.getLatitude(), gotten.getLongitude(), 1);
                    return addresses.get(0).getAddressLine(0);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            currentLocation.setText(s);
        }
    }

    class SearchMech extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            mechanicCollection.addSnapshotListener(
                    new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (queryDocumentSnapshots != null) {
                                queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                    if (document != null) {
                                        MechanicModel model = MechanicData.convertDocumentToDriverModel(document);
                                        modelList.add(model);
                                    }
                                }
                            }
                        }
                    });
            return null;
        }
    }
}