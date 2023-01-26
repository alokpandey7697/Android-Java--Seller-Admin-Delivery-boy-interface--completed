package com.alok.business.Sellers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alok.business.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SellerAddNewProductActivity extends AppCompatActivity {
private String CategoryName,Description,Price,pname,saveCurrentDate,saveCurrentTime,ProductRandomKey,downloadimgeurl;
private Button AddNewProduct;
private static final int GalleryPic=1;
private Uri imageUri;
private ImageView InputProductImage;
private EditText ProductName,ProductPrice,ProductDescription;
private StorageReference imgstorageRef;
private DatabaseReference databaseReference,sellerRef;
private ProgressDialog loadingBar;
private String sName,sPhone,sAddress,sEmail,sID;
private String lattitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_add_product);
        if(getIntent().getExtras()!= null)
        CategoryName = getIntent().getExtras().get("category").toString();
        imgstorageRef= FirebaseStorage.getInstance().getReference().child("Product Images");

        databaseReference=FirebaseDatabase.getInstance().getReference().child("products");
        sellerRef=FirebaseDatabase.getInstance().getReference().child("Seller");


        Toast.makeText(this, CategoryName, Toast.LENGTH_SHORT).show();
        AddNewProduct=(Button)findViewById(R.id.add_product);
        ProductName=(EditText)findViewById(R.id.product_name);
        ProductDescription=(EditText)findViewById(R.id.product_description);
        ProductPrice=(EditText)findViewById(R.id.product_price);
        InputProductImage=(ImageView)findViewById(R.id.select_product_image);
        loadingBar=new ProgressDialog(this);

        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openGallery();
            }
        });
        AddNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductdata();
            }
        });

        sellerRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            sName = dataSnapshot.child("name").getValue().toString();
                            sPhone = dataSnapshot.child("phone").getValue().toString();
                            sAddress = dataSnapshot.child("address").getValue().toString();
                            sID = dataSnapshot.child("sid").getValue().toString();
                            sEmail = dataSnapshot.child("email").getValue().toString();
                            lattitude = dataSnapshot.child("lattitude").getValue().toString();
                            longitude = dataSnapshot.child("longitude").getValue().toString();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void openGallery() {
        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPic);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GalleryPic && resultCode==RESULT_OK && data!=null){
            imageUri=data.getData();
            InputProductImage.setImageURI(imageUri);

        }
    }
    public void ValidateProductdata(){
 Description=ProductDescription.getText().toString();
 Price=ProductPrice.getText().toString();
 pname=ProductName.getText().toString();

 if(imageUri==null){
     Toast.makeText(this, "Product Image is Mandatory...", Toast.LENGTH_SHORT).show();
 }
 else if(TextUtils.isEmpty(Description)){

     Toast.makeText(this, "Please write product description...", Toast.LENGTH_SHORT).show();
 }
 else if(TextUtils.isEmpty(Price)){

     Toast.makeText(this, "Please write product price...", Toast.LENGTH_SHORT).show();
 }
 else if(TextUtils.isEmpty(pname)){

     Toast.makeText(this, "Please write product name...", Toast.LENGTH_SHORT).show();
 }
 else{
     StoreProductInformation();
 }
    }

    private void StoreProductInformation() {

        loadingBar.setTitle("Add new product");
        loadingBar.setMessage("please wait while we are adding product...");
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
                Toast.makeText(SellerAddNewProductActivity.this, "Error:"+message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SellerAddNewProductActivity.this, "Product Image Uploaded Successfully...", Toast.LENGTH_SHORT).show();
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
                    //   Toast.makeText(SellerAddNewProductActivity.this, "got the product url successfully...", Toast.LENGTH_SHORT).show();
                  SaveProductInfoToDatabase();
                   }
                    }
                });
            }
        });
    }

    private void SaveProductInfoToDatabase() {
        HashMap<String,Object>productMap=new HashMap<>();
        productMap.put("pid",sPhone+ProductRandomKey);
        productMap.put("time",saveCurrentTime);
        productMap.put("date",saveCurrentDate);
        productMap.put("description",Description);
        productMap.put("image",downloadimgeurl);
        productMap.put("category",CategoryName);
        productMap.put("price",Price);
        productMap.put("pname",pname);

        productMap.put("sellerName",sName);
        productMap.put("sellerAddress",sAddress);
        productMap.put("sellerPhone",sPhone);
        productMap.put("sellerEmail",sEmail);
        productMap.put("sid",sID);
        productMap.put("lattitude",lattitude);
        productMap.put("longitude",longitude);
        productMap.put("productState","Approved");
        databaseReference.child(sPhone+ProductRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
//                            Intent intent=new Intent(SellerAddNewProductActivity.this, SellerHomeActivity.class);
//                            startActivity(intent);
//                            loadingBar.dismiss();
//                            Toast.makeText(SellerAddNewProductActivity.this, "Product added succesfully...", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(SellerAddNewProductActivity.this, After_add_new.class);
                            intent.putExtra("category", CategoryName);
                            startActivity(intent);
                            loadingBar.dismiss();
                            Toast.makeText(SellerAddNewProductActivity.this, "Product added successfully...", Toast.LENGTH_SHORT).show();

                        }
                        else{
                            loadingBar.dismiss();
                            String Message=task.getException().toString();
                            Toast.makeText(SellerAddNewProductActivity.this, "Error"+Message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    protected  void sellerProductInfo()
    {}
}
