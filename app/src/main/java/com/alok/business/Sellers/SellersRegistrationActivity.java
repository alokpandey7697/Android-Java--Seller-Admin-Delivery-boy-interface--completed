package com.alok.business.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alok.business.Buyer.LoginActivity;
import com.alok.business.Buyer.MainActivity;
import com.alok.business.Delivery.DeliveryRegistration;
import com.alok.business.Map.MapActivity;
import com.alok.business.Notification.SendNotificationPack.Token;
import com.alok.business.Prevalent.Prevalent;
import com.alok.business.R;
import com.alok.business.here.Main_v;
import com.alok.business.models.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SellersRegistrationActivity extends AppCompatActivity {

    private Button AlreadyHaveAnAccount;
    private EditText nameInput,phoneInput,emailInput,passwordInput,addressInput,category;
    private Button registerbtn,sellerLoginBegin,sellerLoc;

    private ImageView shopImage,upload;
    private String downloadimgeurl,saveCurrentDate,saveCurrentTime,ProductRandomKey;
    private Uri imageUri;
    private static final int GalleryPic=3;
    private StorageReference imgstorageRef;


    private FirebaseAuth auth;
    private ProgressDialog loadingBar;
    int LAUNCH_SECOND_ACTIVITY = 1;
    String lattitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sellers_registration);

        auth = FirebaseAuth.getInstance();

        imgstorageRef= FirebaseStorage.getInstance().getReference().child("Shop Images");


        AlreadyHaveAnAccount = (Button)findViewById(R.id.seller_already_account_btn);
        nameInput=findViewById(R.id.seller_name);
        phoneInput=findViewById(R.id.seller_phone);
        passwordInput=findViewById(R.id.seller_password);
        addressInput=findViewById(R.id.seller_address);
        emailInput=findViewById(R.id.seller_email);
        registerbtn=findViewById(R.id.seller_register_btn);
        loadingBar=new ProgressDialog(this);
        sellerLoc=findViewById(R.id.sellerLoc);
        category = findViewById(R.id.seller_category);

        shopImage=findViewById(R.id.shop_image);
        upload=findViewById(R.id.upload);
        imageUri = Uri.parse("android.resource://com.alok.business//drawable/shop_24");
        shopImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        AlreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellersRegistrationActivity.this,SellerLoginActivity.class);
                startActivity(intent);
            }
        });
        sellerLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SellersRegistrationActivity.this, MapActivity.class);
                startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
            }
        });
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInfo())
               StoreProductInformation();
            }
        });
    }
    public Boolean checkInfo() {
        String name = nameInput.getText().toString();
        String phone = phoneInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String address = addressInput.getText().toString();
        if(!name.equals("")&& !phone.equals("")&& !email.equals("") && !password.equals("")&& !address.equals("") && lattitude != null && longitude != null && imageUri != null )
        { if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least six characters long ", Toast.LENGTH_SHORT).show();
return false;
        }
            return true;
    }
    else{
        if(name.equals(""))
        {
            Toast.makeText(SellersRegistrationActivity.this, "please enter name",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (phone.equals("")) {
            Toast.makeText(SellersRegistrationActivity.this, "please enter phone no",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (email.equals("")) {
            Toast.makeText(SellersRegistrationActivity.this, "please enter email",Toast.LENGTH_SHORT).show();
            return false;
        }

        else if (password.equals("")) {
            Toast.makeText(SellersRegistrationActivity.this, "please enter password",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (address.equals("")) {
            Toast.makeText(SellersRegistrationActivity.this, "please enter address",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (lattitude == null && longitude == null ) {
            Toast.makeText(SellersRegistrationActivity.this, "please select locaion on Map",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(imageUri == null) {
            Toast.makeText(SellersRegistrationActivity.this, "please upload shop image",Toast.LENGTH_SHORT).show();
            return false;
        }

        return false;
    }

    }

    private void openGallery() {
        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPic);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(imageUri != null)
            shopImage.setImageURI(imageUri);
    }


    private void registerSeller() {
        String name = nameInput.getText().toString();
        String phone = phoneInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String address = addressInput.getText().toString();
        String categoryy = category.getText().toString();
        if(!name.equals("")&& !phone.equals("")&& !email.equals("") && !password.equals("")&& !address.equals("")&& lattitude != null && longitude != null && imageUri != null)
        {
            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least six characters long ", Toast.LENGTH_SHORT).show();

            } else {
            loadingBar.setTitle("creating seller account");
            loadingBar.setMessage("please wait while we are checking credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                final DatabaseReference rootRef;
                                rootRef = FirebaseDatabase.getInstance().getReference();

                                String sid = auth.getCurrentUser().getUid();
                                HashMap<String, Object> sellerMap = new HashMap<>();
                                sellerMap.put("sid",sid);
                                sellerMap.put("phone",phone);
                                sellerMap.put("email",email);
                                sellerMap.put("address",address);
                                sellerMap.put("name",name);
                                sellerMap.put("password",password);

                                sellerMap.put("lattitude",lattitude);
                                sellerMap.put("longitude",longitude);
                                sellerMap.put("category",categoryy);
sellerMap.put("status","NotApproved");
                                sellerMap.put("image",downloadimgeurl);
                                rootRef.child(
                                        "Seller"
                                ).child(sid).updateChildren(sellerMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                loadingBar.dismiss();
                                                Toast.makeText(SellersRegistrationActivity.this, "You are registered successfully", Toast.LENGTH_SHORT).show();
                                                getUserData();
UpdateToken();
Intent intent = new Intent(SellersRegistrationActivity.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SellersRegistrationActivity.this, "Error ! Retry",Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                });

                            } else
                            {
                                loadingBar.dismiss();
                                if(task.getException().getMessage().equals("The email address is already in use by another account.")) {
                                    Toast.makeText(SellersRegistrationActivity.this, "The email address is already in use by another account.", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(SellersRegistrationActivity.this, "Kindly check email and password", Toast.LENGTH_SHORT).show();
                                }                            }
                        }
                    });
        }
        }
        else
        {
            loadingBar.dismiss();
            Toast.makeText(this, "Please complete the registration form", Toast.LENGTH_SHORT).show();
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
                }
                else{
                    Toast.makeText(SellersRegistrationActivity.this, "Error! Retry", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    finish();
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

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == RESULT_OK){
                addressInput.setText(data.getStringExtra("result"));
                lattitude = data.getStringExtra("lattitude");
                longitude = data.getStringExtra("longitude");
            }
            if (resultCode == RESULT_CANCELED) {
            }
        }

        if(requestCode==GalleryPic && resultCode==RESULT_OK && data!=null){
            imageUri=data.getData();
            shopImage.setImageURI(imageUri);
        }
    }


    private void StoreProductInformation() {

        loadingBar.setTitle("New Profile");
        loadingBar.setMessage("please wait while we are creating profile...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currenrDate=new SimpleDateFormat("MMM dd ,yyyy");
        saveCurrentDate=currenrDate.format(calendar.getTime());

        SimpleDateFormat currenrTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currenrTime.format(calendar.getTime());
        ProductRandomKey=saveCurrentDate + saveCurrentTime;

        final StorageReference filepath=imgstorageRef.child(imageUri.getLastPathSegment() + ProductRandomKey+".jpg");
        final UploadTask uploadTask=filepath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message=e.toString();
                Toast.makeText(SellersRegistrationActivity.this, "Error:"+message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(SellersRegistrationActivity.this, "Shop Image Uploaded Successfully...", Toast.LENGTH_SHORT).show();
                Task<Uri>urlTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        downloadimgeurl=filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadimgeurl=task.getResult().toString();
                            //Toast.makeText(SellersRegistrationActivity.this, "got the product url successfully...", Toast.LENGTH_SHORT).show();
                            registerSeller();                        }
                    }
                });
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
                            Intent intent = new Intent(SellersRegistrationActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }
}
