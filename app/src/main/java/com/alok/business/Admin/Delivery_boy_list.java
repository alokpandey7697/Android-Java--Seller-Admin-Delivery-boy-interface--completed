package com.alok.business.Admin;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alok.business.Notification.SendNotif;
import com.alok.business.R;
import com.alok.business.models.AdminOrders;
import com.alok.business.models.DeliveryBoyModel;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class Delivery_boy_list extends AppCompatActivity {

    private RecyclerView orderList;
    private DatabaseReference delBoyRef,ordersRef;
    public static int count_item= -1;
    private TextView txtmsg1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_boy_list);
        getSupportActionBar().setTitle("Delivery boy list");
        delBoyRef = FirebaseDatabase.getInstance().getReference().child("DeliveryBoy");
       // adminviewref = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin view");
        txtmsg1=(TextView)findViewById(R.id.msg1);

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
        FirebaseRecyclerOptions<DeliveryBoyModel> options = new FirebaseRecyclerOptions.Builder<DeliveryBoyModel>().setQuery(delBoyRef,DeliveryBoyModel.class).build();
        FirebaseRecyclerAdapter<DeliveryBoyModel, Delivery_boy_list.AdminOrdersViewHolder> adapter=
                new FirebaseRecyclerAdapter<DeliveryBoyModel, Delivery_boy_list.AdminOrdersViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, int position, @NonNull DeliveryBoyModel model) {

                        count_item++;
                        Log.d("count", String.valueOf(count_item) + "   " + String.valueOf(position));

                        if(count_item == 0)
                            NoItemCart();

                        holder.name.setText(model.getName());
                        holder.email.setText(model.getEmail());
                        holder.phone.setText( model.getPhone());
                        holder.address.setText(model.getAddress());
                        Glide.with(holder.profileImage).load(model.getImage()).into(holder.profileImage);

//                        holder.showOrderBtn.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                String uID=getRef(position).getKey();
//                                Intent intent=new Intent(Delivery_boy_list.this, AdminUserProductActivity.class);
//                                intent.putExtra("deslat",model.getLattitude());
//                                intent.putExtra("deslon",model.getLongitude());
//                                //intent.putExtra("sid",model.getSid());
//                                intent.putExtra("uid",uID);
//                                startActivity(intent);
//                            }
//                        });
//
//                        holder.assignDeliveryBoy.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Intent intent = new Intent(Delivery_boy_list.this, Delivery_boy_list.class);
//                                startActivity(intent);
//                            }
//                        });
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Yes",
                                                "No"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(Delivery_boy_list.this);
                                builder.setTitle(("Assign for Delivery? "));

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which==0)
                                        {
                                            HashMap<String,Object> productMap=new HashMap<>();
                                            productMap.put("state","in progress");


                                            productMap.put("deliveryBoy",getRef(position).getKey() +" "+ "Y");

                                            ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");

                                            ordersRef.child(getIntent().getExtras().getString("keyRef")).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                                                    SendNotif.sendNotification("AdminTokens", AdminUid[0], "ForAdmin", "Order", "assigned", getApplicationContext());
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                // Toast.makeText(storeList.this, "Opsss.... Something is wrong", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                        SendNotif.sendNotification("DeliveryTokens",model.getSid(),"ForDelivery","Order","assigned", getApplicationContext());


                                                        //Toast.makeText(Delivery_boy_list.this, "Changes applied successfully", Toast.LENGTH_SHORT).show();
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
                    public Delivery_boy_list.AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_boy_model,parent,false);
                        return  new Delivery_boy_list.AdminOrdersViewHolder(view);
                    };
                };
        orderList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder

    {
        public ImageView profileImage;
        public TextView name,email,phone,address;
        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);

           profileImage = itemView.findViewById(R.id.profile_image);
           name = itemView.findViewById(R.id.name);
           email = itemView.findViewById(R.id.email);
           phone = itemView.findViewById(R.id.phone);
           address = itemView.findViewById(R.id.address);

        }
    }
    private void RemoveOrder(String uID) {
        ordersRef.child(uID).removeValue();
        //adminviewref.child(uID).removeValue();
    }
}
