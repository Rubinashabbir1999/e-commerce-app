package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccount_button;
    private EditText username,RegisterNumber,RegisterPassword;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CreateAccount_button =(Button)findViewById(R.id.CreateAccount_btn);            //create account button
        username =(EditText) findViewById(R.id.register_username_input);                //input username to registration
        RegisterPassword =(EditText) findViewById(R.id.register_password_input);        //input password to registration
        RegisterNumber =(EditText) findViewById(R.id.register_phone_input);                //input phone number
        loadingBar = new ProgressDialog(this);

        CreateAccount_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateAccount();
            }
        });
    }

    private void CreateAccount() {

        String name = username.getText().toString();
        String phoneNumber = RegisterNumber.getText().toString();
        String password = RegisterPassword.getText().toString();

        if(TextUtils.isEmpty(name)){

            Toast.makeText( this,"Please enter your name",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phoneNumber)){

            Toast.makeText( this,"Please enter your phone number",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){

            Toast.makeText( this,"Please enter your password",Toast.LENGTH_SHORT).show();
        }
        else {

            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatePhoneNumber(name,phoneNumber,password);     //function
        }

    }

    private void ValidatePhoneNumber(final String name, final String phoneNumber, final String password) {

    final DatabaseReference RootRef;
    RootRef = FirebaseDatabase.getInstance().getReference();

    RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if(!(dataSnapshot.child("Users").child(phoneNumber).exists())){

                HashMap<String, Object> userdataMap = new HashMap<>();
                userdataMap.put("phone", phoneNumber);
                userdataMap.put("password", password);
                userdataMap.put("name", name);
                RootRef.child("Users").child(phoneNumber).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful())
                        {
                            Toast.makeText(RegisterActivity.this, "Congratulations, your account has been created.", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(RegisterActivity.this, LoginPage.class);
                            startActivity(intent);
                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(RegisterActivity.this, "Network Error: Please try again after some time...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
            else{
                Toast.makeText(RegisterActivity.this,"This"+ phoneNumber +"already exists",Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
                Toast.makeText(RegisterActivity.this,"please try again ,use another phone number",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    }
}
