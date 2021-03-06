package com.dhruva.shopping;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import java.util.HashMap;

public class SplashScreenActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Log.d("Step_name", "App Splash Screen Started");
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, "App Splash Screen Started");
        mFirebaseAnalytics.logEvent("App_Started", bundle);
        MobileCore.setLogLevel(LoggingMode.DEBUG);
        HashMap cData = new HashMap<String, String>();
        cData.put("cd.AppStarted", "App Splash Screen Started");
        cData.put("cd.screenName", "FlashScreen");
        MobileCore.trackState("FlashScreen", cData);
        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        finish();
    }
}