package com.dhruva.shopping;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dhruva.shopping.Model.Cart;
import com.dhruva.shopping.Prevalent.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class AdminUserProductsActivity extends AppCompatActivity {
    private RecyclerView productsList;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference cartListRef;
    private String userID = "";
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_admin_user_products);
        userID = getIntent().getStringExtra("uid");
        productsList = findViewById(R.id.products_list);
        productsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        productsList.setLayoutManager(layoutManager);
        cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin view").child(userID).child("Products");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Cart> options= new FirebaseRecyclerOptions.Builder<Cart>().setQuery(cartListRef,Cart.class).build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                holder.txtProductID.setText("Product ID: "+model.getPid());
                holder.txtProductOrderDate.setText("Order Date: "+model.getDate());
                holder.txtProductOrderTime.setText("Order Time: "+model.getTime());
                holder.txtProductName.setText("Product: "+model.getPname());
                holder.txtProductQuantity.setText("Quantity: "+model.getQuantity());
                holder.txtProductPrice.setText("Price: Rs"+model.getPrice());
                Log.d("Step_name", "User Product details View");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "User Product details View");
                bundle.putString(FirebaseAnalytics.Param.PRICE, String.valueOf(model.getPrice()));
                //bundle.putString("Total_Price", String.valueOf(model.getPrice()));
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(model.getPid()));
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, String.valueOf(model.getPname()));
                bundle.putString(FirebaseAnalytics.Param.QUANTITY, String.valueOf(model.getQuantity()));
                bundle.putString("Date", String.valueOf(model.getDate()));
                bundle.putString("Time", String.valueOf(model.getTime()));
                mFirebaseAnalytics.logEvent("User_Product_DetailsView", bundle);
                HashMap cData = new HashMap<String, String>();
                cData.put("cd.ProductID", String.valueOf(model.getPid()));
                cData.put("cd.ProductName", String.valueOf(model.getPname()));
                cData.put("cd.ProductPrice", String.valueOf(model.getPrice()));
                cData.put("cd.ProductQuantity", String.valueOf(model.getQuantity()));
                cData.put("cd.Date", String.valueOf(model.getDate()));
                cData.put("cd.Time", String.valueOf(model.getTime()));
                cData.put("cd.ProductDetailView", "User Product details View");
                cData.put("cd.screenName", "AdminUserProductScreen");
                MobileCore.trackState("AdminUserProductScreen", cData);
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };
        productsList.setAdapter(adapter);
        adapter.startListening();
    }
}