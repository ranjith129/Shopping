package com.dhruva.shopping;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
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

public class RegisterActivity extends AppCompatActivity {
    private Button CreateAccountButton;
    private EditText InputName, InputPhoneNumber, InputPassword;
    private ProgressDialog loadingBar;
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        CreateAccountButton = (Button) findViewById(R.id.register_btn);
        InputName = (EditText) findViewById(R.id.register_username_input);
        InputPassword = (EditText) findViewById(R.id.register_password_input);
        InputPhoneNumber = (EditText) findViewById(R.id.register_phone_number_input);
        loadingBar = new ProgressDialog(this);
        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });
    }
    private void CreateAccount(){
        String name = InputName.getText().toString();
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();
        if (TextUtils.isEmpty(name))
        {
            Log.d("Step_name", "Enter your name");
            Toast.makeText(this, "Please enter your name.", Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Please enter your name");
            mFirebaseAnalytics.logEvent("SignUp_EnterName_Error", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.SignUpEnterNameError", "Please enter your name");
            MobileCore.trackState("SignUpScreen", cData);
        }
        //Reg exp: "^[a-zA-Z\\\\s]{5,30}$"
        else if (!name.matches("^(?!\\s)(?![\\s\\S]*\\s$)[a-zA-Z\\s]{5,30}$")) {
            Log.d("Step_name", "Enter valid name");
            Toast.makeText(this, "Please enter valid name. Name Hint: Xerago/xerago/XeRaGo likewise with minimum 5 characters to maximum 30 characters", Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Please enter valid name");
            mFirebaseAnalytics.logEvent("SignUp_EnterName_ValidError", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.SignUpEnterNameValidError", "Please enter valid name");
            MobileCore.trackState("SignUpScreen", cData);
        }
        else if (TextUtils.isEmpty(phone))
        {
            Log.d("Step_name", "Enter your phone number");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Please enter your phone number");
            mFirebaseAnalytics.logEvent("SignUp_PhoneNumber_Error", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.SignUpPhoneNumberError", "Please enter your phone number");
            MobileCore.trackState("SignUpScreen", cData);
            Toast.makeText(this, "Please enter your phone number.", Toast.LENGTH_SHORT).show();
        }
        else if (!phone.matches("^[6-9]{1}[0-9]{9}$")) {
            Log.d("Step_name", "Enter valid phone number");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Enter valid phone number");
            mFirebaseAnalytics.logEvent("SignUp_PhoneNumber_ValidError", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.SignUpPhoneNumberValidError", "Enter valid phone number");
            MobileCore.trackState("SignUpScreen", cData);
            Toast.makeText(this, "Please enter valid phone number. Phone number Hint: First number start only with 6-9 maximum 10 digit", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Log.d("Step_name", "Enter your password");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Please enter your password");
            mFirebaseAnalytics.logEvent("SignUp_EnterPassword_Error", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.SignUpEnterPasswordError", "Please enter your password");
            MobileCore.trackState("SignUpScreen", cData);
            Toast.makeText(this, "Please enter your password.", Toast.LENGTH_SHORT).show();
        }
        else if (!password.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[~!@#$%^&*-+=()])[^\\s]{8,20}$")) {
            Log.d("Step_name", "Enter Valid password");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Enter valid password");
            mFirebaseAnalytics.logEvent("SignUp_EnterPassword_ValidError", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.SignUpEnterPasswordValidError", "Enter valid password");
            MobileCore.trackState("SignUpScreen", cData);
            Toast.makeText(this, "Please enter valid password. Password Hint: Password required combined with following all one digit/lower/upper/special character with maximum 8 to 20 characters", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials.");
            Log.d("Step_name", "Create Account");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Message: Your account created.");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.AccountCreated", "Your account created");
            MobileCore.trackState("SignUpScreen", cData);
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            ValidatephoneNumber(name, phone, password);
        }
    }

    private void ValidatephoneNumber(final String name, final String phone,final String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("Users").child(phone).exists())){
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phone);
                    userdataMap.put("password", password);
                    userdataMap.put("name", name);
                    RootRef.child("Users").child(phone).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful())
                            {
                                Toast.makeText(RegisterActivity.this, "Congratulations, your account has been created.", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                Log.d("Step_name", "Account Created");
                                Bundle bundle = new Bundle();
                                bundle.putString(FirebaseAnalytics.Param.METHOD, "Message: User account created.");
                                mFirebaseAnalytics.logEvent("Signup_UserAccount_Created", bundle);
                                HashMap cData = new HashMap<String, String>();
                                cData.put("cd.SignupUserAccountCreated", "User account created");
                                MobileCore.trackState("SignUpScreen", cData);
                                Intent intent = new Intent(RegisterActivity.this, com.dhruva.shopping.LoginActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                loadingBar.dismiss();
                                Log.d("Step_name", "Account creation failed - A");
                                Bundle bundle = new Bundle();
                                bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Account creation failed.");
                                mFirebaseAnalytics.logEvent("Signup_UserAccount_CreationFailed", bundle);
                                HashMap cData = new HashMap<String, String>();
                                cData.put("cd.SignupUserAccountCreationFailed", "Account creation failed");
                                MobileCore.trackState("SignUpScreen", cData);
                                Toast.makeText(RegisterActivity.this, "Network Error: Please try again after some time.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(RegisterActivity.this, "This " + phone + " already exists. Please try again using another phone number.", Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                    Log.d("Step_name", "Account Phone number already exists");
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Account Phone number already exists");
                    mFirebaseAnalytics.logEvent("Signup_PhoneNumber_AlreadyExists", bundle);
                    HashMap cData = new HashMap<String, String>();
                    cData.put("cd.SignupPhoneNumberAlreadyExists", "Account Phone number already exists");
                    MobileCore.trackState("SignUpScreen", cData);
                    //Toast.makeText(RegisterActivity.this, "Please try again using another phone number.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, com.dhruva.shopping.MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Step_name", "New Account Creation Cancelled");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Message: New Account Creation Cancelled");
                mFirebaseAnalytics.logEvent("Signup_Cancelled", bundle);
                HashMap cData = new HashMap<String, String>();
                cData.put("cd.SignupCancelled", "New Account Creation Cancelled");
                MobileCore.trackState("SignUpScreen", cData);
            }
        });
    }
}