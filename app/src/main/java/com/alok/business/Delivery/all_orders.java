package com.alok.business.Delivery;

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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alok.business.Admin.AdminUserProductsActivity;
import com.alok.business.Buyer.MainActivity;
import com.alok.business.Notification.SendNotif;
import com.alok.business.R;
import com.alok.business.models.AdminOrders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class all_orders extends AppCompatActivity {



    private RecyclerView orderList;
    private DatabaseReference ordersRef,adminviewref;
    private FirebaseAuth auth;
    public static int count_item= -1;
    private TextView txtmsg1;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_inprogress:
                    Intent intent = new Intent(all_orders.this, all_orders.class);
                    intent.addFlags( Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);

                    return true;


                case R.id.navigation_delivered:
                    Intent intenta = new Intent(all_orders.this, DeliveryBoyHomeDelivered.class);
                    intenta.addFlags( Intent.FLAG_ACTIVITY_NO_ANIMATION);

                    startActivity(intenta);
                    return true;


                case R.id.navigation_logout:
                    final FirebaseAuth auth;
                    auth = FirebaseAuth.getInstance();
                    auth.signOut();
                    Intent intentmain = new Intent(all_orders.this, MainActivity.class);
                    intentmain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intentmain.addFlags( Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intentmain);
                    finish();

                    return true;

            }
            return false;
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_orders2);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        txtmsg1=(TextView)findViewById(R.id.msg1);
        NoItemCart();
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        adminviewref = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin view");

        orderList=findViewById(R.id.orders_list);
        orderList.setLayoutManager(new LinearLayoutManager(this));

    }
    @Override
    protected void onResume() {
        super.onResume();
        count_item = -1;
        NoItemCart();
    }
    public void NoItemCart() {

        if(count_item == -1) {
            txtmsg1.setVisibility(View.VISIBLE);
            txtmsg1.setText("Empty !");
            Log.d("txtmsg1","textno");
        }
        else {

            txtmsg1.setVisibility(View.GONE);

        }
    }
    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<AdminOrders> options = new FirebaseRecyclerOptions.Builder<AdminOrders>().setQuery(ordersRef.orderByChild("deliveryBoy").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid() +" " + "Y"),AdminOrders.class).build();

        FirebaseRecyclerAdapter<AdminOrders, DeliveryBoyHome.AdminOrdersViewHolder> adapter=
                new FirebaseRecyclerAdapter<AdminOrders, DeliveryBoyHome.AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull DeliveryBoyHome.AdminOrdersViewHolder holder, int position, @NonNull AdminOrders model) {

                        holder.userName.setText("Name:" + model.getName());
                        holder.UserPhoneNumber.setText("Phone:" + model.getPhone());
                        holder.userTotalPrice.setText("Total Amount:" + model.getTotalAmount());
                        holder.userDateTime.setText("Order at:" + model.getDate() + " "+model.getTime());
                        holder.userShippingAddress.setText("Shipping Address:" + model.getAddress()+ " "+model.getCity() );

                        holder.showOrderBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String uID=getRef(position).getKey();
                                Intent intent=new Intent(all_orders.this, AdminUserProductsActivity.class);
                                intent.putExtra("deslat",model.getLattitude());
                                intent.putExtra("deslon",model.getLongitude());
                                //intent.putExtra("sid",model.getSid());
                                intent.putExtra("uid",uID);
                                startActivity(intent);
                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Yes",
                                                "No"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(all_orders.this);
                                builder.setTitle(("Have you delivered this product ? "));

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which==0)
                                        {
                                            auth = FirebaseAuth.getInstance();
                                            HashMap<String,Object> productMap=new HashMap<>();
                                            productMap.put("deliBoy",auth.getCurrentUser().getUid() +" "+ "delivered");
                                            productMap.put("deliveryBoy", auth.getCurrentUser().getUid()+" "+ "N");


                                            ordersRef.child(getRef(position).getKey()).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        final String[] AdminUid = new String[1];
                                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("AdminTokens");
                                                        reference.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                                    AdminUid[0] = dataSnapshot1.getKey();
                                                                    SendNotif.sendNotification("AdminTokens", AdminUid[0], "ForAdmin", "Order", "accepted", getApplicationContext());
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                // Toast.makeText(storeList.this, "Opsss.... Something is wrong", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                        //Toast.makeText(all_orders.this, "Changes applied successfully", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                }
                                            });
                                        }
                                        else
                                            finish();
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public DeliveryBoyHome.AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout_deli,parent,false);
                        return  new DeliveryBoyHome.AdminOrdersViewHolder(view);
                    };
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