package com.alok.business.Sellers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alok.business.R;

public class all_orders extends AppCompatActivity {

    private Button accepted, pending, rejected, delivered;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_orders);

        accepted=(Button)findViewById(R.id.seller_accepted);
        rejected=(Button)findViewById(R.id.seller_rejected);
        pending=(Button)findViewById(R.id.seller_pending);
delivered=(Button)findViewById(R.id.seller_delivered);
        accepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(all_orders.this, accepted_orders.class);
                startActivity(intent);
            }
        });

        rejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(all_orders.this, rejected_orders.class);
                startActivity(intent);
            }
        });

        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(all_orders.this, pending_orders.class);
                startActivity(intent);
            }
        });

        delivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(all_orders.this, delivered_orders.class);
                startActivity(intent);
            }
        });


    }
}