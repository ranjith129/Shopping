package com.dhruva.shopping;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;

public class AdminCategoryActivity extends AppCompatActivity {
    private ImageView tShirts, sportsTShirts, femaleDresses, sweathers;
    private ImageView glasses, hatsCaps, walletsBagsPurses, shoes;
    private ImageView headPhonesHandFree, Laptops, watches, mobilePhones;
    private Button LogoutBtn, CheckOrdersBtn;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_admin_category);

        LogoutBtn = (Button) findViewById(R.id.admin_logout_btn);
        LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Step_name", "Admin logout Succeed");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Button: Navigation to Logout (Main) Screen");
                mFirebaseAnalytics.logEvent("Admin_Logout", bundle);
                Intent intent= new Intent(AdminCategoryActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        CheckOrdersBtn = (Button) findViewById(R.id.check_orders_btn);
        CheckOrdersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Step_name", "Check the orders");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Button: Check the orders");
                mFirebaseAnalytics.logEvent("Admin_Check_Orders", bundle);
                Intent intent= new Intent(AdminCategoryActivity.this,AdminNewOrdersActivity.class);
                startActivity(intent);
            }
        });

        tShirts = (ImageView) findViewById(R.id.t_shirts);
        sportsTShirts = (ImageView) findViewById(R.id.sports_t_shirts);
        femaleDresses = (ImageView) findViewById(R.id.female_dresses);
        sweathers = (ImageView) findViewById(R.id.sweathers);
        glasses = (ImageView) findViewById(R.id.glasses);
        hatsCaps = (ImageView) findViewById(R.id.hats_caps);
        walletsBagsPurses = (ImageView) findViewById(R.id.purses_bags_wallets);
        shoes = (ImageView) findViewById(R.id.shoes);
        headPhonesHandFree = (ImageView) findViewById(R.id.headphones_handfree);
        Laptops = (ImageView) findViewById(R.id.laptop_pc);
        watches = (ImageView) findViewById(R.id.watches);
        mobilePhones = (ImageView) findViewById(R.id.mobilephones);

        tShirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d("Step_name", "Product tShirts");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Button: Product TShirts");
                mFirebaseAnalytics.logEvent("Admin_Uploaded_TShirts", bundle);
                Intent intent = new Intent(AdminCategoryActivity.this, com.dhruva.shopping.AdminAddNewProductActivity.class);
                intent.putExtra("category", "tShirts");
                startActivity(intent);
            }
        });

        sportsTShirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d("Step_name", "Product Sports TShirts");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Button: Product sports TShirts");
                mFirebaseAnalytics.logEvent("Admin_Uploaded_SportsTShirts", bundle);
                Intent intent = new Intent(AdminCategoryActivity.this, com.dhruva.shopping.AdminAddNewProductActivity.class);
                intent.putExtra("category", "Sports tShirts");
                startActivity(intent);
            }
        });

        femaleDresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d("Step_name", "Product female Dresses");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Button: Product female Dresses");
                mFirebaseAnalytics.logEvent("Admin_Uploaded_FemaleDresses", bundle);
                Intent intent = new Intent(AdminCategoryActivity.this, com.dhruva.shopping.AdminAddNewProductActivity.class);
                intent.putExtra("category", "Dresses");
                startActivity(intent);
            }
        });

        sweathers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d("Step_name", "Product Sweathers");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Button: Product Sweathers");
                mFirebaseAnalytics.logEvent("Admin_Uploaded_Sweathers", bundle);
                Intent intent = new Intent(AdminCategoryActivity.this, com.dhruva.shopping.AdminAddNewProductActivity.class);
                intent.putExtra("category", "Sweathers");
                startActivity(intent);
            }
        });

        glasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d("Step_name", "Product Glasses");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Button: Product Glasses");
                mFirebaseAnalytics.logEvent("Admin_Uploaded_Glasses", bundle);
                Intent intent = new Intent(AdminCategoryActivity.this, com.dhruva.shopping.AdminAddNewProductActivity.class);
                intent.putExtra("category", "Glasses");
                startActivity(intent);
            }
        });

        hatsCaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d("Step_name", "Product Hats Caps - A");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Button: Product Hats Caps");
                mFirebaseAnalytics.logEvent("Admin_Uploaded_HatsCaps", bundle);
                Intent intent = new Intent(AdminCategoryActivity.this, com.dhruva.shopping.AdminAddNewProductActivity.class);
                intent.putExtra("category", "Hats Caps");
                startActivity(intent);
            }
        });

        walletsBagsPurses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d("Step_name", "Product Wallets Bags Purses");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Button: Product Wallets Bags Purses");
                mFirebaseAnalytics.logEvent("Admin_Uploaded_WalletsBagsPurses", bundle);
                Intent intent = new Intent(AdminCategoryActivity.this, com.dhruva.shopping.AdminAddNewProductActivity.class);
                intent.putExtra("category", "Wallets Bags Purses");
                startActivity(intent);
            }
        });

        shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d("Step_name", "Product Shoes");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Button: Product Shoes");
                mFirebaseAnalytics.logEvent("Admin_Uploaded_Shoes", bundle);
                Intent intent = new Intent(AdminCategoryActivity.this, com.dhruva.shopping.AdminAddNewProductActivity.class);
                intent.putExtra("category", "Shoes");
                startActivity(intent);
            }
        });

        headPhonesHandFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d("Step_name", "Product HeadPhones");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Button: Product HeadPhones");
                mFirebaseAnalytics.logEvent("Admin_Uploaded_HeadPhones", bundle);
                Intent intent = new Intent(AdminCategoryActivity.this, com.dhruva.shopping.AdminAddNewProductActivity.class);
                intent.putExtra("category", "HeadPhones");
                startActivity(intent);
            }
        });

        Laptops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d("Step_name", "Product Laptops");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Button: Product Laptops");
                mFirebaseAnalytics.logEvent("Admin_Uploaded_Laptops", bundle);
                Intent intent = new Intent(AdminCategoryActivity.this, com.dhruva.shopping.AdminAddNewProductActivity.class);
                intent.putExtra("category", "Laptops");
                startActivity(intent);
            }
        });

        watches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d("Step_name", "Product Watches");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Button: Product Watches");
                mFirebaseAnalytics.logEvent("Admin_Uploaded_Watches", bundle);
                Intent intent = new Intent(AdminCategoryActivity.this, com.dhruva.shopping.AdminAddNewProductActivity.class);
                intent.putExtra("category", "Watches");
                startActivity(intent);
            }
        });

        mobilePhones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d("Step_name", "Product Mobile Phones");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Button: Product Mobile Phones");
                mFirebaseAnalytics.logEvent("Admin_Uploaded_MobilePhones", bundle);
                Intent intent = new Intent(AdminCategoryActivity.this, com.dhruva.shopping.AdminAddNewProductActivity.class);
                intent.putExtra("category", "Mobile Phones");
                startActivity(intent);
            }
        });
    }
}