package com.alok.business.Sellers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.alok.business.R;

public class SellerProductCategoryActivity extends AppCompatActivity {
    private ImageView vegetables,Grocery,Meat,tech;
    private ImageView pharmacy,Dairy,Sweats,ToyShop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_add_category);
        vegetables=(ImageView)findViewById(R.id.vegetables);
        Grocery=(ImageView)findViewById(R.id.grocery);
        Meat=(ImageView)findViewById(R.id.Meat);
        tech=(ImageView)findViewById(R.id.tech);
        ToyShop = (ImageView)findViewById(R.id.toy);
        pharmacy=(ImageView)findViewById(R.id.medical);
        Dairy=(ImageView)findViewById(R.id.dairy);
        Sweats=(ImageView)findViewById(R.id.Sweats);

        ToyShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("category","Toy");
                startActivity(intent);
            }
        });
        vegetables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("category","Vegetables");
                startActivity(intent);
            }
        });

        Grocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("category","Grocery");
                startActivity(intent);
            }
        });

        Sweats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("category","Sweats");
                startActivity(intent);
            }
        });

        pharmacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("category","Medicine");
                startActivity(intent);
            }
        });



        tech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("category","Tech shop");
                startActivity(intent);
            }
        });

        Meat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("category","Meat");
                startActivity(intent);
            }
        });
        Dairy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SellerProductCategoryActivity.this, SellerAddNewProductActivity.class);
                intent.putExtra("category","Dairy");
                startActivity(intent);
            }
        });


    }
}
