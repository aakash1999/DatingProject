package com.example.sisirkumarnanda.ithappens;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";

    private EditText mNameEditText,mPhoneEditText;
    private Button mBackButton,mSubmitButton;

    private ImageView mProfileImage;

    //declaring mAuth to get the current userid
     private FirebaseAuth mAuth;

     //creating database reference
    private DatabaseReference mCustomerDataBase;

    private String userId,name,phone,profileImageUrl;


    private String userSex;


    //to store the image of the user
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //getting the user sex
        userSex = getIntent().getStringExtra("userSex");

        initElements();

        //getting the current user id of the user
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        //creating the child customers under users to store information about users
        mCustomerDataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(userId);

        //getting user information
        getUserInfo();

        //uploading the image form the personal gallery
        setProfileImage();

        //assigning onClick to the submit button to save users details
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });


    }

    private void setProfileImage() {
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });
    }

    /*
        here we get the information about the user
    * */
    private void getUserInfo() {

        mCustomerDataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()&&dataSnapshot.getChildrenCount()>0){
                    Map<String,Object> map = (Map<String,Object>)dataSnapshot.getValue();
                    Log.d(TAG, "onDataChange: " + map);

                    if(map.get("name")!=null)
                    {
                        name = map.get("name").toString();
                        mNameEditText.setText(name);}
                    if(map.get("phone")!=null) {
                        phone = map.get("phone").toString();
                        mPhoneEditText.setText(phone);
                    }

                    if(map.get("profileImageUrl")!=null) {
                        profileImageUrl = map.get("profileImageUrl").toString();

                        //using glide to set the profile image for the user on the settings screen
                        Glide.with(getApplication()).load(profileImageUrl).into(mProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*
        Here we set/update the information about the user
    * */

    private void saveUserInformation() {

        name = mNameEditText.getText().toString();
        phone = mPhoneEditText.getText().toString();

        //creating a Hashmap to store userInfo
        Map userInfo = new HashMap();
        userInfo.put("name",name);
        userInfo.put("phone",phone);

        //now updating the children in the database
        mCustomerDataBase.updateChildren(userInfo);

        //here we wirte the code to save the profile image for the user
        if(resultUri!=null){

            //creating a storage reference its actually quite similiar to databaase reference
            StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userId);

            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //now using ByteArrayOutputStream we upload the image
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,20,byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();

            //creating an upload task to faciliate uploading of the image
            UploadTask uploadTask  = filepath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    //now we can save the image url to the database
                    Map profileImageUri = new HashMap();
                    profileImageUri.put("profileImageUrl",downloadUrl.toString());


                    finish();
                    return;
                }
            });


        }else{
            finish();
        }
    }

    public void initElements(){
        mNameEditText = (EditText)findViewById(R.id.nameEditText);
        mPhoneEditText = (EditText)findViewById(R.id.phoneEditText);
        mProfileImage = (ImageView)findViewById(R.id.profileImage);
        mSubmitButton = (Button)findViewById(R.id.submitButton);
        mBackButton = (Button)findViewById(R.id.backButton);

    }

    /*
        This activity is for the result of the intent started for setting up image from external storage means getting the
        image uri from the external storage
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1&&resultCode== Activity.RESULT_OK)
        {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }
}
