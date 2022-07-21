package com.dhruva.shopping;

import static android.os.Build.BOARD;
import static android.os.Build.BRAND;
import static android.os.Build.DEVICE;
import static android.os.Build.MANUFACTURER;
import static android.os.Build.MODEL;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.adobe.marketing.mobile.AdobeCallback;
import com.adobe.marketing.mobile.Analytics;
import com.adobe.marketing.mobile.Identity;
import com.adobe.marketing.mobile.InvalidInitException;
import com.adobe.marketing.mobile.Lifecycle;
import com.adobe.marketing.mobile.LoggingMode;
import com.adobe.marketing.mobile.MobileCore;
import com.adobe.marketing.mobile.Signal;
import com.adobe.marketing.mobile.Target;
import com.adobe.marketing.mobile.UserProfile;
import com.dhruva.shopping.Model.Users;
import com.dhruva.shopping.Prevalent.Prevalent;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private Button joinNowButton, loginButton;
    private ProgressDialog loadingBar;
    private FirebaseAnalytics mFirebaseAnalytics;
    private String cuniqueid,deviceUDID,deviceModel,deviceName,deviceManufacturer,deviceBoard,deviceBrand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        MobileCore.setApplication(getApplication());
        MobileCore.setLogLevel(LoggingMode.DEBUG);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_main);
        joinNowButton = (Button) findViewById(R.id.main_join_now_btn);
        loginButton = (Button) findViewById(R.id.main_login_btn);
        loadingBar = new ProgressDialog(this);

        Paper.init(this);
        Log.d("Step_name", "Application Started");
        String deviceUDID = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
        String deviceModel = MODEL;
        String deviceName = DEVICE;
        String deviceManufacturer = MANUFACTURER;
        String deviceBoard = BOARD;
        String deviceBrand = BRAND;
        Bundle bundle = new Bundle();
        bundle.putString("App_Open", "Application Opened");
        HashMap cData = new HashMap<String, String>() {{put("cd.AppOpened", "Main Activity");put("cd.screenName", "MainScreen");put("cd.deviceUDID", deviceUDID);put("cd.deviceModel", deviceModel);put("cd.deviceName", deviceName);put("cd.deviceManufacturer", deviceManufacturer);put("cd.deviceBoard", deviceBoard);put("cd.deviceBrand", deviceBrand);}};
        try{
            Target.registerExtension();
            Analytics.registerExtension();
            Identity.registerExtension();
            Lifecycle.registerExtension();
            Signal.registerExtension();
            UserProfile.registerExtension();
            MobileCore.start(new AdobeCallback () {
                @Override
                public void call(Object o) {
                    MobileCore.configureWithAppID("4fa03d1212c6/eac0d963ebae/launch-da73755d4a8b");
                }
            });
            Log.d("Step_name", "deviceUDID Creation" + deviceUDID);
            cData.put("cd.screenName", "MainScreen");
            cData.put("cd.CustomerUniqueID", cuniqueid);
            cData.put("cd.deviceUDID", deviceUDID);
            cData.put("cd.deviceModel", deviceModel);
            cData.put("cd.deviceName", deviceName);
            cData.put("cd.deviceManufacturer", deviceManufacturer);
            cData.put("cd.deviceBoard", deviceBoard);
            cData.put("cd.deviceBrand", deviceBrand);
            MobileCore.trackState("MainScreen", cData);
        } catch (InvalidInitException e) {
            e.printStackTrace();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Step_name", "Navigated to Login Section");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Button: Navigated to Login Section");
                bundle.putString("CustomerUniqueID", cuniqueid);
                bundle.putString("deviceUDID", deviceUDID);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
                HashMap cData = new HashMap<String, String>(){{
                put("cd.NavigationType", "Navigated to Login Section");
                put("cd.screenName", "MainScreen");
                put("cd.CustomerUniqueID", cuniqueid);
                put("cd.deviceUDID", deviceUDID);
                put("cd.deviceModel", deviceModel);
                put("cd.deviceName", deviceName);
                put("cd.deviceManufacturer", deviceManufacturer);
                put("cd.deviceBoard", deviceBoard);
                put("cd.deviceBrand", deviceBrand);
                }};
                MobileCore.trackState("MainScreen", cData);
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("CustomerUniqueID", cuniqueid);
                intent.putExtra("deviceUDID", deviceUDID);
                intent.putExtra("deviceModel", deviceModel);
                intent.putExtra("deviceName", deviceName);
                intent.putExtra("deviceManufacturer", deviceManufacturer);
                intent.putExtra("deviceBoard", deviceBoard);
                intent.putExtra("deviceBrand", deviceBrand);
                startActivity(intent);
            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d("Step_name", "Navigated to Sign in screen");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Button: Navigated to Sign in screen");
                //mFirebaseAnalytics.logEvent("button_clicked",null);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);
                HashMap cData = new HashMap<String, String>();
                cData.put("cd.NavigationType", "Navigated to Sign in screen");
                cData.put("cd.CustomerUniqueID", cuniqueid);
                cData.put("cd.deviceUDID", deviceUDID);
                cData.put("cd.deviceModel", deviceModel);
                cData.put("cd.deviceName", deviceName);
                cData.put("cd.deviceManufacturer", deviceManufacturer);
                cData.put("cd.deviceBoard", deviceBoard);
                cData.put("cd.deviceBrand", deviceBrand);
                cData.put("cd.screenName", "MainScreen");
                MobileCore.trackState("MainScreen", cData);
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                intent.putExtra("CustomerUniqueID", cuniqueid);
                intent.putExtra("deviceUDID", deviceUDID);
                intent.putExtra("deviceModel", deviceModel);
                intent.putExtra("deviceName", deviceName);
                intent.putExtra("deviceManufacturer", deviceManufacturer);
                intent.putExtra("deviceBoard", deviceBoard);
                intent.putExtra("deviceBrand", deviceBrand);
                startActivity(intent);
            }
        });

        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);
        if (UserPhoneKey != "" && UserPasswordKey != "")
        {
            if (!TextUtils.isEmpty(UserPhoneKey)  &&  !TextUtils.isEmpty(UserPasswordKey))
            {
                AllowAccess(UserPhoneKey, UserPasswordKey);
                Log.d("Step_name", "Getting Logged in");
                // Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Message: Getting Logged in");
                mFirebaseAnalytics.logEvent("Logged_In", bundle);
                cData.put("cd.CustomerUniqueID", cuniqueid);
                cData.put("cd.deviceUDID", deviceUDID);
                cData.put("cd.deviceModel", deviceModel);
                cData.put("cd.deviceName", deviceName);
                cData.put("cd.deviceManufacturer", deviceManufacturer);
                cData.put("cd.deviceBoard", deviceBoard);
                cData.put("cd.deviceBrand", deviceBrand);
                cData.put("cd.LoginType", "Getting Logged in");
                cData.put("cd.screenName", "MainScreen");
                MobileCore.trackState("MainScreen", cData);
                loadingBar.setTitle("Already Logged in.");
                loadingBar.setMessage("Please wait for few moments.");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }
    }

    private void AllowAccess(final String phone, final String password)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(phone).exists()){
                    Users usersData = dataSnapshot.child("Users").child(phone).getValue(Users.class);
                    if (usersData.getPhone().equals(phone))
                    {
                        if (usersData.getPassword().equals(password))
                        {
                            Log.d("Step_name", "Already logged in");
                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Param.METHOD, "Message: Already logged in");
                            mFirebaseAnalytics.logEvent("Already_LoggedIn", bundle);
                            HashMap cData = new HashMap<String, String>();
                            cData.put("cd.LoggedType", "Already logged in");
                            cData.put("cd.screenName", "MainScreen");
                            cData.put("cd.CustomerUniqueID", cuniqueid);
                            cData.put("cd.deviceUDID", deviceUDID);
                            cData.put("cd.deviceModel", deviceModel);
                            cData.put("cd.deviceName", deviceName);
                            cData.put("cd.deviceManufacturer", deviceManufacturer);
                            cData.put("cd.deviceBoard", deviceBoard);
                            cData.put("cd.deviceBrand", deviceBrand);
                            MobileCore.trackState("MainScreen", cData);
                            Toast.makeText(MainActivity.this, "Please wait, you are already logged in.", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            Prevalent.currentOnlineUser = usersData;
                            intent.putExtra("CustomerUniqueID", cuniqueid);
                            intent.putExtra("deviceUDID", deviceUDID);
                            intent.putExtra("deviceModel", deviceModel);
                            intent.putExtra("deviceName", deviceName);
                            intent.putExtra("deviceManufacturer", deviceManufacturer);
                            intent.putExtra("deviceBoard", deviceBoard);
                            intent.putExtra("deviceBrand", deviceBrand);
                            startActivity(intent);
                        }
                        else {
                            Log.d("Step_name", "Login Password Incorrect");
                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Login Password Incorrect");
                            mFirebaseAnalytics.logEvent("Login_Password_Incorrect", bundle);
                            HashMap cData = new HashMap<String, String>();
                            cData.put("cd.InputError", "Login Password Incorrect");
                            cData.put("cd.screenName", "MainScreen");
                            cData.put("cd.CustomerUniqueID", cuniqueid);
                            cData.put("cd.deviceUDID", deviceUDID);
                            cData.put("cd.deviceModel", deviceModel);
                            cData.put("cd.deviceName", deviceName);
                            cData.put("cd.deviceManufacturer", deviceManufacturer);
                            cData.put("cd.deviceBoard", deviceBoard);
                            cData.put("cd.deviceBrand", deviceBrand);
                            MobileCore.trackState("MainScreen", cData);
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this,"Password is incorrect. Enter correct password to proceed.",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    Log.d("Step_name", "Phone number do not exists");
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Phone number do not exists");
                    mFirebaseAnalytics.logEvent("Login_PhoneNumber_DoNotExists", bundle);
                    HashMap cData = new HashMap<String, String>();
                    cData.put("cd.InputError", "Phone number do not exists");
                    cData.put("cd.screenName", "MainScreen");
                    cData.put("cd.CustomerUniqueID", cuniqueid);
                    cData.put("cd.deviceUDID", deviceUDID);
                    cData.put("cd.deviceModel", deviceModel);
                    cData.put("cd.deviceName", deviceName);
                    cData.put("cd.deviceManufacturer", deviceManufacturer);
                    cData.put("cd.deviceBoard", deviceBoard);
                    cData.put("cd.deviceBrand", deviceBrand);
                    MobileCore.trackState("MainScreen", cData);
                    Toast.makeText(MainActivity.this, "Account with this " + phone + " number do not exists.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Step_name", "Login Operation Cancelled");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Message: Login Operation Cancelled");
                mFirebaseAnalytics.logEvent("Login_Operation_Cancelled", bundle);
                HashMap cData = new HashMap<String, String>();
                cData.put("cd.LoginType", "Login Operation Cancelled");
                cData.put("cd.screenName", "MainScreen");
                cData.put("cd.CustomerUniqueID", cuniqueid);
                cData.put("cd.deviceUDID", deviceUDID);
                cData.put("cd.deviceModel", deviceModel);
                cData.put("cd.deviceName", deviceName);
                cData.put("cd.deviceManufacturer", deviceManufacturer);
                cData.put("cd.deviceBoard", deviceBoard);
                cData.put("cd.deviceBrand", deviceBrand);
                MobileCore.trackState("MainScreen", cData);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MobileCore.setApplication(getApplication());
        HashMap cData = new HashMap<String, String>();
        cData.put("cd.category", "Shopping");
        cData.put("cd.ActivityType", "Activity on Resumed");
        cData.put("cd.screenName", "MainScreen");
        cData.put("cd.CustomerUniqueID", cuniqueid);
        cData.put("cd.deviceUDID", deviceUDID);
        cData.put("cd.deviceModel", deviceModel);
        cData.put("cd.deviceName", deviceName);
        cData.put("cd.deviceManufacturer", deviceManufacturer);
        cData.put("cd.deviceBoard", deviceBoard);
        cData.put("cd.deviceBrand", deviceBrand);
        //MobileCore.trackState("Login Screen", cData);
        MobileCore.lifecycleStart(cData);
        MobileCore.trackState("MainScreen", cData);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobileCore.lifecyclePause();
        HashMap cData = new HashMap<String, String>();
        cData.put("cd.category", "Shopping");
        cData.put("cd.ActivityType", "Activity on Paused");
        cData.put("cd.screenName", "MainScreen");
        cData.put("cd.CustomerUniqueID", cuniqueid);
        cData.put("cd.deviceUDID", deviceUDID);
        cData.put("cd.deviceModel", deviceModel);
        cData.put("cd.deviceName", deviceName);
        cData.put("cd.deviceManufacturer", deviceManufacturer);
        cData.put("cd.deviceBoard", deviceBoard);
        cData.put("cd.deviceBrand", deviceBrand);
        MobileCore.trackState("MainScreen", cData);
    }
}