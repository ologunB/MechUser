package com.mechanics.mechapp.customer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mechanics.mechapp.Database.MyCartDatabase;
import com.mechanics.mechapp.R;
import com.squareup.picasso.Picasso;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.mechanics.mechapp.RandomStringHelper.presentTimeString;
import static com.mechanics.mechapp.RandomStringHelper.randomString;

public class CartFragment extends Fragment {
    private ExpandableLayout expandableLayout1, expandableLayout2;
    private ImageView selector1, selector2;
    private MyCartDatabase myDatabase;
    private List<CartModel> list;
    private CartFragmentAdapter adapter;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private TextView s1, charge1, total1;
    private double sub22 = 0;
    private TextInputEditText t1, t2, t3;
    private String cusEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private String currentUserName;
    private List<Integer> numberOfCartItems;
    private String payTransactID;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cart_fragment_page, container, false);
        myDatabase = Room.databaseBuilder(getContext(), MyCartDatabase.class, "cartDB").allowMainThreadQueries().build();

        payTransactID = randomString();
        RecyclerView recyclerView = view.findViewById(R.id.cartRecycler);
        s1 = view.findViewById(R.id.cartSubTotal);
        charge1 = view.findViewById(R.id.cartCharge);
        total1 = view.findViewById(R.id.cartTotal);
        selector1 = view.findViewById(R.id.selector1);
        selector2 = view.findViewById(R.id.selector2);
        list = new ArrayList<>();
        list = myDatabase.myCartDao().getItems();
        Set<CartModel> l = new HashSet<>(list);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TypeFile", Context.MODE_PRIVATE);
        numberOfCartItems = new ArrayList<>();

        currentUserName = sharedPreferences.getString("Name", null);

        adapter = new CartFragmentAdapter(getContext(), new ArrayList<>(l));
        LinearLayout l1 = view.findViewById(R.id.empty_cart_layout);
        LinearLayout l2 = view.findViewById(R.id.full_cart);

        if (list.size() == 0) {
            l1.setVisibility(View.VISIBLE);
            l2.setVisibility(View.GONE);
        } else {

            recyclerView.setAdapter(adapter);
            l1.setVisibility(View.GONE);
            l2.setVisibility(View.VISIBLE);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        expandableLayout1 = view.findViewById(R.id.expandable_layout1);
        expandableLayout2 = view.findViewById(R.id.expandable_layout2);
        view.findViewById(R.id.linearHeader1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandableLayout1.isExpanded()) {
                    expandableLayout1.collapse();
                    selector1.setImageResource(R.drawable.ic_expand_more_black_24dp);
                } else {
                    expandableLayout1.expand();
                    selector1.setImageResource(R.drawable.ic_expand_less_black_24dp);
                }
            }
        });

        view.findViewById(R.id.linearHeader2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandableLayout2.isExpanded()) {
                    expandableLayout2.collapse();
                    selector2.setImageResource(R.drawable.ic_expand_more_black_24dp);
                } else {
                    expandableLayout2.expand();
                    selector2.setImageResource(R.drawable.ic_expand_less_black_24dp);
                }
            }
        });

        new Calc1().execute(list);

        t1 = view.findViewById(R.id.pt_user_phone_number);
        t2 = view.findViewById(R.id.pt_user_streetname);
        t3 = view.findViewById(R.id.pt_user_city);

        Button payForItem = view.findViewById(R.id.buy_shop_item);

            payForItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (t1.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Phone Number field is Empty!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (t2.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Street Address cannot be Empty!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (t3.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "City Field is Empty!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (sub22 == 0) {
                    Toast.makeText(getContext(), "Empty Cart!", Toast.LENGTH_LONG).show();
                    return;
                }
                new RavePayManager(getActivity())
                        .setAmount(sub22 * 1.1)
                        .setCountry("NG")
                        .setCurrency("NGN")
                        .setEmail(cusEmail)
                        .setfName(currentUserName)
                        .setlName(t1.getText().toString())
                        .setNarration("Payment For Items")
                       .setPublicKey("FLWPUBK-37eaceebb259b1537c67009339575c01-X")
                      // demo  .setPublicKey("FLWPUBK_TEST-9ba09916a6e4e8385b9fb2036439beac-X")
                       .setEncryptionKey("ab5cfe0059e5253250eb68a4")
                      //demo  .setEncryptionKey("FLWSECK_TEST3ba765b74b1f")
                        .setTxRef(payTransactID.toString())
                      //  .acceptAccountPayments(true)
                        .acceptCardPayments(true)
                       // .acceptUssdPayments(true)
                       // .acceptBankTransferPayments(true)
                        .onStagingEnv(false)
                        .shouldDisplayFee(true)
                        .showStagingLabel(false)
                        .withTheme(R.style.CustomThemeForRave)
                        .initialize();
            }
        });

        progressDialog = new ProgressDialog(getContext());
        databaseReference = FirebaseDatabase.getInstance().getReference();

        return view;
    }

    private void DoAfterSuccess(String serverData) {
        progressDialog.setMessage("Finalizing...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        List<String> productListName = new ArrayList<>();
        List<String> productListImages = new ArrayList<>();
        List<String> productListSellers = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            productListName.add(list.get(i).getName() + "_" + numberOfCartItems.get(i));
            productListImages.add(list.get(i).getImage());
            productListSellers.add(list.get(i).getSeller());
        }

        final String uid = FirebaseAuth.getInstance().getUid();

        final Map<String, Object> valuesToCustomer = new HashMap<>();
        valuesToCustomer.put("Customer Name", currentUserName);
        valuesToCustomer.put("Customer Number", t1.getText().toString());
        valuesToCustomer.put("Customer Email", cusEmail);
        valuesToCustomer.put("Customer Uid", uid);
        valuesToCustomer.put("Product List", productListName);
        valuesToCustomer.put("Product Sellers", productListSellers);
        valuesToCustomer.put("Product Numbers", numberOfCartItems);
        valuesToCustomer.put("Product Images", productListImages);
        int roundVal= (int) Math.round(sub22 * 1.1);
        valuesToCustomer.put("Total Amount Paid", "" + roundVal);
        valuesToCustomer.put("Street Address", t2.getText().toString());
        valuesToCustomer.put("City", t3.getText().toString());
        valuesToCustomer.put("Trans Time", presentTimeString());
        valuesToCustomer.put("Server Confirmation", serverData);
        valuesToCustomer.put("Trans Description", "Payment for Items");
        valuesToCustomer.put("Trans ID", payTransactID);
        valuesToCustomer.put("Trans Status", "Processing");
        valuesToCustomer.put("Timestamp",  Calendar.getInstance().getTimeInMillis());

        String made = "You have made a payment of ₦" + sub22 * 1.1 +
                " and has been withdrawn from your Card. Thanks for using FABAT";

        final Map<String, Object> sentMessage = new HashMap<>();
        sentMessage.put("notification_message", made);
        sentMessage.put("notification_time", presentTimeString());
        sentMessage.put("Timestamp",  Calendar.getInstance().getTimeInMillis());

        databaseReference.child("Cart Collection").child(uid)
                .child(payTransactID).setValue(valuesToCustomer)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.setMessage("Paying now and clarifying things...");
                        databaseReference.child("Notification Collection").child("Customer").child(uid).push().setValue(sentMessage).
                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        databaseReference.child("Cart Transactions").child("Ordered Items").child(uid).child(payTransactID).setValue(valuesToCustomer);
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "SUCCESSFUL, Check your Orders for progress or notifications", Toast.LENGTH_SHORT).show();
                                        myDatabase.myCartDao().deleteAll();


                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            builder.setView(R.layout.confirmed_xml);
                                        }
                                        builder.setCancelable(false);
                                        builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                startActivity(new Intent(getContext(), MainActivity.class));
                                            }
                                        });
                                        final AlertDialog dialog = builder.create();
                                        dialog.show();
                                        Toast.makeText(getContext(), "Successful Transaction", Toast.LENGTH_LONG).show();

                                    }
                                });
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                DoAfterSuccess(message);
            } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(getContext(), "ERROR ", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(getContext(), "CANCELLED ", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    class Calc1 extends AsyncTask<List<CartModel>, Void, Double> {

        @Override
        protected Double doInBackground(List<CartModel>... lists) {
            double _total = 0;

            for (int i = 0; i < lists[0].size(); i++) {
                _total = _total + Double.parseDouble(lists[0].get(i).getPrice());
            }
            return _total;
        }

        @Override
        protected void onPostExecute(Double aDouble) {
            sub22 = aDouble;
            s1.setText(String.format("₦%s", Math.round(sub22)));
            charge1.setText(String.format("₦%s", Math.round(sub22 * 0.1)));
            total1.setText(String.format("₦%s", Math.round(sub22 * 1.1)));
            super.onPostExecute(aDouble);
        }
    }

    public class CartFragmentAdapter extends RecyclerView.Adapter<CartFragmentAdapter.MyViewHolder> {

        private Context context;
        private final List<CartModel> mData;

        CartFragmentAdapter(Context context, List<CartModel> mArrayL) {
            this.context = context;
            this.mData = mArrayL;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.cartitem_viewholder, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
            holder.itemName.setText(mData.get(position).getName());
            holder.thePrice.setText(String.format("₦%s", mData.get(position).getPrice()));
            holder.itemSeller.setText(mData.get(position).getSeller());

            numberOfCartItems.add(1);

            if (mData.get(position).getImage() == null) {
                holder.itemImage.setImageResource(R.drawable.placeholder);
            } else {
                Picasso.get().load(mData.get(position).getImage()).placeholder(R.drawable.placeholder).into(holder.itemImage);
            }

            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int[] no5 = {Integer.parseInt(holder.numberOf.getText().toString())};
                    if (no5[0] < 10) {
                        holder.numberOf.setText(String.valueOf(no5[0] + 1));
                        sub22 = sub22 + Double.parseDouble(mData.get(position).getPrice());
                        s1.setText(String.format("₦%s", Math.round(sub22)));
                        charge1.setText(String.format("₦%s", Math.round(sub22 * 0.1)));
                        total1.setText(String.format("₦%s", Math.round(sub22 * 1.1)));

                        numberOfCartItems.set(position, numberOfCartItems.get(position) + 1);
                    }
                }
            });

            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int[] no5 = {Integer.parseInt(holder.numberOf.getText().toString())};

                    if (no5[0] > 1) {
                        holder.numberOf.setText(String.valueOf(no5[0] - 1));

                        sub22 = sub22 - Double.parseDouble(mData.get(position).getPrice());
                        s1.setText(String.format("₦%s", Math.round(sub22)));
                        charge1.setText(String.format("₦%s", Math.round(sub22 * 0.1)));
                        total1.setText(String.format("₦%s", Math.round(sub22 * 1.1)));

                        numberOfCartItems.set(position, numberOfCartItems.get(position) - 1);
                    }
                }
            });

            holder.deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Confirmation!");
                    builder.setMessage("Do you want to delete the Item?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            myDatabase.myCartDao().delete(mData.get(position));

                            list.remove(mData.get(position));
                            numberOfCartItems.remove(position);

                            sub22 = sub22 - Double.parseDouble(mData.get(position).getPrice()) * Integer.parseInt(holder.numberOf.getText().toString());
                            s1.setText(String.format("₦%s", Math.round(sub22)));
                            charge1.setText(String.format("₦%s", Math.round(sub22 * 0.1)));
                            total1.setText(String.format("₦%s", Math.round(sub22 * 1.1)));

                            holder.itemView.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();

                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onResume();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView numberOf, itemName, thePrice, itemSeller;
            ImageView add, remove, itemImage, deleteItem;

            private MyViewHolder(View itemView) {

                super(itemView);
                numberOf = itemView.findViewById(R.id.numberOfB);
                itemName = itemView.findViewById(R.id.itemNameB);
                thePrice = itemView.findViewById(R.id.itemPriceB);
                itemSeller = itemView.findViewById(R.id.itemSellerB);
                add = itemView.findViewById(R.id.itemAddB);
                remove = itemView.findViewById(R.id.itemRemoveB);
                itemImage = itemView.findViewById(R.id.itemImageB);
                deleteItem = itemView.findViewById(R.id.delete_item);
            }
        }
    }
}