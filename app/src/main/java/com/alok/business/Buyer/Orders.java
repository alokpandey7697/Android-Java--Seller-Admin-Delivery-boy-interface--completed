package com.alok.business.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alok.business.Admin.AdminUserProductsActivity;
import com.alok.business.Prevalent.Prevalent;
import com.alok.business.R;
import com.alok.business.models.AdminOrders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Orders extends AppCompatActivity {


    private RecyclerView orderList;
    private DatabaseReference ordersRef,adminviewref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);


        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        //adminviewref = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin view");

        orderList=findViewById(R.id.orders_list);
        orderList.setLayoutManager(new LinearLayoutManager(this));
    }

    protected  void onStart()
    {
        super.onStart();
        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                        .setQuery(ordersRef.orderByChild("phone").equalTo(Prevalent.currentOnlineUsers.getPhone()),AdminOrders.class)
                        .build();
        FirebaseRecyclerAdapter<AdminOrders, Orders.AdminOrdersViewHolder> adapter= new FirebaseRecyclerAdapter<AdminOrders, Orders.AdminOrdersViewHolder>(options) {


            @NonNull
            @Override
            public Orders.AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                return new Orders.AdminOrdersViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(@NonNull Orders.AdminOrdersViewHolder holder, int position, @NonNull AdminOrders model) {
                holder.userName.setText("Name:" + model.getName());
                holder.UserPhoneNumber.setText("Phone:" + model.getPhone());
                holder.userTotalPrice.setText("Total Amount:" + model.getTotalAmount());
                holder.userDateTime.setText("Order at:" + model.getDate() + " " + model.getTime());
                holder.userShippingAddress.setText("Shipping Address:" + model.getAddress() + " " + model.getCity());

                holder.showOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uID = getRef(position).getKey();
                        Intent intent = new Intent(Orders.this, AdminUserProductsActivity.class);
                        intent.putExtra("deslat", model.getLattitude());
                        intent.putExtra("deslon", model.getLongitude());
                        intent.putExtra("uid", uID);
                        startActivity(intent);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }


                });
            }
        };
                orderList.setAdapter(adapter);
                adapter.startListening();
            }



public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder

{
    public Button showOrderBtn;
    public TextView userName,UserPhoneNumber, userTotalPrice, userDateTime, userShippingAddress;
    public AdminOrdersViewHolder(@NonNull View itemView) {
        super(itemView);

        userName=itemView.findViewById(R.id.user_name);
        UserPhoneNumber=itemView.findViewById(R.id.order_phone_number);
        userTotalPrice=itemView.findViewById(R.id.order_total_price);
        userDateTime=itemView.findViewById(R.id.order_date_time);
        userShippingAddress=itemView.findViewById(R.id.order_address_city);
        showOrderBtn=itemView.findViewById(R.id.show_all_products_btn);
    }
}

}