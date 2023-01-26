package com.alok.business.Delivery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alok.business.Buyer.LoginActivity;
import com.alok.business.Buyer.MainActivity;
import com.alok.business.Map.MapActivity;
import com.alok.business.Notification.SendNotificationPack.Token;
import com.alok.business.Prevalent.Prevalent;
import com.alok.business.R;
import com.alok.business.Sellers.SellerAddNewProductActivity;
import com.alok.business.Sellers.SellerHomeActivity;
import com.alok.business.Sellers.SellerLoginActivity;
import com.alok.business.Sellers.SellersRegistrationActivity;
import com.alok.business.models.DeliveryBoyModel;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class DeliveryRegistration extends AppCompatActivity {
    private Button AlreadyHaveAnAccount;
    private EditText nameInput,phoneInput,emailInput,passwordInput,addressInput;
    private Button registerbtn,sellerLoginBegin,sellerLoc,deliveryAlreadyAccount;
    private ImageView profileImage,upload;

    private String downloadimgeurl,saveCurrentDate,saveCurrentTime,ProductRandomKey;

    private FirebaseAuth auth;
    private ProgressDialog loadingBar;

    int LAUNCH_SECOND_ACTIVITY = 2;
    private   Uri imageUri;

    private static final int GalleryPic=3;
    private StorageReference imgstorageRef;

    String lattitude, longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_registration);

        auth = FirebaseAuth.getInstance();

        imgstorageRef= FirebaseStorage.getInstance().getReference().child("Profile Images");

        nameInput=findViewById(R.id.seller_name);
        phoneInput=findViewById(R.id.seller_phone);
        passwordInput=findViewById(R.id.seller_password);
        addressInput=findViewById(R.id.seller_address);
        emailInput=findViewById(R.id.seller_email);
        registerbtn=findViewById(R.id.seller_register_btn);
        loadingBar=new ProgressDialog(this);
        sellerLoc=findViewById(R.id.sellerLoc);
        profileImage=findViewById(R.id.profile_image);
        upload=findViewById(R.id.upload);
        deliveryAlreadyAccount = findViewById(R.id.delivery_already_account_btn);

        imageUri = Uri.parse("android.resource://com.alok.business//drawable/profile");


        profileImage.setOnClickListener(new View.OnClickListener() {
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

deliveryAlreadyAccount.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(DeliveryRegistration.this, DeliveryBoyLogin.class);
        startActivity(intent);
    }
});
        sellerLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(DeliveryRegistration.this, MapActivity.class);
                startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
            }
        });
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInfo()) {
                    StoreProductInformation();
                }
            }
        });
    }

public Boolean checkInfo() {
    String name = nameInput.getText().toString();
    String phone = phoneInput.getText().toString();
    String email = emailInput.getText().toString();
    String password = passwordInput.getText().toString();
    String address = addressInput.getText().toString();
    if(!name.equals("")&& !phone.equals("")&& !email.equals("") && !password.equals("")&& !address.equals("")  && lattitude != null && longitude != null && imageUri != null)
    {
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least six characters long ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    else{
        if(name.equals(""))
        {
            Toast.makeText(DeliveryRegistration.this, "please enter name",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (phone.equals("")) {
            Toast.makeText(DeliveryRegistration.this, "please enter phone no",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (email.equals("")) {
            Toast.makeText(DeliveryRegistration.this, "please enter email",Toast.LENGTH_SHORT).show();
            return false;
        }

        else if (password.equals("")) {
            Toast.makeText(DeliveryRegistration.this, "please enter password",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (address.equals("")) {
            Toast.makeText(DeliveryRegistration.this, "please enter address",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (lattitude == null && longitude == null ) {
            Toast.makeText(DeliveryRegistration.this, "please select locaion on Map",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(imageUri == null) {
            Toast.makeText(DeliveryRegistration.this, "please upload shop image",Toast.LENGTH_SHORT).show();
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
            profileImage.setImageURI(imageUri);
    }

    private void registerSeller() {
        String name = nameInput.getText().toString();
        String phone = phoneInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String address = addressInput.getText().toString();
        if(!name.equals("")&& !phone.equals("")&& !email.equals("") && !password.equals("")&& !address.equals("") && lattitude != null && longitude != null && imageUri != null) {
            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least six characters long ", Toast.LENGTH_SHORT).show();
            } else {
                loadingBar.setTitle("creating seller account");
                loadingBar.setMessage("please wait while we are checking credentials.");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    final DatabaseReference rootRef;
                                    rootRef = FirebaseDatabase.getInstance().getReference();

                                    String sid = auth.getCurrentUser().getUid();
                                    HashMap<String, Object> sellerMap = new HashMap<>();
                                    sellerMap.put("sid", sid);
                                    sellerMap.put("phone", phone);
                                    sellerMap.put("email", email);
                                    sellerMap.put("address", address);
                                    sellerMap.put("name", name);
                                    sellerMap.put("password", password);
//                                Log.d("latt",lattitude);
//                                Log.d("lattt",longitude);

                                    sellerMap.put("lattitude", lattitude);
                                    sellerMap.put("longitude", longitude);

                                    sellerMap.put("image", downloadimgeurl);

                                    rootRef.child(
                                            "DeliveryBoy"
                                    ).child(sid).updateChildren(sellerMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    loadingBar.dismiss();
                                                    Toast.makeText(DeliveryRegistration.this, "You are registered successfully", Toast.LENGTH_SHORT).show();
                                                    UpdateToken();
                                                    getUserData();
                                                    Intent intent = new Intent(DeliveryRegistration.this, all_orders.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(DeliveryRegistration.this, "Ooops..Retry", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }
                                    });

                                }
                                else
                                {
                                    loadingBar.dismiss();
                                    if(task.getException().getMessage().equals("The email address is already in use by another account.")) {
                                        Toast.makeText(DeliveryRegistration.this, "The email address is already in use by another account.", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(DeliveryRegistration.this, "Kindly check email and password", Toast.LENGTH_SHORT).show();
                                    }
                                }
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
                }
                else{
                    Toast.makeText(DeliveryRegistration.this, "Error!", Toast.LENGTH_SHORT).show();
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
                //Write your code if there's no result
            }

        }
        if(requestCode==GalleryPic && resultCode==RESULT_OK && data!=null){
            imageUri=data.getData();
            profileImage.setImageURI(imageUri);
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
                Toast.makeText(DeliveryRegistration.this, "Error:"+message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               // Toast.makeText(DeliveryRegistration.this, "Product Image Uploaded Successfully...", Toast.LENGTH_SHORT).show();
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
                       //     Toast.makeText(DeliveryRegistration.this, "got the product url successfully...", Toast.LENGTH_SHORT).show();
                            registerSeller();                        }
                    }
                });
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


