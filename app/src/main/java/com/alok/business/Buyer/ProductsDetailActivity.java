package com.alok.business.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.alok.business.Prevalent.Prevalent;
import com.alok.business.R;
import com.alok.business.models.Products;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductsDetailActivity extends AppCompatActivity {

    private Button addToCartBtn;
    private ImageView productImage;
    private ElegantNumberButton numberbutton;
    private TextView productprice,productname,productdescription;
    private String productID="",state="Normal";
    private String sellerAddress,downloadimgeurl,sid,lattitude,longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_products_detail);
        productID=getIntent().getStringExtra("pid");
        addToCartBtn=(Button)findViewById(R.id.pd_add_to_cart);
        numberbutton=(ElegantNumberButton)findViewById(R.id.numberBtn);
        productImage=(ImageView)findViewById(R.id.product_image_details);
        productprice=(TextView)findViewById(R.id.product_price_details);
        productname=(TextView)findViewById(R.id.product_name_details);
        productdescription=(TextView)findViewById(R.id.product_description_details);

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                if(state.equals("Order Placed") || state.equals("Order Shipped"))
//                {
//                    Toast.makeText(ProductsDetailActivity.this, "You can purchasee more products once your order is shipped or confirmed", Toast.LENGTH_LONG).show();
//                }
//                else
//                {
                    addingTOCartList();
                //}
            }
        });

        getProductDetails(productID);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //CheckOrderState();
    }

    private void addingTOCartList() {
        String savecurrentDate,saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        savecurrentDate=currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calForDate.getTime());

        DatabaseReference cartListRef=FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String,Object>cartMap=new HashMap<>();
        cartMap.put("pid",productID);
        cartMap.put("pname",productname.getText().toString());
        cartMap.put("price",productprice.getText().toString());
        cartMap.put("date",savecurrentDate);
        cartMap.put("time",saveCurrentTime);
        cartMap.put("quantity",numberbutton.getNumber());
        cartMap.put("discount","");
        cartMap.put("lattitude",lattitude);
        cartMap.put("longitude",longitude);

        cartMap.put("sellerAddress",sellerAddress);
        cartMap.put("image",downloadimgeurl);
        cartMap.put("sid",sid);
        cartListRef.child("User view").child(Prevalent.currentOnlineUsers.getPhone()).child("Products").child(productID)
                .updateChildren(cartMap)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(ProductsDetailActivity.this,"Added to cart list",Toast.LENGTH_SHORT).show();
                    finish();
//                    cartListRef.child("Admin view").child(Prevalent.currentOnlineUsers.getPhone()).child("Products").child(productID)
//                            .updateChildren(cartMap)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if(task.isSuccessful())
//                                    {
//                                        Toast.makeText(ProductsDetailActivity.this,"Added to cart list",Toast.LENGTH_SHORT).show();
//                                        Intent intent=new Intent(ProductsDetailActivity.this, HomeActivity.class);
//                                        startActivity(intent);
//                                    }
//                                }
//                            });
                }
            }
        });



    }

    private void getProductDetails(String productID) {
        DatabaseReference productreference = FirebaseDatabase.getInstance().getReference().child("products");
        productreference.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Products products=dataSnapshot.getValue(Products.class);
                productname.setText(products.getPname());
                productprice.setText(products.getPrice());
                productdescription.setText(products.getDescription());
                sellerAddress = products.getSellerAddress();
                lattitude = products.getLattitude();
                longitude = products.getLongitude();
                downloadimgeurl= products.getImage();
                sid=products.getSid();
                Glide.with(productImage).load(products.getImage()).into(productImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void CheckOrderState() {
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUsers.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    String username = dataSnapshot.child("name").getValue().toString();

                    if (shippingState.equals("shipped")) {
                        state="Order Shipped";
                    } else if (shippingState.equals("not shipped")) {
                        state="Order Placed";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
