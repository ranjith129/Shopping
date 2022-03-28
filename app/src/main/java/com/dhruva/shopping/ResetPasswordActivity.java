package com.dhruva.shopping;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
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

import com.dhruva.shopping.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
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
            PhoneVerifyBtn.setText("Set Questions");

            DispalyPreviousSecurityAnswers();
            PhoneVerifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSecurityAnswers();
                }
            });

        }else if(checks.equals("Login")){
            FindPhoneNumber.setVisibility(View.VISIBLE);
            PhoneVerifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResetPasswordUserVerify();
                }
            });
        }

    }

    private void setSecurityAnswers()
    {
        String SecurityAnswerOne = SecurityQuestionsOne.getText().toString().toLowerCase();
        String SecurityAnswerTwo = SecurityQuestionsTwo.getText().toString().toLowerCase();
        if(SecurityQuestionsOne.equals("") && SecurityQuestionsTwo.equals("")){
            Toast.makeText(ResetPasswordActivity.this, "Please answers both security questions", Toast.LENGTH_SHORT).show();
        }else{
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
            HashMap<String, Object> userdataMap = new HashMap<>();
            userdataMap.put("SecurityAnswerOne", SecurityAnswerOne);
            userdataMap.put("SecurityAnswerTwo", SecurityAnswerTwo);
            ref.child("SecurityQuestions").updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ResetPasswordActivity.this, "Security questions created successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ResetPasswordActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private void DispalyPreviousSecurityAnswers()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());

        ref.child("SecurityQuestions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
            if(datasnapshot.exists())
                {
                    String SafeAnswerOne = datasnapshot.child("SecurityAnswerOne").getValue().toString();
                    String SafeAnswerTwo = datasnapshot.child("SecurityAnswerTwo").getValue().toString();
                    SecurityQuestionsOne.setText(SafeAnswerOne);
                    SecurityQuestionsTwo.setText(SafeAnswerTwo);
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
           Toast.makeText(this, "Please enter the mobile number", Toast.LENGTH_SHORT).show();
            }else if(SecurityAnswerOne.equals("")){
                Toast.makeText(this, "Please enter first security answers to proceed", Toast.LENGTH_SHORT).show();
            }else if(SecurityAnswerTwo.equals("")){
                Toast.makeText(this, "Please enter second security answers to proceed", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(ResetPasswordActivity.this, "First security questions answer is wrong", Toast.LENGTH_SHORT).show();
                            }else if(!SafeAnswerTwo.equals(SecurityAnswerTwo)){
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
                                        }else if(!finalNewPassword.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[~!@#$%^&*-+=()])[^\\s]{8,20}$")){
                                            Toast.makeText(ResetPasswordActivity.this, "Please enter the valid password. Password Hint: Password required combined with following all one digit/lower/upper/special character with maximum 8 to 20 characters", Toast.LENGTH_SHORT).show();
                                        } else{
                                            ref.child("password").setValue(finalNewPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                                        startActivity(intent);
                                                        Toast.makeText(ResetPasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
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
                                    }
                                });

                                builder.show();
                            }

                        } else{
                            Toast.makeText(ResetPasswordActivity.this, "You have not set the security questions", Toast.LENGTH_SHORT).show();
                        }

                    }else {

                        Toast.makeText(ResetPasswordActivity.this, "This phone number not exist, try again.", Toast.LENGTH_SHORT).show();
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