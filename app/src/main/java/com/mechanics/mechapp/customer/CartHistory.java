package com.mechanics.mechapp.customer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.mechanics.mechapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CartHistory extends Fragment {

    private CartHistoryAdapter adapter;
    private DatabaseReference databaseReference;
    private ArrayList<String> keyArray;

    static  String[] bbb, ccc, eee;
    static Integer[] ddd;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.cart_fragment_history_page, container, false);

        final RecyclerView recyclerView = view.findViewById(R.id.cartHistoryRecycler);
        final ProgressBar progressBar = view.findViewById(R.id.progress44);
        progressBar.setVisibility(View.VISIBLE);

        final String uid = FirebaseAuth.getInstance().getUid();

        assert uid != null;
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Cart Collection").child(uid);

        final LinearLayout empty_list = view.findViewById(R.id.no_cart_layout);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<CartHistoryModel> list = new ArrayList<>();
                keyArray = new ArrayList<>();

                GenericTypeIndicator< List<String>> g = new GenericTypeIndicator< List<String>>() {
                };

                GenericTypeIndicator< List<Integer>> h = new GenericTypeIndicator< List<Integer>>() {
                };
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                     List<String> t1 = dataSnapshot1.child("Product List").getValue(g);
                     List<String> t2 = dataSnapshot1.child("Product Sellers").getValue(g);
                     List<String> t13 = dataSnapshot1.child("Product Images").getValue(g);
                     List<Integer> t10 = dataSnapshot1.child("Product Numbers").getValue(h);

                    String t3 = dataSnapshot1.child("Total Amount Paid").getValue(String.class);
                    String t5 = dataSnapshot1.child("Customer Uid").getValue(String.class);
                    String t6 = dataSnapshot1.child("Customer Name").getValue(String.class);
                    String t7 = dataSnapshot1.child("Customer Number").getValue(String.class);
                    String t8 = dataSnapshot1.child("Street Address").getValue(String.class);
                    String t9 = dataSnapshot1.child("Customer Email").getValue(String.class);
                    String t11 = dataSnapshot1.child("Trans ID").getValue(String.class);
                    String t12 = dataSnapshot1.child("City").getValue(String.class);
                    String t14 = dataSnapshot1.child("Trans Status").getValue(String.class);

                     list.add(new CartHistoryModel(t6, t3, t2, t5, t9, t7, t8, t1, t10, t11, t12, t13, t14));
                    keyArray.add(dataSnapshot1.getKey());
                }

                if (list.size() > 0) {
                    adapter = new CartHistoryAdapter(getContext(), list);
                    progressBar.setVisibility(View.INVISIBLE);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.setAdapter(adapter);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    empty_list.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    public class CartHistoryAdapter extends RecyclerView.Adapter<CartHistoryAdapter.MyViewHolder> {

        private Context context;
        private final ArrayList<CartHistoryModel> mData;

        CartHistoryAdapter(Context context, ArrayList<CartHistoryModel> mArrayL) {
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
            String s = mData.get(position).getItemNames().toString();
            String s1 = mData.get(position).getItemSellers().toString();
            String s2 = mData.get(position).getStatus();
            holder.itemName.setText(s.substring(1, s.length() - 1));
            holder.itemSeller.setText(s1.substring(1, s1.length() - 1));
            holder.numberOf.setTextSize(20);
            holder.numberOf.setText(s2);

            holder.thePrice.setText(String.format("₦%s.", Math.round(Double.parseDouble(mData.get(position).getPrice()))));

            if (s2.equals("Processing") || s2.equals("En Route")) {
                holder.numberOf.setTextColor(Color.RED);
                holder.deleteItem.setVisibility(View.GONE);
            } else {
                holder.numberOf.setTextColor(Color.GREEN);
                holder.deleteItem.setVisibility(View.VISIBLE);
            }

            if (mData.get(position).getItemImages().get(0) == null) {
                holder.itemImage.setImageResource(R.drawable.placeholder);
            } else {
                Picasso.get().load(mData.get(position).getItemImages().get(0)).placeholder(R.drawable.placeholder).into(holder.itemImage);
            }

            holder.add.setVisibility(View.GONE);
            holder.remove.setVisibility(View.GONE);

            holder.deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Confirmation");
                    builder.setMessage("Are you sure you want to delete the Delivered Item?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            databaseReference.child(keyArray.get(position)).removeValue();
                            adapter.notifyDataSetChanged();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(getContext(), CartItemActivity.class);

                    String[] a = {mData.get(position).getPrice(), mData.get(position).getStreetname(),
                            mData.get(position).getCusName(), mData.get(position).getCusUid(),
                            mData.get(position).getCusNumber(), mData.get(position).getCusEmail(),
                            mData.get(position).getTransId(), mData.get(position).getStatus(),
                            mData.get(position).getCity()};

                    String[] b = new String[mData.get(position).getItemSellers().size()];
                    b = mData.get(position).getItemSellers().toArray(b);

                    String[] c = new String[mData.get(position).getItemImages().size()];
                   c = mData.get(position).getItemImages().toArray(c);

                    Integer[] d = new Integer[mData.get(position).getItemNumbers().size()];
                    d = mData.get(position).getItemNumbers().toArray(d);

                    String[] e = new String[mData.get(position).getItemNames().size()];
                    e = mData.get(position).getItemNames().toArray(e);

                    in.putExtra("itemOtherDetails", a);
                    in.putExtra("itemSellers", b);
                    in.putExtra("itemsImages", c);
                    in.putExtra("itemsNum", d);
                    in.putExtra("itemsNames", e);

                     startActivity(in);
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
                deleteItem = itemView.findViewById(R.id.delete_item);
                itemImage = itemView.findViewById(R.id.itemImageB);
            }
        }
    }

}
