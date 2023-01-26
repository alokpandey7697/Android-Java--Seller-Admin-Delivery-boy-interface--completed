package com.alok.business.Admin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alok.business.Notification.SendNotif;
import com.alok.business.R;
import com.alok.business.models.AdminOrders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class accepted_orders extends AppCompatActivity {

    private RecyclerView orderList;
    private DatabaseReference ordersRef, adminviewref;
    public static int count_item = -1;
    private TextView txtmsg1;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_orders);
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        adminviewref = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin view");
        txtmsg1 = (TextView) findViewById(R.id.msg1);

        orderList = findViewById(R.id.seller_pending_orders_list);
        orderList.setLayoutManager(new LinearLayoutManager(this));
    }

    public void NoItemCart() {

        if (count_item == -1) {
            txtmsg1.setVisibility(View.VISIBLE);
            txtmsg1.setText("Empty !");
            txtmsg1.setText("Empty !");
        } else {

            txtmsg1.setVisibility(View.GONE);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        count_item = -1;
        NoItemCart();
    }

    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<AdminOrders> options = new FirebaseRecyclerOptions.Builder<AdminOrders>().setQuery(ordersRef.orderByChild("acce").equalTo("Y"), AdminOrders.class).build();
        FirebaseRecyclerAdapter<AdminOrders, accepted_orders.AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, accepted_orders.AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull accepted_orders.AdminOrdersViewHolder holder, int position, @NonNull AdminOrders model) {
                        count_item++;
                        Log.d("count", String.valueOf(count_item) + "   " + String.valueOf(position));

                        if (count_item == 0)
                            NoItemCart();
                        if (model.getDeliCharge() == null) {
                            holder.deli_charge.setText("Delivery Charge :" + " Not Added");
                        } else if(model.getDeliCharge().equals("Not added")) {
                            holder.deli_charge.setText("Delivery Charge :" + model.getDeliCharge());
                        }
                            else if(isValidNumeric(model.getDeliCharge())){
                            holder.deli_charge.setText("Delivery Charge :" + "  ₹ " + model.getDeliCharge());

                        }

                        holder.userName.setText("Name:" + model.getName());
                        holder.UserPhoneNumber.setText("Phone:" + model.getPhone());
                        holder.userTotalPrice.setText("Total Amount:" + model.getTotalAmount());
                        holder.userDateTime.setText("Order at:" + model.getDate() + " " + model.getTime());
                        holder.userShippingAddress.setText("Shipping Address:" + model.getAddress() + " " + model.getCity());
                        if (model.getDeliBoy().equals("N")) {
                            holder.deli_boy.setText("Not delivered");
                        } else {
                            holder.deli_boy.setText("delivered. please confirm");

                        }

                        if (model.getDeliveryBoy().equals("N")) {
                            holder.deli_status.setText("Deli boy not assigned");

                        } else {
                            holder.deli_status.setText("Deli boy assigned");
                        }
                        holder.showOrderBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String uID = getRef(position).getKey();
                                Intent intent = new Intent(accepted_orders.this, AdminUserProductsActivity.class);
                                intent.putExtra("deslat", model.getLattitude());
                                intent.putExtra("deslon", model.getLongitude());
                                //intent.putExtra("sid",model.getSid());
                                intent.putExtra("uid", uID);
                                startActivity(intent);
                            }
                        });

                        holder.assignDeliveryBoy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (model.getDeliveryBoy().equals("N")) {

                                    CharSequence[] options = new CharSequence[]
                                            {
                                                    "Add", "Cancel"
                                            };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(accepted_orders.this);


                                    final EditText edittext = new EditText(accepted_orders.this);
                                    edittext.setRawInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                    builder.setView(edittext);
                                    builder.setTitle("Enter Delivery Charge");


                                    builder.setCancelable(false);
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(which==0)
                                            {
                                                String Deli_Charge = edittext.getText().toString();
                                                if (isValidNumeric(Deli_Charge)) {
                                                    loadingBar=new ProgressDialog(accepted_orders.this);
                                                    loadingBar.setTitle("creating seller account");
                                                    loadingBar.setMessage("please wait while we are checking credentials.");
                                                    loadingBar.setCanceledOnTouchOutside(false);
                                                    loadingBar.show();
                                                    HashMap<String, Object> productMap = new HashMap<>();
                                                    productMap.put("deliCharge", Deli_Charge);


                                                    ordersRef.child(getRef(position).getKey()).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                loadingBar.dismiss();
                                                                Intent intent = new Intent(accepted_orders.this, Delivery_boy_list.class);
                                                                intent.putExtra("keyRef", getRef(position).getKey());
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            loadingBar.dismiss();
                                                            Toast.makeText(getApplicationContext(), "Retry !",Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }

                                                else {
                                                    Toast.makeText(accepted_orders.this, " Enter valid amount !", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            if(which == 1)
                                            {
                                                dialog.cancel();
                                            }
                                        }
                                    });
                                    builder.show();

                                    }
                                 else {
                                    Toast.makeText(accepted_orders.this, "Already assigned Delivery boy.", Toast.LENGTH_SHORT).show();
                                }
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(accepted_orders.this);
                                builder.setTitle(("Have you shipped this orders products ? "));

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            HashMap<String, Object> productMap = new HashMap<>();
                                            productMap.put("accepted", model.getSid() + " " + "N");
                                            productMap.put("delivered", model.getSid() + " " + "Y");
                                            productMap.put("state", "Delivered");

                                            productMap.put("pend", "N");

                                            productMap.put("acce", "N");

                                            productMap.put("deli", "Y");

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
                                                                    SendNotif.sendNotification("AdminTokens", AdminUid[0], "ForAdmin", "Order", "delivered", getApplicationContext());
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                // Toast.makeText(storeList.this, "Opsss.... Something is wrong", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                        SendNotif.sendNotification("SellerTokens", model.getSid(), "VireStore", "Order delivered", "delivered", getApplicationContext());
                                                        SendNotif.sendNotification("BuyersTokens", model.getUid(), "VireStore", "Order delivered", "delivered", getApplicationContext());

                                                        finish();

                                                    }
                                                }
                                            });
                                        } else
                                            finish();
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public accepted_orders.AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                        return new accepted_orders.AdminOrdersViewHolder(view);
                    }

                    ;
                };
        orderList.setAdapter(adapter);
        adapter.startListening();
    }


    public boolean isValidNumeric(String str)
    {
        str = str.trim(); // trims the white spaces.

        if (str.length() == 0)
            return false;

        // if string is of length 1 and the only
        // character is not a digit
        if (str.length() == 1 && !Character.isDigit(str.charAt(0)))
            return false;

        // If the 1st char is not '+', '-', '.' or digit
        if (str.charAt(0) != '+' && str.charAt(0) != '-'
                && !Character.isDigit(str.charAt(0))
                && str.charAt(0) != '.')
            return false;

        // To check if a '.' or 'e' is found in given
        // string. We use this flag to make sure that
        // either of them appear only once.
        boolean flagDotOrE = false;

        for (int i = 1; i < str.length(); i++) {
            // If any of the char does not belong to
            // {digit, +, -, ., e}
            if (!Character.isDigit(str.charAt(i))
                    && str.charAt(i) != 'e' && str.charAt(i) != '.'
                    && str.charAt(i) != '+' && str.charAt(i) != '-')
                return false;

            if (str.charAt(i) == '.') {
                // checks if the char 'e' has already
                // occurred before '.' If yes, return 0.
                if (flagDotOrE == true)
                    return false;

                // If '.' is the last character.
                if (i + 1 >= str.length())
                    return false;

                // if '.' is not followed by a digit.
                if (!Character.isDigit(str.charAt(i + 1)))
                    return false;
            }

            else if (str.charAt(i) == 'e') {
                // set flagDotOrE = 1 when e is encountered.
                flagDotOrE = true;

                // if there is no digit before 'e'.
                if (!Character.isDigit(str.charAt(i - 1)))
                    return false;

                // If 'e' is the last Character
                if (i + 1 >= str.length())
                    return false;

                // if e is not followed either by
                // '+', '-' or a digit
                if (!Character.isDigit(str.charAt(i + 1))
                        && str.charAt(i + 1) != '+'
                        && str.charAt(i + 1) != '-')
                    return false;
            }
        }

        /* If the string skips all above cases, then
           it is numeric*/
        return true;
    }

    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder
    {
        public Button showOrderBtn;
        public TextView userName,UserPhoneNumber, userTotalPrice, userDateTime, userShippingAddress,assignDeliveryBoy,deli_boy,deli_status,deli_charge;
        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            deli_charge = itemView.findViewById(R.id.Deli_charge);
            userName=itemView.findViewById(R.id.user_name);
            UserPhoneNumber=itemView.findViewById(R.id.order_phone_number);
            userTotalPrice=itemView.findViewById(R.id.order_total_price);
            userDateTime=itemView.findViewById(R.id.order_date_time);
            userShippingAddress=itemView.findViewById(R.id.order_address_city);
            showOrderBtn=itemView.findViewById(R.id.show_all_products_btn);
            assignDeliveryBoy=itemView.findViewById(R.id.button);
            deli_boy= itemView.findViewById(R.id.Deli_boy);
            deli_status= itemView.findViewById(R.id.Deli_status);

        }
    }
 /*   private void RemoveOrder(String uID) {
        ordersRef.child(uID).removeValue();
        adminviewref.child(uID).removeValue();
    }*/
}
