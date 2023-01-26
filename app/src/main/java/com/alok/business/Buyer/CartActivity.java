package com.alok.business.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alok.business.Map.MapActivity;
import com.alok.business.Prevalent.Prevalent;
import com.alok.business.R;
import com.alok.business.ViewHolder.CartViewHolder;
import com.alok.business.models.Cart;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerview;
    private RecyclerView.LayoutManager layoutManager;
    private Button Processbtn;
    private TextView txttotal,txtmsg1;
    private int overallAmount=0;
    private String add;
    public static int count_item= -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerview = findViewById(R.id.cart_list);
        recyclerview.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerview.setLayoutManager(layoutManager);
        Processbtn=(Button)findViewById(R.id.process_btn);
        txttotal=(TextView)findViewById(R.id.total_price);
        txtmsg1=(TextView)findViewById(R.id.msg1);
        if(count_item == -1) {
            Processbtn.setVisibility(View.GONE);
            txtmsg1.setVisibility(View.VISIBLE);
            txtmsg1.setText("No item in cart");
        }
        Processbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txttotal.setText("Total Price= $" + String.valueOf(overallAmount));
//                Intent intent =new Intent(CartActivity.this, MapActivity.class);
//                intent.putExtra("Total price",String.valueOf(overallAmount));
//                startActivity(intent);
//                finish();
                Intent i = new Intent(CartActivity.this, MapActivity.class);
                startActivityForResult(i, 1);
            }
        });
    }
    protected void onStart() {


       // CheckOrderState();
        super.onStart();
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart>options=
                new FirebaseRecyclerOptions.Builder<Cart>().setQuery(cartListRef.child("User view").
                        child(Prevalent.currentOnlineUsers.getPhone()).child("Products"),Cart.class).build();


        FirebaseRecyclerAdapter<Cart, CartViewHolder>adapter=new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {

                count_item++;

                holder.TxtProductQuantity.setText("Quantity = "+ model.getQuantity());
                holder.txtProductPrice.setText("Price "+model.getPrice()+"$");
                holder.txtProductName.setText(model.getPname());

                int oneTypeProductPrice=((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                overallAmount = overallAmount+oneTypeProductPrice;


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]
                                {
                                       "Edit",
                                       "Remove"
                                }  ;
                        AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart OPtions:");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which==0)
                                {
                                    Intent intent = new  Intent(CartActivity.this, ProductsDetailActivity.class);
                                    intent.putExtra("pid",model.getPid());
                                    startActivity(intent);
                                }
                                if(which==1)
                                {
                                    cartListRef.child("User view")
                                            .child(Prevalent.currentOnlineUsers.getPhone())
                                            .child("Products")
                                            .child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        count_item--;
                                                        Toast.makeText(CartActivity.this, "Item removed Successfully", Toast.LENGTH_SHORT).show();

                                                        Intent intent = new  Intent(CartActivity.this, HomeActivity.class);

                                                        startActivity(intent);
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
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerview.setAdapter(adapter);
        adapter.startListening();
    }

    private void CheckOrderState()
    {
        DatabaseReference orderRef;
        orderRef=FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUsers.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    String  shippingState = dataSnapshot.child("state").getValue().toString();
                    String  username = dataSnapshot.child("name").getValue().toString();

                    if(shippingState.equals("shipped"))
                    {
                        txttotal.setText("Dear"+username+"\n order is shipped successfully");
                        recyclerview.setVisibility(View.GONE);

                        txtmsg1.setVisibility(View.VISIBLE);
                        txtmsg1.setText("Congractulation ,your final order is placed and will reach at your home.");
                        Processbtn.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "you can purchase more products, once you received your first order", Toast.LENGTH_SHORT).show();
                    }
                    else if(shippingState.equals("not shipped"))
                    {
                        txttotal.setText("Shipping status = Not shipped");
                        recyclerview.setVisibility(View.GONE);

                        txtmsg1.setVisibility(View.VISIBLE);
                        Processbtn.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this, "you can purchase more products, once you received your first order", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                add = data.getStringExtra("result");
                Intent intent =new Intent(CartActivity.this, ConferFinalOrderActivity.class);
              // Log.d("jdj",data.getStringExtra("lattitude"));
               // Log.d("jdj",data.getStringExtra("longitude"));
               // intent.putExtra("sid",sid);
                intent.putExtra("deslat",data.getStringExtra("lattitude"));
                intent.putExtra("deslon",data.getStringExtra("longitude"));
                intent.putExtra("Total price",overallAmount);
                intent.putExtra("Address",add );
                startActivity(intent);
                finish();
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(count_item == -1) {
            Processbtn.setVisibility(View.GONE);
            txtmsg1.setVisibility(View.VISIBLE);
            txtmsg1.setText("No item in cart");
        }
        else {
                Processbtn.setVisibility(View.VISIBLE);
                txtmsg1.setVisibility(View.GONE);
        }

    }
}
