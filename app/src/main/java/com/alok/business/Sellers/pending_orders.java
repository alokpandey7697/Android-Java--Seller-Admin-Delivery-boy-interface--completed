package com.alok.business.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alok.business.Admin.AdminUserProductsActivity;
import com.alok.business.Notification.SendNotif;
import com.alok.business.R;
import com.alok.business.models.AdminOrders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class pending_orders extends AppCompatActivity {

    private RecyclerView orderList;
    private DatabaseReference ordersRef,adminviewref;
    public static int count_item= -1;
    private TextView txtmsg1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_orders);
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        adminviewref = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin view");
        txtmsg1=(TextView)findViewById(R.id.msg1);

        orderList=findViewById(R.id.seller_pending_orders_list);
        orderList.setLayoutManager(new LinearLayoutManager(this));
    }
    public void NoItemCart() {

        if(count_item == -1) {
            txtmsg1.setVisibility(View.VISIBLE);
            txtmsg1.setText("Empty !");
        }
        else {

            txtmsg1.setVisibility(View.GONE);

        }
    }  @Override
    protected void onResume() {
        super.onResume();
        count_item = -1;
        NoItemCart();
    }
    protected  void onStart()
    {
        super.onStart();
        FirebaseRecyclerOptions<AdminOrders> options = new FirebaseRecyclerOptions.Builder<AdminOrders>().setQuery(ordersRef.orderByChild("pending").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid() +" "+"Y"),AdminOrders.class).build();
        final FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>[] adapter = new FirebaseRecyclerAdapter[]{new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, int position, @NonNull AdminOrders model) {
                count_item++;
                Log.d("count", String.valueOf(count_item) + "   " + String.valueOf(position));

                if(count_item == 0)
                    NoItemCart();

                Log.d("holder", getRef(position).getKey());
                holder.userName.setText("Name:" + model.getName());
                holder.UserPhoneNumber.setText("Phone:" + model.getPhone());
                holder.userTotalPrice.setText("Total Amount:" + model.getTotalAmount());
                holder.userDateTime.setText("Order at:" + model.getDate() + " " + model.getTime());
                holder.userShippingAddress.setText("Shipping Address:" + model.getAddress() + " " + model.getCity());

                holder.showOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String orderID = getRef(position).getKey();
                        Intent intent = new Intent(pending_orders.this, AdminUserProductsActivity.class);
                        intent.putExtra("deslat", model.getLattitude());
                        intent.putExtra("deslon", model.getLongitude());
                        //intent.putExtra("sid",model.getSid());
                        intent.putExtra("uid", orderID);
                        startActivity(intent);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Accept",
                                        "reject"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(pending_orders.this);
                        builder.setTitle(("New Order ? "));

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {

                                    HashMap<String, Object> productMap = new HashMap<>();
                                    productMap.put("pending", model.getSid() + " " + "N");
                                    productMap.put("accepted", model.getSid() + " " + "Y");
                                    productMap.put("state", "IN PROGRESS");

                                    productMap.put("pend", "N");

                                    productMap.put("acce", "Y");


                                    ordersRef.child(getRef(position).getKey()).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                final String[] AdminUid = new String[1];
                                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("AdminTokens");
                                                reference.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                            AdminUid[0] = dataSnapshot1.getKey();
                                                            SendNotif.sendNotification("AdminTokens", AdminUid[0], "VireStore", "Order Accepted", "accepted", getApplicationContext());
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        // Toast.makeText(storeList.this, "Opsss.... Something is wrong", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                SendNotif.sendNotification("BuyersTokens", model.getUid(), "VireStore", "Order Accepted", "accepted", getApplicationContext());


                                             //   Toast.makeText(pending_orders.this, "Changes applied successfully", Toast.LENGTH_SHORT).show();
                                                finish();

                                            }
                                        }
                                    });
                                }
                                if (which == 1) {
                                    HashMap<String, Object> productMap = new HashMap<>();
                                    productMap.put("rejected", model.getSid() + " " + "Y");
                                    productMap.put("accepted", model.getSid() + " " + "N");
                                    productMap.put("pending", model.getSid() + " " + "N");
                                    productMap.put("state", "Rejected");

                                    productMap.put("pend", "N");

                                    productMap.put("acce", "N");

                                    productMap.put("reje", "Y");


                                    ordersRef.child(getRef(position).getKey()).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                final String[] AdminUid = new String[1];
                                                FirebaseDatabase.getInstance().getReference().child("AdminTokens").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override

                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        AdminUid[0] = Objects.requireNonNull(dataSnapshot).getKey();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                                SendNotif.sendNotification("BuyersTokens", model.getUid(), "ForBuyer", "Order", "rejected", getApplicationContext());
                                                SendNotif.sendNotification("AdminTokens", AdminUid[0], "ForAdmin", "Order", "rejected", getApplicationContext());
                                                Log.d("Adminnm", model.getUid());

                                                Log.d("Adminnnn", AdminUid[0]);

                                            //    Toast.makeText(pending_orders.this, "Changes applied successfully", Toast.LENGTH_SHORT).show();
                                                // finish();
                                            }
                                        }
                                    });
                                }


                            }


                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_pending_orders_layout, parent, false);
                return new AdminOrdersViewHolder(view);
            }

            ;
        }};
        orderList.setAdapter(adapter[0]);
        adapter[0].startListening();
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
    private void RemoveOrder(String uID) {
        ordersRef.child(uID).removeValue();
        adminviewref.child(uID).removeValue();
    }
}
