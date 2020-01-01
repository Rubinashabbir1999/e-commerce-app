package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
//import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Model.Users;
import com.example.ecommerce.Prevalent.Prevalent;
import com.rey.material.widget.CheckBox;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginPage extends AppCompatActivity {

    private EditText Login_phoneInput,Login_PasswordInput;
    private Button loginButton;
    private TextView AdminLink, NotAdminLink;
    private ProgressDialog loadingBar;
    private CheckBox chkBoxRememberMe;

    private String parentDbName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);


        Login_phoneInput=(EditText)findViewById(R.id.Login_phone_input);
        Login_PasswordInput=(EditText)findViewById(R.id.Login_password_input);
        loginButton=(Button) findViewById(R.id.login_btn);
        //admin link initilization
        AdminLink=(TextView)findViewById(R.id.admin_panel_link);
        NotAdminLink=(TextView)findViewById(R.id.not_admin_panel_link);

        loadingBar=new ProgressDialog(this);

        chkBoxRememberMe = (CheckBox) findViewById(R.id.rememberMeChck);
        Paper.init(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                LoginUser();
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                loginButton.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                loginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });

    }

    private void LoginUser() {

        String phoneNumber = Login_phoneInput.getText().toString();
        String password = Login_PasswordInput.getText().toString();

        if(TextUtils.isEmpty(phoneNumber)){

            Toast.makeText( this,"Please enter your phone number",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){

            Toast.makeText( this,"Please enter your password",Toast.LENGTH_SHORT).show();
        }
        else {

            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(phoneNumber, password);

        }


    }

    private void AllowAccessToAccount(final String phoneNumber, final String password) {

        if(chkBoxRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.UserPhoneKey, phoneNumber);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }


        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(parentDbName).child(phoneNumber).exists()){

                    Users usersData = dataSnapshot.child(parentDbName).child(phoneNumber).getValue(Users.class);

                    if(usersData.getPhone().equals(phoneNumber)){

                        if(usersData.getPassword().equals(password)){

                            if(parentDbName.equals("Admins")){

                                Toast.makeText(LoginPage.this,"welcome Admin , you are Logged in successfully",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent nextPage = new Intent(LoginPage.this,AdminCategoryActivity.class);
                                startActivity(nextPage);
                            }
                            else if(parentDbName.equals("Users")){

                                Toast.makeText(LoginPage.this,"Logged in successfully",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent nextPage = new Intent(LoginPage.this,Main2Activity.class);
                                Prevalent.currentOnlineUser=usersData;
                                startActivity(nextPage);
                                }

                        }
                        else {
                            loadingBar.dismiss();
                            Toast.makeText(LoginPage.this,"Password is incorrect",Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                else {

                    Toast.makeText(LoginPage.this,"Account with this "+ phoneNumber+" number do not exists.",Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
