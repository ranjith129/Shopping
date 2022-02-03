package com.dhruva.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.dhruva.shopping.Model.Products;
import com.dhruva.shopping.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {
    private Button addToCartButton;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productPrice,productDescription,productName;
    private String productID="", state = "Normal";
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_product_details);
        productID = getIntent().getStringExtra("pid");
        addToCartButton =(Button) findViewById(R.id.pd_add_to_cart_button);
        numberButton = (ElegantNumberButton) findViewById(R.id.number_btn);
        productImage = (ImageView) findViewById(R.id.product_image_details);
        productName = (TextView) findViewById(R.id.product_name_details);
        productDescription = (TextView) findViewById(R.id.product_description_details);
        productPrice = (TextView) findViewById(R.id.product_price_details);
        getProductDetails(productID);
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (state.equals("Order Placed") || state.equals("Order Shipped")){
                    Log.d("Step_name", "Already Order done");
                    Bundle bundle = new Bundle();
                    bundle.putString("Order_State", "Order Placed/Shipped");
                    mFirebaseAnalytics.logEvent("Order_State", bundle);
                    Toast.makeText(ProductDetailsActivity.this,"You can add Purchase more product, once your order is shipped or confirmed.",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Log.d("Step_name", "Item added into cart");
                    Bundle bundle = new Bundle();
                    bundle.putString("AddToCart", "Item added into cart");
                    mFirebaseAnalytics.logEvent("AddToCart", bundle);
                    addingToCartList();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderState();
    }

    private void addingToCartList() {
        String saveCurrentTime,saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calForDate.getTime());
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        final HashMap<String, Object>cartMap = new HashMap<>();
        cartMap.put("pid",productID);
        cartMap.put("pname",productName.getText().toString());
        cartMap.put("price",productPrice.getText().toString());
        cartMap.put("date",saveCurrentDate);
        cartMap.put("time",saveCurrentTime);
        cartMap.put("quantity",numberButton.getNumber());
        cartMap.put("discount","");

        cartListRef.child("User view").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(productID).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    cartListRef.child("Admin view").child(Prevalent.currentOnlineUser.getPhone())
                        .child("Products").child(productID)
                        .updateChildren(cartMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Log.d("Step_name", "Item Added to cart List");
                                    Bundle bundle = new Bundle();
                                    bundle.putString("Product_Price", String.valueOf(productPrice));
                                    bundle.putString("Product_Name", String.valueOf(productName));
                                    bundle.putString("Product_ID", String.valueOf(productID));
                                    bundle.putString("Quantity", String.valueOf(numberButton));
                                    bundle.putString("Date", saveCurrentDate);
                                    bundle.putString("Time", saveCurrentTime);
                                    mFirebaseAnalytics.logEvent("AddToCart_ItemsList", bundle);
                                    Toast.makeText(ProductDetailsActivity.this,"Added to cart List",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ProductDetailsActivity.this,HomeActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
                }
            }
        });

    }

    private void getProductDetails(String productID) {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Products products=dataSnapshot.getValue(Products.class);
                    productName.setText("Product Name: "+ products.getPname());
                    productPrice.setText("Product Price: Rs"+ products.getPrice());
                    productDescription.setText("Description: "+ products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImage);
                    Log.d("Step_name", "Getting product details from DB");
                    Bundle bundle = new Bundle();
                    bundle.putString("Product_Price", String.valueOf(productPrice));
                    bundle.putString("Product_Name", String.valueOf(productName));
                    bundle.putString("Product_ID", String.valueOf(productDescription));
                    bundle.putString("Product_ImageUrl", String.valueOf(productImage));
                    mFirebaseAnalytics.logEvent("Get_Product_Details", bundle);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Step_name", "Getting product details from DB Cancelled");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD,"Getting product details from DB Cancelled");
                mFirebaseAnalytics.logEvent("Get_ProductDetails_Cancelled", bundle);
            }
        });
    }

    private void CheckOrderState()
    {
        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    if (shippingState.equals("Shipped")){
                        state ="Order Shipped";
                        Log.d("Step_name", "Product Order Shipped");
                        Bundle bundle = new Bundle();
                        bundle.putString("Checking_Order_State", "Product Order Shipped");
                        mFirebaseAnalytics.logEvent("Check_OrderState", bundle);
                    }
                    else if (shippingState.equals("Not Shipped")){
                        state ="Order Placed";
                        Log.d("Step_name", "Product Order Placed");
                        Bundle bundle = new Bundle();
                        bundle.putString("Checking_Order_State", "Product Order Placed");
                        mFirebaseAnalytics.logEvent("Check_OrderState", bundle);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Step_name", "Product Order Cancelled");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD,"Product Order Cancelled");
                mFirebaseAnalytics.logEvent("Check_ProductOrder_Cancelled", bundle);
            }
        });
    }
}