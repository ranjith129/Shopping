package com.dhruva.shopping;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.adobe.marketing.mobile.MobileCore;
import com.dhruva.shopping.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
public class ResetPasswordActivity extends AppCompatActivity {

    private String checks = "";
    private TextView ResetpageTitle, TitleSecurityQuestions;
    private EditText FindPhoneNumber,SecurityQuestionsOne,SecurityQuestionsTwo;
    private Button PhoneVerifyBtn;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_reset_password);
        checks = getIntent().getStringExtra("Checks");
        ResetpageTitle = findViewById(R.id.reset_page_title);
        TitleSecurityQuestions = findViewById(R.id.title_security_questions);
        FindPhoneNumber = findViewById(R.id.find_phone_number);
        SecurityQuestionsOne = findViewById(R.id.security_questions_one);
        SecurityQuestionsTwo = findViewById(R.id.security_questions_two);
        PhoneVerifyBtn = findViewById(R.id.verify_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FindPhoneNumber.setVisibility(View.GONE);
        if(checks.equals("Settings")){
            ResetpageTitle.setText("Set Security Questions");
            TitleSecurityQuestions.setText("Kindly set answers the following security questions");
            PhoneVerifyBtn.setText("Set Answers");

            DisplayPreviousSecurityAnswers();
            PhoneVerifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSecurityAnswers();
                }
            });
            Log.d("Step_name", "Moving To Set Security Questions/Answers");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Moving To Set Security Questions/Answers");
            mFirebaseAnalytics.logEvent("MovingToSetSecurityQuestionsAnswers", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.screenName", "ResetPasswordScreen");
            MobileCore.trackState("ResetPasswordScreen", cData);
        }else if(checks.equals("Login")){
            FindPhoneNumber.setVisibility(View.VISIBLE);
            PhoneVerifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetPasswordUserVerify();
                }
            });
            Log.d("Step_name", "Moving to Reset Password");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Moving to Reset Password");
            mFirebaseAnalytics.logEvent("MovingToResetPassword", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.screenName", "ResetPasswordScreen");
            MobileCore.trackState("ResetPasswordScreen", cData);
        }

    }

    private void setSecurityAnswers()
    {
        String SecurityAnswerOne = SecurityQuestionsOne.getText().toString().toLowerCase();
        String SecurityAnswerTwo = SecurityQuestionsTwo.getText().toString().toLowerCase();
        if(SecurityQuestionsOne.equals("")){
            Log.d("Step_name", "Set First Security Question Answer");
            Bundle bundle = new Bundle();
            bundle.putString("SecurityQuestionsAnswerError", "Set First Security Question Answer");
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Set First Security Question Answer");
            mFirebaseAnalytics.logEvent("SetFirstSecurityQuestionsAnswers", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.SecurityQuestionsAnswerError", "Please answer first security question");
            cData.put("cd.screenName", "ResetPasswordScreen");
            MobileCore.trackState("ResetPasswordScreen", cData);
            Toast.makeText(ResetPasswordActivity.this, "Please answer first security question.", Toast.LENGTH_SHORT).show();
        }
        else if(SecurityQuestionsTwo.equals("")){
            Log.d("Step_name", "Set Second Security Question Answer");
            Bundle bundle = new Bundle();
            bundle.putString("SecurityQuestionsAnswerError", "Set Second Security Question Answer");
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Set Second Security Question Answer");
            mFirebaseAnalytics.logEvent("SetSecondSecurityQuestionsAnswers", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.SecurityQuestionsAnswerError", "Please answers second security question");
            cData.put("cd.screenName", "ResetPasswordScreen");
            MobileCore.trackState("ResetPasswordScreen", cData);
            Toast.makeText(ResetPasswordActivity.this, "Please answer second security question.", Toast.LENGTH_SHORT).show();
    }else{
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
            HashMap<String, Object> userdataMap = new HashMap<>();
            userdataMap.put("SecurityAnswerOne", SecurityAnswerOne);
            userdataMap.put("SecurityAnswerTwo", SecurityAnswerTwo);
            ref.child("SecurityQuestions").updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d("Step_name", "Security questions created successfully");
                        Bundle bundle = new Bundle();
                        bundle.putString("SecurityAnswerOne", SecurityAnswerOne);
                        bundle.putString("SecurityAnswerTwo", SecurityAnswerTwo);
                        bundle.putString(FirebaseAnalytics.Param.METHOD, "Security questions answers created successfully");
                        mFirebaseAnalytics.logEvent("SecurityQuestionsStatus", bundle);
                        HashMap cData = new HashMap<String, String>();
                        cData.put("cd.SecurityQuestionsStatus", "Security questions answers created successfully");
                        cData.put("cd.screenName", "ResetPasswordScreen");
                        MobileCore.trackState("ResetPasswordScreen", cData);
                        Toast.makeText(ResetPasswordActivity.this, "Security questions answers created successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ResetPasswordActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private void DisplayPreviousSecurityAnswers()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());

        ref.child("SecurityQuestions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
            if(datasnapshot.exists())
                {
                    Log.d("Step_name", "Display Previous Security Answers");
                    String SafeAnswerOne = datasnapshot.child("SecurityAnswerOne").getValue().toString();
                    String SafeAnswerTwo = datasnapshot.child("SecurityAnswerTwo").getValue().toString();
                    SecurityQuestionsOne.setText(SafeAnswerOne);
                    SecurityQuestionsTwo.setText(SafeAnswerTwo);
                    Bundle bundle = new Bundle();
                    bundle.putString("SecurityAnswerOne", SafeAnswerOne);
                    bundle.putString("SecurityAnswerTwo", SafeAnswerTwo);
                    bundle.putString(FirebaseAnalytics.Param.METHOD, "Display Previous Security Answers");
                    mFirebaseAnalytics.logEvent("SecurityQuestionsStatus", bundle);
                    HashMap cData = new HashMap<String, String>();
                    cData.put("cd.SecurityAnswerOne", SafeAnswerOne);
                    cData.put("cd.SecurityAnswerTwo", SafeAnswerTwo);
                    cData.put("cd.SecurityQuestionsStatus", "Display Previous Security Answers");
                    cData.put("cd.screenName", "ResetPasswordScreen");
                    MobileCore.trackState("ResetPasswordScreen", cData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }
    private void ResetPasswordUserVerify(){

        String phone = FindPhoneNumber.getText().toString();
        String SecurityAnswerOne = SecurityQuestionsOne.getText().toString().toLowerCase();
        String SecurityAnswerTwo = SecurityQuestionsTwo.getText().toString().toLowerCase();
        if (phone.equals("")){
            Log.d("Step_name", "Please enter the mobile number");
            Toast.makeText(this, "Please enter the mobile number", Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle();
            bundle.putString("ResetPasswordError", "Please enter the mobile number");
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Please enter the mobile number");
            mFirebaseAnalytics.logEvent("ResetPasswordError", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.ResetPasswordError", "Please enter the mobile number");
            cData.put("cd.screenName", "ResetPasswordScreen");
            MobileCore.trackState("ResetPasswordScreen", cData);
            }else if(SecurityAnswerOne.equals("")){
            Log.d("Step_name", "Please enter first security answer to proceed");
            Toast.makeText(this, "Please enter first security answer to proceed", Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle();
            bundle.putString("SecurityQuestionsAnswerError", "Please enter the first security question answer");
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Please enter the first security question answer");
            mFirebaseAnalytics.logEvent("SecurityQuestionsAnswerError", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.SecurityQuestionsAnswerError", "Please enter the first security question answer");
            cData.put("cd.screenName", "ResetPasswordScreen");
            MobileCore.trackState("ResetPasswordScreen", cData);
            }else if(SecurityAnswerTwo.equals("")){
            Toast.makeText(this, "Please enter second security question answer to proceed", Toast.LENGTH_SHORT).show();
            Log.d("Step_name", "Please enter second security question answer to proceed");
            Bundle bundle = new Bundle();
            bundle.putString("SecurityQuestionsAnswerError", "Please enter the second security question answer");
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Please enter the second security question answer");
            mFirebaseAnalytics.logEvent("SecurityQuestionsAnswerError", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.SecurityQuestionsAnswerError", "Please enter the second security question answer ");
            cData.put("cd.screenName", "ResetPasswordScreen");
            MobileCore.trackState("ResetPasswordScreen", cData);
           } else

        if (!phone.equals("") && !SecurityAnswerOne.equals("") && !SecurityAnswerTwo.equals("")){

            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(phone);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                    if(datasnapshot.exists()){
                        if(datasnapshot.hasChild("SecurityQuestions")){
                            String SafeAnswerOne = datasnapshot.child("SecurityQuestions").child("SecurityAnswerOne").getValue().toString();
                            String SafeAnswerTwo = datasnapshot.child("SecurityQuestions").child("SecurityAnswerTwo").getValue().toString();
                            if(!SafeAnswerOne.equals(SecurityAnswerOne)){
                                Log.d("Step_name", "First security questions answer is wrong");
                                Bundle bundle = new Bundle();
                                bundle.putString("SecurityQuestionsAnswerError", "First security questions answer is wrong");
                                mFirebaseAnalytics.logEvent("SecurityQuestionsAnswerStatus", bundle);
                                HashMap cData = new HashMap<String, String>();
                                cData.put("cd.SecurityQuestionsAnswerError", "First security questions answer is wrong");
                                cData.put("cd.screenName", "ResetPasswordScreen");
                                MobileCore.trackState("ResetPasswordScreen", cData);
                                Toast.makeText(ResetPasswordActivity.this, "First security questions answer is wrong", Toast.LENGTH_SHORT).show();
                            }else if(!SafeAnswerTwo.equals(SecurityAnswerTwo)){
                                Log.d("Step_name", "Second security questions answer is wrong");
                                Bundle bundle = new Bundle();
                                bundle.putString("SecurityQuestionsAnswerError", "Second security questions answer is wrong");
                                mFirebaseAnalytics.logEvent("SecurityQuestionsAnswerStatus", bundle);
                                HashMap cData = new HashMap<String, String>();
                                cData.put("cd.SecurityQuestionsAnswerError", "Second security questions answer is wrong");
                                cData.put("cd.screenName", "ResetPasswordScreen");
                                MobileCore.trackState("ResetPasswordScreen", cData);
                                Toast.makeText(ResetPasswordActivity.this, "Second security questions answer is wrong", Toast.LENGTH_SHORT).show();
                            }else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this, R.style.MaterialAlertDialog);
                                builder.setTitle("Set new password!");
                                builder.setMessage("Enter desired new password!");
                                builder.setIcon(R.drawable.password);
                                TextInputLayout textInputLayout = new TextInputLayout(ResetPasswordActivity.this);
                                final EditText newPassword = new EditText(ResetPasswordActivity.this);
                                FrameLayout container = new FrameLayout(ResetPasswordActivity.this);
                                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                int left_margin = ResetPasswordActivity.dpToPx(20, getResources());
                                int top_margin = ResetPasswordActivity.dpToPx(0, getResources());
                                int right_margin = ResetPasswordActivity.dpToPx(20, getResources());
                                int bottom_margin = ResetPasswordActivity.dpToPx(20, getResources());
                                params.setMargins(left_margin, top_margin, right_margin, bottom_margin);
                                textInputLayout.setLayoutParams(params);
                                textInputLayout.addView(newPassword);
                                newPassword.setHint("Enter new password.");
                                newPassword.setCursorVisible(true);
                                container.addView(textInputLayout);
                                builder.setView(container);
                                //builder.setView(newPassword);
                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        String finalNewPassword = newPassword.getText().toString();
                                        if(finalNewPassword.equals("")) {
                                            Toast.makeText(ResetPasswordActivity.this, "Please enter the password", Toast.LENGTH_SHORT).show();
                                            Log.d("Step_name", "Please enter the password");
                                            Bundle bundle = new Bundle();
                                            bundle.putString("ResetPasswordError", "Please enter the password");
                                            mFirebaseAnalytics.logEvent("ResetPasswordStatus", bundle);
                                            HashMap cData = new HashMap<String, String>();
                                            cData.put("cd.ResetPasswordError", "Please enter the password");
                                            cData.put("cd.screenName", "ResetPasswordScreen");
                                            MobileCore.trackState("ResetPasswordScreen", cData);
                                        }else if(!finalNewPassword.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[~!@#$%^&*-+=()])[^\\s]{8,20}$")){
                                            Toast.makeText(ResetPasswordActivity.this, "Please enter the valid password. Password Hint: Password required combined with following all one digit/lower/upper/special character with maximum 8 to 20 characters", Toast.LENGTH_SHORT).show();
                                            Log.d("Step_name", "Please enter the valid password");
                                            Bundle bundle = new Bundle();
                                            bundle.putString("ResetPasswordError", "Please enter the valid password");
                                            mFirebaseAnalytics.logEvent("ResetPasswordStatus", bundle);
                                            HashMap cData = new HashMap<String, String>();
                                            cData.put("cd.ResetPasswordError", "Please enter the valid password");
                                            cData.put("cd.screenName", "ResetPasswordScreen");
                                            MobileCore.trackState("ResetPasswordScreen", cData);

                                        } else{
                                            ref.child("password").setValue(finalNewPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                                        startActivity(intent);
                                                        Toast.makeText(ResetPasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                                                        Log.d("Step_name", "Password changed successfully");
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("ResetPasswordStatus", "Password changed successfully");
                                                        mFirebaseAnalytics.logEvent("ResetPasswordStatus", bundle);
                                                        HashMap cData = new HashMap<String, String>();
                                                        cData.put("cd.ResetPasswordStatus", "Password changed successfully");
                                                        cData.put("cd.screenName", "ResetPasswordScreen");
                                                        MobileCore.trackState("ResetPasswordScreen", cData);
                                                    }
                                                }
                                            });
                                        }
                                    }

                                });

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                        Bundle bundle = new Bundle();
                                        Log.d("Step_name", "Reset Password Cancelled");
                                        bundle.putString("ResetPasswordCancelled", "Reset Password Cancelled");
                                        mFirebaseAnalytics.logEvent("ResetPasswordCancelled", bundle);
                                        HashMap cData = new HashMap<String, String>();
                                        cData.put("cd.ResetPasswordCancelled", "Reset Password Cancelled");
                                        cData.put("cd.screenName", "ResetPasswordScreen");
                                        MobileCore.trackState("ResetPasswordScreen", cData);
                                    }

                                });

                                builder.show();
                            }

                        } else{
                            Toast.makeText(ResetPasswordActivity.this, "You have not set the security questions", Toast.LENGTH_SHORT).show();
                            Log.d("Step_name", "You have not set the security questions");
                            Bundle bundle = new Bundle();
                            bundle.putString("SecurityQuestionsAnswerError", "You have not set the security questions");
                            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: You have not set the security questions");
                            mFirebaseAnalytics.logEvent("SecurityQuestionsAnswerError", bundle);
                            HashMap cData = new HashMap<String, String>();
                            cData.put("cd.SecurityQuestionsAnswerError", "You have not set the security questions");
                            cData.put("cd.screenName", "ResetPasswordScreen");
                            MobileCore.trackState("ResetPasswordScreen", cData);
                        }

                    }else {
                        Toast.makeText(ResetPasswordActivity.this, "This phone number not exist, try again.", Toast.LENGTH_SHORT).show();
                        Log.d("Step_name", "This phone number not exist, try again.");
                        Bundle bundle = new Bundle();
                        bundle.putString("SecurityQuestionsAnswerError", "This phone number not exist, try again.");
                        bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: This phone number not exist, try again.");
                        mFirebaseAnalytics.logEvent("SecurityQuestionsAnswerError", bundle);
                        HashMap cData = new HashMap<String, String>();
                        cData.put("cd.SecurityQuestionsAnswerError", "This phone number not exist, try again.");
                        cData.put("cd.screenName", "ResetPasswordScreen");
                        MobileCore.trackState("ResetPasswordScreen", cData);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        /*else {
            Toast.makeText(ResetPasswordActivity.this, "Try again, entering phone number & security answers.", Toast.LENGTH_SHORT).show();
        }*/
    }

}