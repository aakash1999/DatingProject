package com.example.sisirkumarnanda.ithappens;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private Button mRegister;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mName;

    private RadioGroup mRadioGroup;

    //these are for authentication purpose
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //setting up the firebase authentication instance
        mAuth = FirebaseAuth.getInstance();

        //firebase auth state listner which checks and acts accordingly if the user is logged in or out
        firebaseAuthStateListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //this gets the auth state of the current user
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                /*
                checks if the user is null i.e. means there is no user and if there is a user then it takes
                to the MainActivity i.e. the Home screen
                 */
                if(user!=null)
                {
                    Intent intent = new Intent(RegistrationActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mRegister = (Button)findViewById(R.id.register);
        mEmail = (EditText)findViewById(R.id.email);
        mPassword = (EditText)findViewById(R.id.password);
        mRadioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        mName = (EditText)findViewById(R.id.name);

        //setting onclick for the register button
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectId = mRadioGroup.getCheckedRadioButtonId();
                final RadioButton radioButton = (RadioButton)findViewById(selectId);

                if(radioButton.getText() == null){
                    return;
                }




                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String name = mName.getText().toString();

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checing if the signup is successfull
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(RegistrationActivity.this, "Signup error!", Toast.LENGTH_SHORT).show();
                        }else{
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(radioButton.getText().toString()).child(userId).child("name");
                            currentUserDb.setValue(name);
                        }
                    }
                });
            }
        });
    }
    /*
    when the activity starts this method is called and the firebase authentication process starts
     */

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(firebaseAuthStateListner);
    }

     /*
    when the activity stops this method is called and the firebase authentication process stops
     */

    @Override
    protected void onStop() {
        super.onStop();

        mAuth.removeAuthStateListener(firebaseAuthStateListner);
    }
}
