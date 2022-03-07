package com.dhruva.shopping;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.dhruva.shopping.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import java.util.HashMap;
import de.hdodenhof.circleimageview.CircleImageView;
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

public class SettingsActivity extends AppCompatActivity {
    private CircleImageView profileImageView;
    private EditText fullNameEditText, userPhoneEditText, addressEditText;
    private TextView profileChangeTextBtn,  closeTextBtn, saveTextButton;
    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePrictureRef;
    private String checker = "";
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_settings);
        storageProfilePrictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");
        profileImageView = (CircleImageView) findViewById(R.id.settings_profile_image);
        fullNameEditText = (EditText) findViewById(R.id.settings_full_name);
        userPhoneEditText = (EditText) findViewById(R.id.settings_phone_number);
        addressEditText = (EditText) findViewById(R.id.settings_address);
        profileChangeTextBtn = (TextView) findViewById(R.id.profile_image_change_btn);
        closeTextBtn = (TextView) findViewById(R.id.close_settings_btn);
        saveTextButton = (TextView) findViewById(R.id.update_account_settings_btn);
        userInfoDisplay(profileImageView, fullNameEditText, userPhoneEditText, addressEditText);

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d("Step_name", "Setting changes closed");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Setting changes closed");
                bundle.putString("Profile_Image", String.valueOf(profileImageView));
                bundle.putString("Profile_FullName", String.valueOf(fullNameEditText));
                bundle.putString("Profile_PhoneNumber", String.valueOf(userPhoneEditText));
                bundle.putString("Profile_Address", String.valueOf(addressEditText));
                mFirebaseAnalytics.logEvent("ProfileUpdate_Closed", bundle);
                HashMap cData = new HashMap<String, String>();
                cData.put("cd.ProfileImage", String.valueOf(profileImageView));
                cData.put("cd.ProfileFullName", String.valueOf(fullNameEditText));
                cData.put("cd.ProfilePhoneNumber", String.valueOf(userPhoneEditText));
                cData.put("cd.ProfileAddress", String.valueOf(addressEditText));
                cData.put("cd.ProfileUpdateClosed", "Setting changes closed");
                MobileCore.trackState("SettingScreen", cData);
                finish();
            }
        });

        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (checker.equals("clicked"))
                {
                    Log.d("Step_name", "User information Saved");
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.METHOD, "User information Saved");
                    bundle.putString("Profile_Image", String.valueOf(profileImageView));
                    bundle.putString("Profile_FullName", String.valueOf(fullNameEditText));
                    bundle.putString("Profile_PhoneNumber", String.valueOf(userPhoneEditText));
                    bundle.putString("Profile_Address", String.valueOf(addressEditText));
                    mFirebaseAnalytics.logEvent("Profile_Saved", bundle);
                    HashMap cData = new HashMap<String, String>();
                    cData.put("cd.ProfileImage", String.valueOf(profileImageView));
                    cData.put("cd.ProfileFullName", String.valueOf(fullNameEditText));
                    cData.put("cd.ProfilePhoneNumber", String.valueOf(userPhoneEditText));
                    cData.put("cd.ProfileAddress", String.valueOf(addressEditText));
                    cData.put("cd.ProfileSaved", "User information Saved");
                    MobileCore.trackState("SettingScreen", cData);
                    userInfoSaved();
                }
                else
                {
                    Log.d("Step_name", "User information updated");
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.METHOD, "User information updated");
                    bundle.putString("Profile_Image", String.valueOf(profileImageView));
                    bundle.putString("Profile_FullName", String.valueOf(fullNameEditText));
                    bundle.putString("Profile_PhoneNumber", String.valueOf(userPhoneEditText));
                    bundle.putString("Profile_Address", String.valueOf(addressEditText));
                    mFirebaseAnalytics.logEvent("Profile_Updated", bundle);
                    HashMap cData = new HashMap<String, String>();
                    cData.put("cd.ProfileImage", String.valueOf(profileImageView));
                    cData.put("cd.ProfileFullName", String.valueOf(fullNameEditText));
                    cData.put("cd.ProfilePhoneNumber", String.valueOf(userPhoneEditText));
                    cData.put("cd.ProfileAddress", String.valueOf(addressEditText));
                    cData.put("cd.ProfileUpdated", "User information updated");
                    MobileCore.trackState("SettingScreen", cData);
                    updateOnlyUserInfo();
                }
            }
        });

        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d("Step_name", "Profile Image Changes updated");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Profile Image Changes updated");
                bundle.putString("Profile_Image", String.valueOf(profileImageView));
                bundle.putString("Profile_FullName", String.valueOf(fullNameEditText));
                bundle.putString("Profile_PhoneNumber", String.valueOf(userPhoneEditText));
                bundle.putString("Profile_Address", String.valueOf(addressEditText));
                mFirebaseAnalytics.logEvent("Profile_ImageChanges_Updated", bundle);
                HashMap cData = new HashMap<String, String>();
                cData.put("cd.ProfileImage", String.valueOf(profileImageView));
                cData.put("cd.ProfileFullName", String.valueOf(fullNameEditText));
                cData.put("cd.ProfilePhoneNumber", String.valueOf(userPhoneEditText));
                cData.put("cd.ProfileAddress", String.valueOf(addressEditText));
                cData.put("cd.ProfileImageChangesUpdated", "Profile Image Changes updated");
                MobileCore.trackState("SettingScreen", cData);
                checker = "clicked";
                CropImage.activity(imageUri).setAspectRatio(1, 1).start(SettingsActivity.this);
            }
        });
    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> userMap = new HashMap<>();
        userMap. put("name", fullNameEditText.getText().toString());
        userMap. put("address", addressEditText.getText().toString());
        userMap. put("phoneOrder", userPhoneEditText.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);
        Log.d("Step_name", "Profile Information updated - A");
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, "User Information updated");
        bundle.putString("User_FullName", String.valueOf(fullNameEditText));
        bundle.putString("User_PhoneNumber", String.valueOf(userPhoneEditText));
        bundle.putString("User_Address", String.valueOf(addressEditText));
        mFirebaseAnalytics.logEvent("UserProfile_Information_Updated", bundle);
        HashMap cData = new HashMap<String, String>();
        cData.put("cd.UserFullName", String.valueOf(fullNameEditText));
        cData.put("cd.UserPhoneNumber", String.valueOf(userPhoneEditText));
        cData.put("cd.UserAddress", String.valueOf(addressEditText));
        cData.put("cd.UserProfileInformationUpdated", "User Information updated successfully");
        MobileCore.trackState("SettingScreen", cData);
        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            profileImageView.setImageURI(imageUri);
            Log.d("Step_name", "Profile image view done");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Profile image view done");
            mFirebaseAnalytics.logEvent("Profile_ImageView", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.ProfileImageView", "Profile image view done");
            MobileCore.trackState("SettingScreen", cData);
            startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        }
        else
        {
            Log.d("Step_name", "Profile image update failure, try some other time");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Profile image update failure");
            mFirebaseAnalytics.logEvent("ProfileImage_UpdateFailure", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.ProfileImageUpdateFailure", "Profile image update failure");
            MobileCore.trackState("SettingScreen", cData);
            Toast.makeText(this, "Error, Try Again after some time.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }
    }

    private void userInfoSaved()
    {
        if (TextUtils.isEmpty(fullNameEditText.getText().toString()))
        {
            Log.d("Step_name", "Name is mandatory");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Profile Name is mandatory");
            mFirebaseAnalytics.logEvent("Profile_Name_Error", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.ProfileNameError", "Profile Name is mandatory");
            MobileCore.trackState("SettingScreen", cData);
            Toast.makeText(this, "Name is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(addressEditText.getText().toString()))
        {
            Log.d("Step_name", "Address is mandatory");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Profile Address is mandatory");
            mFirebaseAnalytics.logEvent("Profile_Address_Error", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.ProfileAddressError", "Profile Address is mandatory");
            MobileCore.trackState("SettingScreen", cData);
            Toast.makeText(this, "Address is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(userPhoneEditText.getText().toString()))
        {
            Log.d("Step_name", "Phone Number is mandatory");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Profile Phone Number is mandatory");
            mFirebaseAnalytics.logEvent("Profile_PhoneNumber_Error", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.ProfilePhoneNumberError", "Profile Phone Number is mandatory");
            MobileCore.trackState("SettingScreen", cData);
            Toast.makeText(this, "Phone Number is mandatory.", Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("clicked"))
        {
            Log.d("Step_name", "User Information updated Succeed");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "User Information updated Succeed");
            mFirebaseAnalytics.logEvent("Profile_Updated_Error", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.ProfileUpdatedError", "User Information updated Succeed");
            MobileCore.trackState("SettingScreen", cData);
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (imageUri != null)
        {
            final StorageReference fileRef = storageProfilePrictureRef.child(Prevalent.currentOnlineUser.getPhone() + ".jpg");
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    Log.d("Step_name", "Profile Information image update");
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.METHOD, "Profile Information image update");
                    mFirebaseAnalytics.logEvent("Profile_ImageUpdate", bundle);
                    HashMap cData = new HashMap<String, String>();
                    cData.put("cd.ProfileImageUpdate", "Profile Information image update");
                    MobileCore.trackState("SettingScreen", cData);
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("name", fullNameEditText.getText().toString());
                        userMap.put("address", addressEditText.getText().toString());
                        userMap.put("phoneOrder", userPhoneEditText.getText().toString());
                        userMap.put("image", myUrl);
                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);
                        progressDialog.dismiss();
                        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                        Log.d("Step_name", "Profile Info updated successfully");
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.METHOD, "User Information updated");
                        bundle.putString("User_FullName", String.valueOf(fullNameEditText));
                        bundle.putString("User_PhoneNumber", String.valueOf(userPhoneEditText));
                        bundle.putString("User_Address", String.valueOf(addressEditText));
                        bundle.putString("User_ImageUrl", String.valueOf(myUrl));
                        mFirebaseAnalytics.logEvent("Profile_Updated_Succeed", bundle);
                        HashMap cData = new HashMap<String, String>();
                        cData.put("cd.UserFullName", String.valueOf(fullNameEditText));
                        cData.put("cd.UserPhoneNumber", String.valueOf(userPhoneEditText));
                        cData.put("cd.UserAddress", String.valueOf(addressEditText));
                        cData.put("cd.UserImageUrl", String.valueOf(myUrl));
                        cData.put("cd.ProfileUpdatedSucceed", "Profile Info update successfully");
                        MobileCore.trackState("SettingScreen", cData);
                        Toast.makeText(SettingsActivity.this, "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        progressDialog.dismiss();
                        Log.d("Step_name", "Profile Information update failure");
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.METHOD, "Profile Information updated failure");
                        mFirebaseAnalytics.logEvent("Profile_Updated_Failure", bundle);
                        HashMap cData = new HashMap<String, String>();
                        cData.put("cd.ProfileUpdatedFailure", "Profile Information updated failure");
                        MobileCore.trackState("SettingScreen", cData);
                        Toast.makeText(SettingsActivity.this, "Error. Try some other time later", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            Log.d("Step_name", "Profile Image is not selected");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Profile Image is not selected");
            mFirebaseAnalytics.logEvent("ProfileImage_NotSelected", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.ProfileImageNotSelected", "Profile Image is not selected");
            MobileCore.trackState("SettingScreen", cData);
            Toast.makeText(this, "Image is not selected.", Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText addressEditText)
    {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.child("image").exists())
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();
                        Picasso.get().load(image).into(profileImageView);
                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);
                        addressEditText.setText(address);
                        Log.d("Step_name", "Profile Image viewing");
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.METHOD, "User Information viewing");
                        bundle.putString("User_FullName", String.valueOf(fullNameEditText));
                        bundle.putString("User_PhoneNumber", String.valueOf(userPhoneEditText));
                        bundle.putString("User_Address", String.valueOf(addressEditText));
                        bundle.putString("User_ImageUrl", String.valueOf(imageUri));
                        mFirebaseAnalytics.logEvent("Profile_Displayed", bundle);
                        HashMap cData = new HashMap<String, String>();
                        cData.put("cd.UserFullName", String.valueOf(fullNameEditText));
                        cData.put("cd.UserPhoneNumber", String.valueOf(userPhoneEditText));
                        cData.put("cd.UserAddress", String.valueOf(addressEditText));
                        cData.put("cd.UserImageUrl", String.valueOf(imageUri));
                        cData.put("cd.ProfileDisplayed", "User Profile Information viewing");
                        MobileCore.trackState("SettingScreen", cData);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Step_name", "User Profile updated cancelled - A");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Profile updated cancelled");
                mFirebaseAnalytics.logEvent("ProfileUpdated_Cancelled", bundle);
                HashMap cData = new HashMap<String, String>();
                cData.put("cd.ProfileUpdatedCancelled", "Profile updated cancelled");
                MobileCore.trackState("SettingScreen", cData);
            }
        });
    }
}