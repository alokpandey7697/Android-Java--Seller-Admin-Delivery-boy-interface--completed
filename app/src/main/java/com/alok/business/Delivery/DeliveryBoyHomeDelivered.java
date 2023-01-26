package com.alok.business.Delivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alok.business.Admin.AdminUserProductsActivity;
import com.alok.business.R;
import com.alok.business.models.AdminOrders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeliveryBoyHomeDelivered extends AppCompatActivity {




    private RecyclerView orderList;
    private DatabaseReference ordersRef,adminviewref;
    private FirebaseAuth auth;
    public static int count_item= -1;
    private TextView txtmsg1;


    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_boy_home_delivered);

        txtmsg1=(TextView)findViewById(R.id.msg1);


        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        adminviewref = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin view");

        orderList=findViewById(R.id.orders_list);
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
        FirebaseRecyclerOptions<AdminOrders> options = new FirebaseRecyclerOptions.Builder<AdminOrders>().setQuery(ordersRef.orderByChild("deliveryBoy").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid() +" " + "N"),AdminOrders.class).build();

        FirebaseRecyclerAdapter<AdminOrders,AdminOrdersViewHolder> adapter=
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, int position, @NonNull AdminOrders model) {

                        count_item++;
                        Log.d("count", String.valueOf(count_item) + "   " + String.valueOf(position));

                        if(count_item == 0)
                            NoItemCart();

                        holder.userName.setText("Name:" + model.getName());
                        holder.UserPhoneNumber.setText("Phone:" + model.getPhone());
                        holder.userTotalPrice.setText("Total Amount:" + model.getTotalAmount());
                        holder.userDateTime.setText("Order at:" + model.getDate() + " "+model.getTime());
                        holder.userShippingAddress.setText("Shipping Address:" + model.getAddress()+ " "+model.getCity() );

                        holder.showOrderBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String uID=getRef(position).getKey();
                                Intent intent=new Intent(DeliveryBoyHomeDelivered.this, AdminUserProductsActivity.class);
                                intent.putExtra("deslat",model.getLattitude());
                                intent.putExtra("deslon",model.getLongitude());
                                //intent.putExtra("sid",model.getSid());
                                intent.putExtra("uid",uID);
                                startActivity(intent);
                            }
                        });

//                        holder.itemView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                CharSequence options[] = new CharSequence[]
//                                        {
//                                                "Yes",
//                                                "No"
//                                        };
//                                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryBoyHomeDelivered.this);
//                                builder.setTitle(("Have you delivered this product ? "));
//
//                                builder.setItems(options, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        if(which==0)
//                                        {
//                                            auth = FirebaseAuth.getInstance();
//                                            HashMap<String,Object> productMap=new HashMap<>();
//                                            productMap.put("deliBoy",auth.getCurrentUser().getUid() +" "+ "delivered");
//                                            productMap.put("deliveryBoy", auth.getCurrentUser().getUid()+" "+ "N");
//
//
//                                            ordersRef.child(getRef(position).getKey()).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if(task.isSuccessful())
//                                                    {
//                                                        Toast.makeText(DeliveryBoyHomeDelivered.this, "Changes applied successfully", Toast.LENGTH_SHORT).show();
//                                                        finish();
//                                                    }
//                                                }
//                                            });
//                                        }
//                                        else
//                                            finish();
//                                    }
//
//
//                                });
//                                builder.show();
//                            }
//                        });
                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout_deli,parent,false);
                        return  new AdminOrdersViewHolder(view);
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
    private void RemoveOrder(String uID) {
        ordersRef.child(uID).removeValue();
        adminviewref.child(uID).removeValue();
    }
}
