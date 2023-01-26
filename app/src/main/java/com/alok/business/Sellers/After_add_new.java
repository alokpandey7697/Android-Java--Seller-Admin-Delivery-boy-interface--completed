package com.alok.business.Sellers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alok.business.R;
import com.alok.business.here.Main_v;

public class After_add_new extends AppCompatActivity {

    Button new_product, new_category, home_activity;
    String CategoryName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_add_new);

        new_product = findViewById(R.id.add_new_product);
        new_category = findViewById(R.id.select_new_category);
        home_activity = findViewById(R.id.homeActivity);
        CategoryName = getIntent().getExtras().get("category").toString();

        new_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(After_add_new.this, SellerAddNewProductActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                intent.putExtra("category", CategoryName);
                startActivity(intent);

            }
        });

        new_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(After_add_new.this, SellerProductCategoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        home_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(After_add_new.this, SellerHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}