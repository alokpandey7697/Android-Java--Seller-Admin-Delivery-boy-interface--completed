package com.alok.business.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alok.business.Prevalent.Prevalent;
import com.alok.business.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConferFinalOrderActivity extends AppCompatActivity {

    private EditText nameEdittext,phoneEdittxt,addressEdittxt,cityedittxt;
    private Button confirmOrder;
    private String totalAmount="";
    final String savecurrentDate,saveCurrentTime;
    public ConferFinalOrderActivity(){
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        savecurrentDate=currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calForDate.getTime());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confer_final_order);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        totalAmount=getIntent().getStringExtra("Total price");

        Toast.makeText(this, "Total Price= $"+totalAmount, Toast.LENGTH_SHORT).show();

        confirmOrder  =(Button)findViewById(R.id.Confirm_order_btn);
        nameEdittext=(EditText)findViewById(R.id.shipment_name);
        phoneEdittxt=(EditText)findViewById(R.id.shipment_phone_number);
        addressEdittxt=(EditText)findViewById(R.id.shipment_address);
        cityedittxt=(EditText)findViewById(R.id.shipment_city);

        addressEdittxt.setText(getIntent().getStringExtra("Address"));

        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Check();
            }
        });
    }

    private void Check() {
        if(TextUtils.isEmpty(nameEdittext.getText().toString()))
        {
            Toast.makeText(this, "Please provide your full name", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(phoneEdittxt.getText().toString()))
        {
            Toast.makeText(this, "Please provide your phone number", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(addressEdittxt.getText().toString()))
        {
            Toast.makeText(this, "Please provide your full address", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(cityedittxt.getText().toString()))
        {
            Toast.makeText(this, "Please enter your city name", Toast.LENGTH_SHORT).show();
        }
        else
        {
            ConfirmOrder();
        }
    }

    private void ConfirmOrder() {



        final DatabaseReference orderRef= FirebaseDatabase.getInstance().getReference()
                .child("Orders").child(Prevalent.currentOnlineUsers.getPhone()+savecurrentDate+saveCurrentTime);
        HashMap<String,Object>orderMap=new HashMap<>();

        orderMap.put("totalAmount",totalAmount);
        orderMap.put("name",nameEdittext.getText().toString());
        orderMap.put("phone",phoneEdittxt.getText().toString());
        orderMap.put("address",addressEdittxt.getText().toString());
        orderMap.put("city",cityedittxt.getText().toString());
        orderMap.put("date",savecurrentDate);
        orderMap.put("time",saveCurrentTime);
        orderMap.put("lattitude",getIntent().getStringExtra("deslat"));
        orderMap.put("longitude",getIntent().getStringExtra("deslon"));
        orderMap.put("state","not shipped");


        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {   copyFirebaseData();
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User view")
                            .child(Prevalent.currentOnlineUsers.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {




                                        Toast.makeText(ConferFinalOrderActivity.this, " order placed successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(ConferFinalOrderActivity.this, HomeActivity.class);
                                       //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    finish();
                                    }
                                }
                            });
                }
            }
        });

    }


    public void copyFirebaseData() {

        DatabaseReference questionNodes = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User view").child(Prevalent.currentOnlineUsers.getPhone()).child("Products");
        final DatabaseReference toUsersQuestions = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin view").child(Prevalent.currentOnlineUsers.getPhone()+savecurrentDate+saveCurrentTime).child("Products");

        questionNodes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot questionCode : dataSnapshot.getChildren()) {
                    String pid = questionCode.getKey();
                    String imageUrlString = questionCode.child("date").getValue(String.class);
                    assert pid != null;
                    toUsersQuestions.child(pid).child("date").setValue(imageUrlString);
                    imageUrlString = questionCode.child("discount").getValue(String.class);
                    toUsersQuestions.child(pid).child("discount").setValue(imageUrlString);
                    imageUrlString = questionCode.child("image").getValue(String.class);
                    toUsersQuestions.child(pid).child("image").setValue(imageUrlString);
                    imageUrlString = questionCode.child("pid").getValue(String.class);
                    toUsersQuestions.child(pid).child("pid").setValue(imageUrlString);
                    imageUrlString = questionCode.child("pname").getValue(String.class);
                    toUsersQuestions.child(pid).child("pname").setValue(imageUrlString);
                    imageUrlString = questionCode.child("price").getValue(String.class);
                    toUsersQuestions.child(pid).child("price").setValue(imageUrlString);
                    imageUrlString = questionCode.child("quantity").getValue(String.class);
                    toUsersQuestions.child(pid).child("quantity").setValue(imageUrlString);
                    imageUrlString = questionCode.child("sellerAddress").getValue(String.class);
                    toUsersQuestions.child(pid).child("sellerAddress").setValue(imageUrlString);
                    imageUrlString = questionCode.child("time").getValue(String.class);
                    toUsersQuestions.child(pid).child("time").setValue(imageUrlString);
                    imageUrlString = questionCode.child("sid").getValue(String.class);
                    toUsersQuestions.child(pid).child("sid").setValue(imageUrlString);
                    imageUrlString = questionCode.child("lattitude").getValue(String.class);
                    toUsersQuestions.child(pid).child("lattitude").setValue(imageUrlString);
                    imageUrlString = questionCode.child("longitude").getValue(String.class);
                    toUsersQuestions.child(pid).child("longitude").setValue(imageUrlString);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}
////////////////////////////////////////////////////////////////////////////////


