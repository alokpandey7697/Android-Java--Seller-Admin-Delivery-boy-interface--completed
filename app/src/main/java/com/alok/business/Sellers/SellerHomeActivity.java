package com.alok.business.Sellers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.alok.business.Admin.AdminMaintainsProductsActivity;
import com.alok.business.Buyer.MainActivity;
import com.alok.business.R;
import com.alok.business.ViewHolder.ItemViewHolder;
import com.alok.business.here.Main_v;
import com.alok.business.models.Products;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SellerHomeActivity extends AppCompatActivity {
    private TextView mTextMessage;

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProducts;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intentHome = new Intent(SellerHomeActivity.this, SellerHomeActivity.class);
                    startActivity(intentHome);

                    return true;


                case R.id.navigation_add:
                    Intent intentcat = new Intent(SellerHomeActivity.this, SellerProductCategoryActivity.class);
                    startActivity(intentcat);

                    return true;


                case R.id.navigation_logout:
                    final FirebaseAuth auth;
                    auth = FirebaseAuth.getInstance();
                    auth.signOut();
                    Intent intentmain = new Intent(SellerHomeActivity.this, MainActivity.class);
                    intentmain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentmain);
                    finish();

                    return true;

                case R.id.navigation_order:
                    Intent intenta = new Intent(SellerHomeActivity.this, all_orders.class);
                    startActivity(intenta);

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
        setContentView(R.layout.activity_seller_home);

        getSupportActionBar().setTitle("Seller Home");


        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        unverifiedProducts = FirebaseDatabase.getInstance().getReference().child("products");
        recyclerView=findViewById(R.id.seller_home_recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(unverifiedProducts.orderByChild("sid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()),Products.class).build();
        FirebaseRecyclerAdapter<Products,ItemViewHolder > adapter=new FirebaseRecyclerAdapter<Products, ItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Products model) {
                holder.txtProductName.setText(model.getPname());
                holder.txtProductDescription.setText(model.getDescription());
                holder.txtProductPrice.setText( " " + model.getPrice() );
                holder.txtProductCategory.setText(model.getCategory());
                Glide.with(holder.imageView).load(model.getImage()).into(holder.imageView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String productID = model.getPid();

                        CharSequence options[] = new CharSequence[]
                                {
                                        "EDIT",
                                        "REMOVE"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(SellerHomeActivity.this);
                        builder.setTitle("Do you want to delete this Product. Are you sure?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==0)
                                {
                                    Intent intent=new Intent(SellerHomeActivity.this, SellerMaintainsProductsActivitty.class);
                                    intent.putExtra("pid",productID);
                                    startActivity(intent);

                                }
                                if(which==1)
                                {
                                    DeleteProduct(productID);
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_item_view_temp, parent, false);
                ItemViewHolder holder = new ItemViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void DeleteProduct(String productID) {
        unverifiedProducts.child(productID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(SellerHomeActivity.this, "Item has been deleted successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
