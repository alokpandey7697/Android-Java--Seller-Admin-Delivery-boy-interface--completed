package com.alok.business.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alok.business.Buyer.LoginActivity;
import com.alok.business.Buyer.MainActivity;
import com.alok.business.Notification.SendNotificationPack.Token;
import com.alok.business.Prevalent.Prevalent;
import com.alok.business.R;
import com.alok.business.here.Main_v;
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

public class SellerLoginActivity extends AppCompatActivity {

    private Button loginSellerBtn;
    private EditText emailInput,passwordInput;
    private ProgressDialog loadingBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);

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
                    Toast.makeText(SellerLoginActivity.this, "Please check email and password", Toast.LENGTH_SHORT).show();

                }
            });
        }
        else
        {
            Toast.makeText(this, "Please check email and password", Toast.LENGTH_SHORT).show();
        }
    }


    private void UpdateToken(){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        Token token= new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("SellerTokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }

    public void getUserData() {

        final DatabaseReference RootRef;
        final Users[] usersdata = new Users[1];
        String sid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        RootRef= FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("Seller").child(sid).exists()){
                    Prevalent.currentOnlineUsers=dataSnapshot.child("Seller").child(sid).getValue(Users.class);
                    if(Prevalent.currentOnlineUsers.getStatus().equals("NotApproved")) {
                        CharSequence options[] = new CharSequence[]
                                {

                                        "Ok"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(SellerLoginActivity.this);
                        builder.setTitle("Your account approval is pending !");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    dialog.cancel();
                                    loadingBar.dismiss();
                                }
                            }
                        });
                        builder.show();
                    }
                   else if(Prevalent.currentOnlineUsers.getStatus().equals("Approved")){
                        Intent intent = new Intent(SellerLoginActivity.this, SellerHomeActivity.class);
                        loadingBar.dismiss();
                        UpdateToken();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
                else{
                    Toast.makeText(SellerLoginActivity.this, "You are not a Seller", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    FirebaseAuth.getInstance().signOut();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
