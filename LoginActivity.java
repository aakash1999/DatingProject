package com.example.sisirkumarnanda.ithappens;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button mLogin;
    private EditText mEmail;
    private EditText mPassword;

    //these are for authentication purpose
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mLogin = (Button)findViewById(R.id.login);
        mEmail = (EditText)findViewById(R.id.email);
        mPassword = (EditText)findViewById(R.id.password);

        //setting onclick for the register button
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();

                //instead of creating a new user we actually sign in with the user
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checks if the user is present and signs in accordingly
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(LoginActivity.this, "Login error!", Toast.LENGTH_SHORT).show();
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

