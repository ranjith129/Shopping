package com.dhruva.shopping;
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
import com.dhruva.shopping.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

public class ConfirmFinalOrderActivity extends AppCompatActivity {
    private EditText nameEditText,phoneEditText,addressEditText,cityEditText;
    private Button confirmOrderBtn;
    private String totalAmount = "";
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_confirm_final_order);
        totalAmount = getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Price: Rs"+totalAmount,Toast.LENGTH_SHORT).show();
        confirmOrderBtn = (Button) findViewById(R.id.confirm_final_order_btn);
        nameEditText =(EditText) findViewById(R.id.shipment_name);
        phoneEditText =(EditText) findViewById(R.id.shipment_phone_number);
        addressEditText =(EditText) findViewById(R.id.shipment_address);
        cityEditText =(EditText) findViewById(R.id.shipment_city);
        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Step_name", "Order confirmation checking");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Message: Order confirmation checking");
                mFirebaseAnalytics.logEvent("OrderConfirmation_Checks", bundle);
                HashMap cData = new HashMap<String, String>();
                cData.put("cd.OrderStatus", "Order confirmation checking");
                cData.put("cd.screenName", "ConfirmFinalOrderScreen");
                MobileCore.trackState("ConfirmFinalOrderScreen", cData);
                Check();
            }
        });
    }

    private void Check() {
        if(TextUtils.isEmpty(nameEditText.getText().toString())){
            Log.d("Step_name", "Please Provide Your Full Name");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Please Provide Your Full Name");
            mFirebaseAnalytics.logEvent("Shipment_Name_Error", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.ShipmentValidError", "Please Provide Your Full Name");
            cData.put("cd.screenName", "ConfirmFinalOrderScreen");
            MobileCore.trackState("ConfirmFinalOrderScreen", cData);
            Toast.makeText(this,"Please Provide Your Full Name",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phoneEditText.getText().toString())){
            Log.d("Step_name", "Please Provide Your Phone Number");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Please Provide Your Phone Number");
            mFirebaseAnalytics.logEvent("Shipment_PhoneNumber_Error", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.ShipmentValidError", "Please Provide Your Phone Number");
            cData.put("cd.screenName", "ConfirmFinalOrderScreen");
            MobileCore.trackState("ConfirmFinalOrderScreen", cData);
            Toast.makeText(this,"Please Provide Your Phone Number",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(addressEditText.getText().toString())){
            Log.d("Step_name", "Please Provide Your Valid Address");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Please Provide Your Valid Address");
            mFirebaseAnalytics.logEvent("Shipment_Address_Error", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.ShipmentValidError", "Please Provide Your Valid Address");
            cData.put("cd.screenName", "ConfirmFinalOrderScreen");
            MobileCore.trackState("ConfirmFinalOrderScreen", cData);
            Toast.makeText(this,"Please Provide Your Valid Address.",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(cityEditText.getText().toString())){
            Log.d("Step_name", "Please Provide Your City Name");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Please Provide Your City Name");
            mFirebaseAnalytics.logEvent("Shipment_City_Error", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.ShipmentValidError", "Please Provide Your City Name");
            cData.put("cd.screenName", "ConfirmFinalOrderScreen");
            MobileCore.trackState("ConfirmFinalOrderScreen", cData);
            Toast.makeText(this,"Please Provide Your City Name",Toast.LENGTH_SHORT).show();
        }
        else {
            Log.d("Step_name", "Order confirmed");
            Bundle bundle = new Bundle();
            bundle.putString("Shipment_Total_Price", String.valueOf(totalAmount));
            bundle.putString("Shipment_Name", String.valueOf(nameEditText));
            bundle.putString("Shipment_PhoneNumber", String.valueOf(phoneEditText));
            bundle.putString("Shipment_Address", String.valueOf(addressEditText));
            bundle.putString("Shipment_City", String.valueOf(cityEditText));
            mFirebaseAnalytics.logEvent("Cart_OrderConfirmed", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.ShipmentTotalPrice", String.valueOf(totalAmount));
            cData.put("cd.ShipmentName", String.valueOf(nameEditText));
            cData.put("cd.ShipmentPhoneNumber", String.valueOf(phoneEditText));
            cData.put("cd.ShipmentAddress", String.valueOf(addressEditText));
            cData.put("cd.ShipmentCity", String.valueOf(cityEditText));
            cData.put("cd.OrderStatus", "Order confirmed");
            cData.put("cd.screenName", "ConfirmFinalOrderScreen");
            MobileCore.trackState("ConfirmFinalOrderScreen", cData);
            ConfirmOrder();
        }
    }

    private void ConfirmOrder() {
        final String saveCurrentTime,saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calForDate.getTime());
        final DatabaseReference ordersRef= FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());
        HashMap<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("totalAmount",totalAmount);
        ordersMap.put("name",nameEditText.getText().toString());
        ordersMap.put("phone",phoneEditText.getText().toString());
        ordersMap.put("address",addressEditText.getText().toString());
        ordersMap.put("city",cityEditText.getText().toString());
        ordersMap.put("date",saveCurrentDate);
        ordersMap.put("time",saveCurrentTime);
        ordersMap.put("state", "Not Shipped");
        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("Cart List").child("User view").child(Prevalent.currentOnlineUser.getPhone()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Log.d("Step_name", "Your final Order has been placed successfully");
                                        Bundle bundle = new Bundle();
                                        bundle.putString("Shipment_Total_Price", String.valueOf(totalAmount));
                                        bundle.putString("Shipment_Name", String.valueOf(nameEditText));
                                        bundle.putString("Shipment_PhoneNumber", String.valueOf(phoneEditText));
                                        bundle.putString("Shipment_Address", String.valueOf(addressEditText));
                                        bundle.putString("Shipment_City", String.valueOf(cityEditText));
                                        bundle.putString("Shipment_Date", saveCurrentDate);
                                        bundle.putString("Shipment_Time", saveCurrentTime);
                                        bundle.putString("Shipment_State", "Not Shipped");
                                        mFirebaseAnalytics.logEvent("Cart_OrderPlaced", bundle);
                                        HashMap cData = new HashMap<String, String>();
                                        cData.put("cd.ShipmentTotalPrice", String.valueOf(totalAmount));
                                        cData.put("cd.ShipmentName", String.valueOf(nameEditText));
                                        cData.put("cd.ShipmentPhoneNumber", String.valueOf(phoneEditText));
                                        cData.put("cd.ShipmentAddress", String.valueOf(addressEditText));
                                        cData.put("cd.ShipmentCity", String.valueOf(cityEditText));
                                        cData.put("cd.ShipmentDate", saveCurrentDate);
                                        cData.put("cd.ShipmentTime", saveCurrentTime);
                                        cData.put("cd.OrderStatus", "Order Placed and Not Shipped");
                                        cData.put("cd.screenName", "ConfirmFinalOrderScreen");
                                        MobileCore.trackState("ConfirmFinalOrderScreen", cData);
                                        Toast.makeText(ConfirmFinalOrderActivity.this,"Your final Order has been placed successfully.",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this,HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });
    }
}