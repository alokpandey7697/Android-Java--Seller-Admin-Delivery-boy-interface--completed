package com.alok.business.Buyer;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alok.business.Admin.AdminHomeActivity;
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

public class LoginActivity extends AppCompatActivity {


    private Button loginSellerBtn;
    private EditText emailInput,passwordInput;
    private ProgressDialog loadingBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                    Toast.makeText(LoginActivity.this, "Please check email and password", Toast.LENGTH_SHORT).show();
                }
            });
        }
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

                if(dataSnapshot.child("Admin").child(sid).exists()){
                    Prevalent.currentOnlineUsers=dataSnapshot.child("Admin").child(sid).getValue(Users.class);
                    UpdateToken();
                    loadingBar.dismiss();
                    Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this, "You are not Admin.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
FirebaseAuth.getInstance().signOut();                }
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
Log.d("Adminn", FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseDatabase.getInstance().getReference("AdminTokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);

    }

    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }


}





































/*

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alok.business.Admin.AdminHomeActivity;
import com.alok.business.Auth.VerificationActivity;
import com.alok.business.Notification.SendNotificationPack.Token;
import com.alok.business.Prevalent.Prevalent;
import com.alok.business.R;
import com.alok.business.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hbb20.CountryCodePicker;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    EditText InputPhoneNumber,InputPassword;
    Button LoginButton;
    private ProgressDialog loadingBar;
    String ParentDBName="Admin", phone, password;
    CheckBox chkbx;
    TextView AdminLink,NotAdminLink,ForgetPasswordLink;
    private CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginButton=(Button)findViewById(R.id.main_start_login);
        InputPhoneNumber=(EditText)findViewById(R.id.login_phone_number_input);
        InputPassword=(EditText)findViewById(R.id.login_password_input);
        chkbx=(com.rey.material.widget.CheckBox)findViewById(R.id.remember_me);
        AdminLink=(TextView)findViewById(R.id.admin_panel_link);
        ForgetPasswordLink=(TextView)findViewById(R.id.forget_password_link);
        loadingBar=new ProgressDialog(this);
        countryCodePicker = findViewById(R.id.ccp);
        countryCodePicker.registerCarrierNumberEditText(InputPhoneNumber);

        Paper.init(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginUser();
            }
        });

        ForgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check","login");
                startActivity(intent);
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginButton.setText("Login Admin");
                AdminLink.setVisibility(v.INVISIBLE);
                NotAdminLink.setVisibility(v.VISIBLE);
                ParentDBName="Admin";
            }
        });
    }

    private void LoginUser() {
         phone=InputPhoneNumber.getText().toString();
         password=InputPassword.getText().toString();

                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(this, "Please write your phone number...", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(password)){
                    Toast.makeText(this, "Please choose your password...", Toast.LENGTH_SHORT).show();
                }
                else{
                    loadingBar.setTitle("login account");
                    loadingBar.setMessage("please wait while we are checking credentials.");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    if(TextUtils.isEmpty(InputPhoneNumber.getText().toString())){
                        Toast.makeText(LoginActivity.this, "Enter No ....", Toast.LENGTH_SHORT).show();
                    }
                    else if(InputPhoneNumber.getText().toString().replace(" ","").length()!=10){
                        Toast.makeText(LoginActivity.this, "Enter Correct No ...", Toast.LENGTH_SHORT).show();
                    }
                    else {

                       // AllowAccessToAccount(phone,password,"SUCESS");


                        Intent i = new Intent(LoginActivity.this, VerificationActivity.class);
                        i.putExtra("number",countryCodePicker.getFullNumberWithPlus().replace(" ",""));
                        startActivityForResult(i, 1);
                    }

                }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                String sucess = data.getStringExtra("result");
                AllowAccessToAccount(phone,password,sucess);
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this,"OTP VERIFICATION FAILED",Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }



    private void AllowAccessToAccount(final String phone, final String password, final String sucess) {
        if(chkbx.isChecked() && sucess.equalsIgnoreCase("SUCESS")){
            Paper.book().write(Prevalent.UserPhoneKey,phone);
            Paper.book().write(Prevalent.UserPasswordKey,password);
        }
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(ParentDBName).child(phone).exists()){
                    Users usersdata=dataSnapshot.child(ParentDBName).child(phone).getValue(Users.class);
                    if(usersdata.getPhone().equals(phone)){
                        if(usersdata.getPassword().equals(password)) {
                            if (ParentDBName.equals("Admin")) {
                                 Toast.makeText(LoginActivity.this, "Welcome Admin you are logged in succesfully...", Toast.LENGTH_SHORT).show();
                                   loadingBar.dismiss();


                                   Intent intent=new Intent(LoginActivity.this, AdminHomeActivity.class);
                                 startActivity(intent);
                            }
                            else if(ParentDBName.equals("Users")){
                                Toast.makeText(LoginActivity.this, "logged in succesfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                UpdateToken();
                                Intent intent=new Intent(LoginActivity.this, HomeActivity.class);
                                Prevalent.currentOnlineUsers=usersdata;
                                startActivity(intent);
                            }
                        }
                        else{
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Incorrect password! please enter valid password", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(LoginActivity.this, "Create Account first", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
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
        FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }

}
*/
