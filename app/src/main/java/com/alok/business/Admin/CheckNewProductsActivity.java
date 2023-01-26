package com.alok.business.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alok.business.Notification.SendNotif;
import com.alok.business.R;
import com.alok.business.ViewHolder.ProductViewHolder;
import com.alok.business.models.Products;
import com.alok.business.models.shopModel;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CheckNewProductsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private DatabaseReference unverifiedSeller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_new_products);
getSupportActionBar().setTitle("Approve new seller");
        unverifiedSeller = FirebaseDatabase.getInstance().getReference().child("Seller");
        recyclerView=findViewById(R.id.admin_products_check_list);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<shopModel>options = new FirebaseRecyclerOptions.Builder<shopModel>()
                .setQuery(unverifiedSeller.orderByChild("status").equalTo("NotApproved"),shopModel.class).build();
        FirebaseRecyclerAdapter<shopModel, MyViewHolder> adapter=new FirebaseRecyclerAdapter<shopModel, MyViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull shopModel profiles) {



                holder.category.setText(profiles.getCategory());
                holder.address.setText(profiles.getAddress());
                holder.email.setText(profiles.getPhone());
                holder.name.setText(profiles.getName());


                Glide.with(holder.image).load(profiles.getImage()).into(holder.image);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        CharSequence options[] = new CharSequence[]
                                {
                                        "Yes",
                                        "No"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CheckNewProductsActivity.this);
                        builder.setTitle(("Approve seller ? "));

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==0)
                                {
                                    HashMap<String,Object> productMap=new HashMap<>();
                                    productMap.put("status","Approved");

                                    unverifiedSeller.child(getRef(position).getKey()).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                SendNotif.sendNotification("SellerTokens",profiles.getSid(),"VireStore","Account Approved","ACCOUNT APPROVED", getApplicationContext());

                                            }
                                        }
                                    });
                                }
                                if(which == 1)
                                {
                                 dialog.cancel();
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_list_row, parent, false);
                MyViewHolder holder = new MyViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView name,email,phone,address,longitude,lattitude,category;
        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            email = (TextView) itemView.findViewById(R.id.email);
            address = (TextView) itemView.findViewById(R.id.address);
            category = (TextView) itemView.findViewById(R.id.category);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
        public void onClick(final int position)
        {

        }
    }
    }

