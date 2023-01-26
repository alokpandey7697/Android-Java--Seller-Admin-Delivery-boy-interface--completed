package com.alok.business.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alok.business.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RagisterActivity extends AppCompatActivity {
    private Button createaccountbutton;
    private EditText Inputname,InputPhoneno,InputPassword,InputEmail;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ragister);
        createaccountbutton=(Button)findViewById(R.id.main_start_register);
        Inputname=(EditText)findViewById(R.id.register_username_input);
        InputPhoneno=(EditText)findViewById(R.id.register_phone_number_input);
        InputPassword=(EditText)findViewById(R.id.register_password_input);
        loadingBar=new ProgressDialog(this);
        InputEmail=(EditText)findViewById(R.id.register_email_input);
        createaccountbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createAccount();
            }
        });
    }

    private void createAccount() {
        String name=Inputname.getText().toString();
        String phone=InputPhoneno.getText().toString();
        String password=InputPassword.getText().toString();
        String email=InputEmail.getText().toString();
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please write your name...", Toast.LENGTH_SHORT).show();
        }
       else if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Please write your phone number...", Toast.LENGTH_SHORT).show();
        }
       else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please choose your password...", Toast.LENGTH_SHORT).show();
        }
       else if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please choose your password...", Toast.LENGTH_SHORT).show();
        }
       else{
           loadingBar.setTitle("create account");
           loadingBar.setMessage("please wait while we are checking credentials.");
           loadingBar.setCanceledOnTouchOutside(false);
           loadingBar.show();

           validatePhoneNumber(name,phone,password,email);
        }
    }

    private void validatePhoneNumber(final String name, final String phone, final String password, final String email) {
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.child("Users").child(phone).exists()){
                    HashMap<String,Object>userdataMap=new HashMap<>();
                    userdataMap.put("phone",phone);
                    userdataMap.put("password",password);
                    userdataMap.put("name",name);
                    userdataMap.put("email",email);
                    userdataMap.put("lattitude",getIntent().getStringExtra("lattitude"));
                    userdataMap.put("longitude",getIntent().getStringExtra("longitude"));
                    RootRef.child("Users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RagisterActivity.this, "Congractulations! your account has been created", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Intent intent = new Intent(RagisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    else{
                                        loadingBar.dismiss();
                                        Toast.makeText(RagisterActivity.this, "Network Error ! please try again...", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(RagisterActivity.this, "This"+phone+"Number already exits.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RagisterActivity.this, "Please try with another phone number", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(RagisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
