package com.dhruva.shopping;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.dhruva.shopping.Model.Users;
import com.dhruva.shopping.Prevalent.Prevalent;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private Button joinNowButton, loginButton;
    private ProgressDialog loadingBar;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_main);
        joinNowButton = (Button) findViewById(R.id.main_join_now_btn);
        loginButton = (Button) findViewById(R.id.main_login_btn);
        loadingBar = new ProgressDialog(this);
        Paper.init(this);
        Log.d("Step_name", "Application Started");
        Bundle bundle = new Bundle();
        bundle.putString("App_Open", "Application Opened");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Step_name", "Navigated to Login Section");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Button: Navigated to Login Section");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
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
                            Toast.makeText(MainActivity.this, "Please wait, you are already logged in.", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            Prevalent.currentOnlineUser = usersData;
                            startActivity(intent);
                        }
                        else {
                            Log.d("Step_name", "Login Password Incorrect");
                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Login Password Incorrect");
                            mFirebaseAnalytics.logEvent("Login_Password_Incorrect", bundle);
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
            }
        });
    }
}