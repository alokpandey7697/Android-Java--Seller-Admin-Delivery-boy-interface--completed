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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alok.business.Buyer.HomeActivity;
import com.alok.business.Buyer.LoginActivity;
import com.alok.business.Buyer.MainActivity;
import com.alok.business.Buyer.ProductsDetailActivity;
import com.alok.business.Notification.SendNotif;
import com.alok.business.R;
import com.alok.business.Sellers.SellerHomeActivity;
import com.alok.business.Sellers.SellerProductCategoryActivity;
import com.alok.business.ViewHolder.ProductViewHolder;
import com.alok.business.models.Products;
import com.alok.business.models.shopModel;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AdminHomeActivity extends AppCompatActivity {

     private Button LogOutBtn, checkOrdersbtn,maintainProductsBtn,approveProducts;

    private DatabaseReference shopRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private String type="";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intentHome = new Intent(AdminHomeActivity.this, AdminHomeActivity.class);
                    startActivity(intentHome);

                    return true;


                case R.id.navigation_add:
                    Intent intent=new Intent(AdminHomeActivity.this, CheckNewProductsActivity.class);
                    startActivity(intent);

                    return true;


                case R.id.navigation_logout:
                    final FirebaseAuth auth;
                    auth = FirebaseAuth.getInstance();
                    auth.signOut();
                    Intent intentmain = new Intent(AdminHomeActivity.this, LoginActivity.class);
                    intentmain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentmain);
                    finish();

                    return true;

                case R.id.navigation_order:

                    Intent intentOrder=new Intent(AdminHomeActivity.this, all_orders.class);
                    startActivity(intentOrder);
                    return true;

//                case R.id.navigation_add:
//                    Intent intenta = new Intent(SellerHomeActivity.this, SellerProductCategoryActivity.class);
//                    startActivity(intenta);
//
//                    return true;


            }
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        shopRef = FirebaseDatabase.getInstance().getReference().child("Seller");
getSupportActionBar().setTitle("Admin Home");

        BottomNavigationView navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<shopModel> options =
                new FirebaseRecyclerOptions.Builder<shopModel>()
                        .setQuery(shopRef.orderByChild("status").equalTo("Approved"), shopModel.class)
                        .build();

        FirebaseRecyclerAdapter<shopModel, MyViewHolder> adapter =
                new FirebaseRecyclerAdapter<shopModel, MyViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull shopModel profiles)
                    {
                        holder.category.setText(profiles.getCategory());
                        holder.address.setText(profiles.getAddress());
                        holder.email.setText(profiles.getPhone());
                        holder.name.setText(profiles.getName());


                        Glide.with(holder.image).load(profiles.getImage()).into(holder.image);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("sid",profiles.getSid());
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
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































/*
        LogOutBtn = findViewById(R.id.logout_admin);
        checkOrdersbtn=findViewById(R.id.check_orders);
        maintainProductsBtn=findViewById(R.id.maintain_btn);
        approveProducts=findViewById(R.id.check_approve_orders);


        maintainProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminHomeActivity.this, HomeActivity.class);
                intent.putExtra("Admin","Admin");
                startActivity(intent);
            }
        });

        LogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Intent intent=new Intent(AdminHomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        checkOrdersbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminHomeActivity.this, all_orders.class);
                startActivity(intent);

            }
        });
        approveProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminHomeActivity.this, CheckNewProductsActivity.class);
                startActivity(intent);

            }
        });*/