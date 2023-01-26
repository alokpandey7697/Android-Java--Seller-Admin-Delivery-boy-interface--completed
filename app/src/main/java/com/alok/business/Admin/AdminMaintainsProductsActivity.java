package com.alok.business.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alok.business.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AdminMaintainsProductsActivity extends AppCompatActivity {

    private Button applyChangesBtn,deleteBtn;
    private EditText name, price, description;
    private ImageView imageView;

    private String productID="";
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintains_products);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Admin Edit");
        productID=getIntent().getStringExtra("pid");
        productsRef= FirebaseDatabase.getInstance().getReference().child("products").child(productID);

        applyChangesBtn = findViewById(R.id.apply_changes_btn);
        name=findViewById(R.id.product_name_maintains);
        price=findViewById(R.id.product_Price_maintains);
        description=findViewById(R.id.product_Description_maintains);
        imageView=findViewById(R.id.product_image_maintains);
        deleteBtn=findViewById(R.id.delete_products_btn);
        displaySpecificProductInfo();

        applyChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyChanges();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteThisProduct();
            }
        });

    }
    private void deleteThisProduct()
    {
        productsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
//                Intent intent=new Intent(AdminMaintainsProductsActivity.this, SellerProductCategoryActivity.class);
//                startActivity(intent);
//                finish();

                Toast.makeText(AdminMaintainsProductsActivity.this, "The product is deleted successfully", Toast.LENGTH_SHORT).show();

           finish();

            }
        });
    }


    private  void applyChanges()
    {
        String pName = name.getText().toString();
        String pPrice = price.getText().toString();
        String pDescripion = description.getText().toString();

        if(pName.equals(""))
        {
            Toast.makeText(this, "Write down product name", Toast.LENGTH_SHORT).show();
        }
       else  if(pPrice.equals(""))
        {
            Toast.makeText(this, "Write down product Price", Toast.LENGTH_SHORT).show();
        }
       else if(pDescripion.equals(""))
        {
            Toast.makeText(this, "Write down product description", Toast.LENGTH_SHORT).show();
        }
       else
        {
            HashMap<String,Object> productMap=new HashMap<>();
            productMap.put("pid",productID);
            productMap.put("price",pPrice);
            productMap.put("pname",pName);
            productMap.put("description",pDescripion);

            productsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(AdminMaintainsProductsActivity.this, "Changes applied successfully", Toast.LENGTH_SHORT).show();
finish();
//                        Intent intent=new Intent(AdminMaintainsProductsActivity.this, SellerProductCategoryActivity.class);
//                        startActivity(intent);
//                        finish();
                    }
                }
            });

        }

    }

    private void displaySpecificProductInfo() {

        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String pName = dataSnapshot.child("pname").getValue().toString();
                    String pPrice = dataSnapshot.child("price").getValue().toString();
                    String pDescription = dataSnapshot.child("description").getValue().toString();
                    String pImage = dataSnapshot.child("image").getValue().toString();


                    name.setText(pName);
                    price.setText(pPrice);
                    description.setText(pDescription);
                    Glide.with(imageView).load(pImage).into(imageView);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
