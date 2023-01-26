package com.alok.business.here;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.alok.business.Buyer.MainActivity;
import com.alok.business.R;
import com.alok.business.Sellers.After_add_new;
import com.alok.business.Sellers.SellerAddNewProductActivity;
import com.alok.business.Sellers.SellerHomeActivity;
import com.alok.business.Sellers.SellerProductCategoryActivity;
import com.alok.business.Sellers.SellersRegistrationActivity;
import com.alok.business.Sellers.all_orders;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Main_v extends AppCompatActivity {

    Button o_c, n_o, a_p;
    String CategoryName;




    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intentHome = new Intent(Main_v.this, SellerHomeActivity.class);
                    startActivity(intentHome);
                    return true;


                case R.id.navigation_add:
                    Intent intentcat = new Intent(Main_v.this, SellerProductCategoryActivity.class);
                    startActivity(intentcat);

                    return true;


                case R.id.navigation_logout:
                    final FirebaseAuth auth;
                    auth = FirebaseAuth.getInstance();
                    auth.signOut();

                    Intent intentmain = new Intent(Main_v.this, MainActivity.class);
                    intentmain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentmain);
                    finish();
                    return true;

                case R.id.navigation_order:
                    Intent intenta = new Intent(Main_v.this, all_orders.class);
                    startActivity(intenta);
                    return true;


            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_v);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        o_c = findViewById(R.id.o_c);
        n_o = findViewById(R.id.n_o);
        a_p = findViewById(R.id.a_p);

        o_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        n_o.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
Intent intent = new Intent(Main_v.this, all_orders.class);
startActivity(intent);
            }
        });

        a_p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main_v.this, SellerHomeActivity.class);
               // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });
    }
}