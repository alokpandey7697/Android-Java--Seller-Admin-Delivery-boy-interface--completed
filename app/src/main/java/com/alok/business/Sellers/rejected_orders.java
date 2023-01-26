package com.alok.business.Sellers;

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

public class rejected_orders extends AppCompatActivity {

    private RecyclerView orderList;
    private DatabaseReference ordersRef,adminviewref;
    public static int count_item= -1;
    private TextView txtmsg1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejected_orders);
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
        FirebaseRecyclerOptions<AdminOrders> options = new FirebaseRecyclerOptions.Builder<AdminOrders>().setQuery(ordersRef.orderByChild("rejected").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid() + " " + "Y"),AdminOrders.class).build();
        FirebaseRecyclerAdapter<AdminOrders, pending_orders.AdminOrdersViewHolder> adapter=
                new FirebaseRecyclerAdapter<AdminOrders, pending_orders.AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull pending_orders.AdminOrdersViewHolder holder, int position, @NonNull AdminOrders model) {
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
                                Intent intent=new Intent(rejected_orders.this, AdminUserProductsActivity.class);
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
//                                AlertDialog.Builder builder = new AlertDialog.Builder(rejected_orders.this);
//                                builder.setTitle(("Have you shipped this orders products ? "));
//
//                                builder.setItems(options, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        if(which==0)
//                                        {
//                                            String uID = getRef(position).getKey();
//                                            RemoveOrder(uID);
//
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
                    public pending_orders.AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_rejected_orders_layout,parent,false);
                        return  new pending_orders.AdminOrdersViewHolder(view);
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
