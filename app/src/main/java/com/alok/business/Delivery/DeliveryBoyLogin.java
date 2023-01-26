package com.alok.business.Delivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alok.business.Buyer.LoginActivity;
import com.alok.business.Notification.SendNotificationPack.Token;
import com.alok.business.Prevalent.Prevalent;
import com.alok.business.R;
import com.alok.business.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class DeliveryBoyLogin extends AppCompatActivity {


    private Button loginSellerBtn;
    private EditText emailInput,passwordInput;
    private ProgressDialog loadingBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_boy_login);

        auth = FirebaseAuth.getInstance();
        emailInput=findViewById(R.id.seller_login_email);
        passwordInput=findViewById(R.id.seller_login_password);
        loginSellerBtn=findViewById(R.id.seller_login_btn);
        loadingBar=new ProgressDialog(this);
        loginSellerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginSeller();
            }
        });
    }

    private void LoginSeller() {

        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();

        if( !email.equals("") && !password.equals("")) {

            loadingBar.setTitle("logging you...");
            loadingBar.setMessage("please wait while we are checking credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            auth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                getUserData();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loadingBar.dismiss();
                    Toast.makeText(DeliveryBoyLogin.this, "Please check email and password", Toast.LENGTH_SHORT).show();

                }
            });}
        else
        {
            Toast.makeText(this, "Please check email and password", Toast.LENGTH_SHORT).show();
        }
    }

    public void getUserData() {

        final DatabaseReference RootRef;
        final Users[] usersdata = new Users[1];
        String sid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        RootRef= FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("DeliveryBoy").child(sid).exists()){
                    Prevalent.currentOnlineUsers=dataSnapshot.child("DeliveryBoy").child(sid).getValue(Users.class);
                    UpdateToken();
loadingBar.dismiss();
                    Intent intent = new Intent(DeliveryBoyLogin.this, all_orders.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(DeliveryBoyLogin.this, "Please check email and password", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    FirebaseAuth.getInstance().signOut();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UpdateToken(){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        Token token= new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("DeliveryTokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }
}
