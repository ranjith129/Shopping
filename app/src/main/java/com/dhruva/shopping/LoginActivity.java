package com.dhruva.shopping;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.adobe.marketing.mobile.MobileCore;
import com.dhruva.shopping.Admin.AdminCategoryActivity;
import com.dhruva.shopping.Model.Users;
import com.dhruva.shopping.Prevalent.Prevalent;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import java.util.HashMap;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private EditText InputPhoneNumber, InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;
    private TextView AdminLink, NotAdminLink, ForgetPasswordLink;
    private String parentDbName = "Users";
    private CheckBox chkBoxRememberMe;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_login);
        LoginButton = (Button) findViewById(R.id.login_btn);
        InputPassword = (EditText) findViewById(R.id.login_password_input);
        InputPhoneNumber = (EditText) findViewById(R.id.login_phone_number_input);
        AdminLink = (TextView) findViewById(R.id.admin_panel_link);
        NotAdminLink = (TextView) findViewById(R.id.not_admin_panel_link);
        ForgetPasswordLink = findViewById(R.id.forget_password_link);
        loadingBar = new ProgressDialog(this);
        chkBoxRememberMe = (CheckBox) findViewById(R.id.remember_me_chkb);
        Paper.init(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

        ForgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("Checks","Login");
                startActivity(intent);
            }
        });
        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                LoginButton.setText("Login as Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
                Log.d("Step_name", "Login as Admin");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Message: Login as Admin");
                mFirebaseAnalytics.logEvent("Admin_Login", bundle);
                HashMap cData = new HashMap<String, String>();
                cData.put("cd.LoginType", "Login as Admin");
                cData.put("cd.screenName", "LoginScreen");
                MobileCore.trackState("LoginScreen", cData);
            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                LoginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
                Log.d("Step_name", "Login as user");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Message: Login as User");
                mFirebaseAnalytics.logEvent("User_Login", bundle);
                HashMap cData = new HashMap<String, String>();
                cData.put("cd.LoginType", "Login as User");
                cData.put("cd.screenName", "LoginScreen");
                MobileCore.trackState("LoginScreen", cData);
            }
        });

    }
    private void LoginUser()
    {
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();
        if (TextUtils.isEmpty(phone))
        {
            Log.d("Step_name", "Enter your phone number");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Enter your phone number");
            mFirebaseAnalytics.logEvent("User_PhoneNumber_Error", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.InputError", "Enter your phone number");
            cData.put("cd.screenName", "LoginScreen");
            MobileCore.trackState("LoginScreen", cData);
            Toast.makeText(this, "Please enter your phone number.", Toast.LENGTH_SHORT).show();
        }else if (!phone.matches("^[6-9]{1}[0-9]{9}$")) {
            Log.d("Step_name", "Enter valid phone number - A");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Enter valid phone number");
            mFirebaseAnalytics.logEvent("User_PhoneNumber_ValidError", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.InputError", "Enter valid phone number");
            cData.put("cd.screenName", "LoginScreen");
            MobileCore.trackState("LoginScreen", cData);
            Toast.makeText(this, "Please enter valid phone number. Phone number Hint: First number start only with 6-9 maximum 10 digit", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Log.d("Step_name", "Enter password");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Enter password");
            mFirebaseAnalytics.logEvent("User_Password_Error", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.InputError", "Enter password");
            cData.put("cd.screenName", "LoginScreen");
            MobileCore.trackState("LoginScreen", cData);
            Toast.makeText(this, "Please enter your password.", Toast.LENGTH_SHORT).show();
        }else if (!password.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[~!@#$%^&*-+=()])[^\\s]{8,20}$")) {
            Log.d("Step_name", "Enter valid password");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Enter valid password");
            mFirebaseAnalytics.logEvent("User_Password_ValidError", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.InputError", "Enter valid password");
            cData.put("cd.screenName", "LoginScreen");
            MobileCore.trackState("LoginScreen", cData);
            Toast.makeText(this, "Please enter valid password. Password Hint: Password required combined with following all one digit/lower/upper/special character with maximum 8 to 20 characters", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            Log.d("Step_name", "Login into User Account");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Button: Login into User Account");
            mFirebaseAnalytics.logEvent("User_LoginAccount", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.LoginType", "Login into User Account");
            cData.put("cd.screenName", "LoginScreen");
            MobileCore.trackState("LoginScreen", cData);
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            AllowAccessToAccount(phone, password);
        }
    }

    private void AllowAccessToAccount(final String phone, final String password)
    {
        if(chkBoxRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentDbName).child(phone).exists()){
                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);
                    String cuniqueid = usersData.getCustomerUniqueID();
                    Paper.book().write(Prevalent.CustomerUniqueID, cuniqueid);

                    if (usersData.getPhone().equals(phone))
                    {
                        if (usersData.getPassword().equals(password))
                        {
                            if(parentDbName.equals("Admins"))
                            {
                                Toast.makeText(LoginActivity.this, "Welcome "+ phone +", you are logged in Successfully.", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Log.d("Step_name", "Admin Login Succeed");
                                Bundle bundle = new Bundle();
                                bundle.putString("CustomerUniqueID", cuniqueid);
                                bundle.putString(FirebaseAnalytics.Param.METHOD, "Message: Admin Login Succeed");
                                mFirebaseAnalytics.logEvent("Admin_Login_Succeed", bundle);
                                HashMap cData = new HashMap<String, String>();
                                cData.put("cd.LoginType", "Admin Login Succeed");
                                cData.put("cd.screenName", "LoginScreen");
                                cData.put("cd.CustomerUniqueID", cuniqueid);
                                MobileCore.trackState("LoginScreen", cData);
                                Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                //intent.putExtra("CustomerUniqueID",cuniqueid);
                                startActivity(intent);
                            }
                            else if (parentDbName.equals("Users")){
                                Toast.makeText(LoginActivity.this, "Welcome "+ phone +", Logged in Successfully.", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Log.d("Step_name", "User Login Succeed");
                                Bundle bundle = new Bundle();
                                bundle.putString("CustomerUniqueID", cuniqueid);
                                bundle.putString(FirebaseAnalytics.Param.METHOD, "Message: User Login Succeed");
                                mFirebaseAnalytics.logEvent("User_Login_Succeed", bundle);
                                HashMap cData = new HashMap<String, String>();
                                cData.put("cd.LoginType", "User Login Succeed");
                                cData.put("cd.screenName", "LoginScreen");
                                cData.put("cd.CustomerUniqueID", cuniqueid);
                                MobileCore.trackState("LoginScreen", cData);
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                //intent.putExtra("CustomerUniqueID",cuniqueid);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);
                            }
                        }
                        else {
                            loadingBar.dismiss();
                            Log.d("Step_name", "Password is incorrect");
                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Password is incorrect");
                            mFirebaseAnalytics.logEvent("Input_Password_Error", bundle);
                            HashMap cData = new HashMap<String, String>();
                            cData.put("cd.InputError", "Password is incorrect");
                            cData.put("cd.screenName", "LoginScreen");
                            MobileCore.trackState("LoginScreen", cData);
                            Toast.makeText(LoginActivity.this,"Password is incorrect.",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, "Account with this " + phone + " number do not exists.", Toast.LENGTH_SHORT).show();
                    Log.d("Step_name", "Phone number do not Exists");
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.METHOD, "Message: Phone number do not Exists");
                    mFirebaseAnalytics.logEvent("PhoneNumber_DoNotExists_Error", bundle);
                    HashMap cData = new HashMap<String, String>();
                    cData.put("cd.InputError", "Phone number do not Exists");
                    cData.put("cd.screenName", "LoginScreen");
                    MobileCore.trackState("LoginScreen", cData);
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Step_name", "Login Activity Cancelled");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Message: Login Activity Cancelled");
                mFirebaseAnalytics.logEvent("Login_Activity_Cancelled", bundle);
                HashMap cData = new HashMap<String, String>();
                cData.put("cd.LoginType", "Login Activity Cancelled");
                cData.put("cd.screenName", "LoginScreen");
                MobileCore.trackState("LoginScreen", cData);
            }
        });
    }
}