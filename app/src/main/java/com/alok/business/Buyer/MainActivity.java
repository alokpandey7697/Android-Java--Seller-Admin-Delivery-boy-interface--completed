package com.alok.business.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alok.business.Delivery.DeliveryBoyHome;
import com.alok.business.Delivery.DeliveryBoyLogin;
import com.alok.business.Delivery.DeliveryRegistration;
import com.alok.business.Prevalent.Prevalent;
import com.alok.business.R;
import com.alok.business.Sellers.SellerHomeActivity;
import com.alok.business.Sellers.SellersRegistrationActivity;
import com.alok.business.here.Main_v;
import com.alok.business.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private Button joinButton,loginButton,deliveryJoinButton;
    private TextView deliveryLogin;
    private ProgressDialog loadingBar;
    private TextView sellerBegin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainn);
        loginButton=(Button)findViewById(R.id.main_start_login);
        sellerBegin=(TextView)findViewById(R.id.seller_begin);
        deliveryJoinButton=(Button)findViewById(R.id.delivery_joint_button);
        deliveryLogin=(TextView)findViewById(R.id.delivery_login);
        loadingBar=new ProgressDialog(this);
        Paper.init(this);

        deliveryLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DeliveryRegistration.class);
                startActivity(intent);
            }
        });
       /* deliveryJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
            Intent intent=new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            }
        });

        sellerBegin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(MainActivity.this, SellersRegistrationActivity.class);
                startActivity(intent);
            }
        });

       /* String UserPhoneKey=Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey=Paper.book().read(Prevalent.UserPasswordKey);
        if(UserPhoneKey!="" && UserPasswordKey!=""){
            if(! TextUtils.isEmpty(UserPhoneKey)&& ! TextUtils.isEmpty(UserPasswordKey)){
                AllowAcess(UserPhoneKey,UserPasswordKey);
                loadingBar.setTitle("Already logged in");
                loadingBar.setMessage("please wait...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();

      /*  FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null)
        {
            Intent intent = new Intent(MainActivity.this, Main_v.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }*/
    }

    private void AllowAcess(final String phone, final String password) {

        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Users").child(phone).exists()){
                    Users usersdata=dataSnapshot.child("Users").child(phone).getValue(Users.class);
                    if(usersdata.getPhone().equals(phone)){
                        if(usersdata.getPassword().equals(password)){
                            Toast.makeText(MainActivity.this, "logged in succesfully...", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent=new Intent(MainActivity.this, HomeActivity.class);
                            Prevalent.currentOnlineUsers=usersdata;
                            startActivity(intent);
                        }
                        else{
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Incorrect password! please enter valid password", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Create Account first", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void checkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            new AlertDialog.Builder(this)
                    .setMessage("Connection Error")
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }

    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

}
