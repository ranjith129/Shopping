package com.dhruva.shopping;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dhruva.shopping.Model.Cart;
import com.dhruva.shopping.Prevalent.CartViewHolder;
import com.dhruva.shopping.Prevalent.Prevalent;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class CartActivity extends AppCompatActivity{
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button NextProcessBtn;
    private TextView txtTotalAmount, txtMsg1;
    private int overTotalPrice=0;private int TotalPrice=0;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_cart);
        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        NextProcessBtn = (Button)findViewById(R.id.next_btn);
        txtTotalAmount = (TextView)findViewById(R.id.total_price);
        txtMsg1 = (TextView)findViewById(R.id.msg1);

        NextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtTotalAmount.setText("Total Price: Rs"+String.valueOf(overTotalPrice));
                Log.d("Step_name", "Product Order Process to next Conformation level");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Product Order Process to next Conformation level");
                bundle.putString(FirebaseAnalytics.Param.PRICE, String.valueOf(overTotalPrice));
                mFirebaseAnalytics.logEvent("Product_Total_Price", bundle);
                HashMap cData = new HashMap<String, String>();
                cData.put("cd.ProductTotalPrice", String.valueOf(overTotalPrice));
                cData.put("cd.OrderStatus", "Product Order Process to next Conformation level");
                cData.put("cd.screenName", "CartActivityScreen");
                MobileCore.trackState("CartActivityScreen", cData);
                Intent intent = new Intent(CartActivity.this,ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price", String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderState();
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>().setQuery(cartListRef.child("User view").child(Prevalent.currentOnlineUser.getPhone()).child("Products"),Cart.class).build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                int FinalPrice = ((Integer.valueOf(model.getPrice())))* Integer.valueOf(model.getQuantity());
                holder.txtProductID.setText("Product ID: "+model.getPid());
                holder.txtProductName.setText("Product Name: "+model.getPname());
                holder.txtProductQuantity.setText("Quantity: "+model.getQuantity());
                holder.txtProductPrice.setText("Products Total Price: Rs"+FinalPrice);
                holder.txtProductOrderDate.setVisibility(View.INVISIBLE);
                holder.txtProductOrderTime.setVisibility(View.INVISIBLE);
                int oneTyprProductTPrice = ((Integer.valueOf(model.getPrice())))* Integer.valueOf(model.getQuantity());
                overTotalPrice = overTotalPrice + oneTyprProductTPrice;
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[] = new CharSequence[]{"Edit","Remove"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options: ");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i==0){
                                    Log.d("Step_name", "Cart item edit");
                                    Bundle bundle = new Bundle();
                                    bundle.putString(FirebaseAnalytics.Param.METHOD, "Cart item edit");
                                    bundle.putString(FirebaseAnalytics.Param.PRICE, String.valueOf(FinalPrice));
                                    bundle.putString("Total_Price", String.valueOf(overTotalPrice));
                                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(model.getPid()));
                                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, String.valueOf(model.getPname()));
                                    bundle.putString(FirebaseAnalytics.Param.QUANTITY, String.valueOf(model.getQuantity()));
                                    mFirebaseAnalytics.logEvent("Cart_Items_Edited", bundle);
                                    HashMap cData = new HashMap<String, String>();
                                    cData.put("cd.ProductID", String.valueOf(model.getPid()));
                                    cData.put("cd.ProductName", String.valueOf(model.getPname()));
                                    cData.put("cd.ProductPrice", String.valueOf(FinalPrice));
                                    cData.put("cd.ProductsTotalPrice", String.valueOf(overTotalPrice));
                                    cData.put("cd.ProductQuantity", String.valueOf(model.getQuantity()));
                                    cData.put("cd.CartItemStatus", "Cart Item edited");
                                    cData.put("cd.screenName", "CartActivityScreen");
                                    MobileCore.trackState("CartActivityScreen", cData);
                                    Intent intent = new Intent(CartActivity.this,ProductDetailsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }
                                if (i==1){
                                    cartListRef.child("User view").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(model.getPid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if (task.isSuccessful()){
                                                   Log.d("Step_name", "Cart Item removed");
                                                   Bundle bundle = new Bundle();
                                                   bundle.putString(FirebaseAnalytics.Param.METHOD, "Cart Item removed");
                                                   mFirebaseAnalytics.logEvent("Cart_Item_Removed", bundle);
                                                   HashMap cData = new HashMap<String, String>();
                                                   cData.put("cd.CartItemStatus", "Cart Item removed");
                                                   cData.put("cd.screenName", "CartActivityScreen");
                                                   MobileCore.trackState("CartActivityScreen", cData);
                                                   Toast.makeText(CartActivity.this,"Item removed Successfully.",Toast.LENGTH_SHORT).show();
                                                   Intent intent = new Intent(CartActivity.this,HomeActivity.class);
                                                   startActivity(intent);
                                               }
                                           }
                                       });
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
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
                    String userName = dataSnapshot.child("name").getValue().toString();
                    if (shippingState.equals("Shipped")){
                        txtTotalAmount.setText("Dear "+userName+"\n order is shipped successfully.");
                        Log.d("Step_name", "Product order is shipped");
                        recyclerView.setVisibility(View.GONE);
                        txtMsg1.setVisibility(View.VISIBLE);
                        txtMsg1.setText("Congratulations, Your Final order has been shipped successfully. Soon you will received your order at your door step.");
                        NextProcessBtn.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this,"You can purchase more products, Once you received your first order.",Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.METHOD, "Product order is shipped");
                        mFirebaseAnalytics.logEvent("Product_Shipped", bundle);
                        HashMap cData = new HashMap<String, String>();
                        cData.put("cd.OrderStatus", "Product order is shipped");
                        cData.put("cd.screenName", "CartActivityScreen");
                        MobileCore.trackState("CartActivityScreen", cData);
                    }
                    else if (shippingState.equals("Not Shipped")){
                        txtTotalAmount.setText("Shipping State = Not Shipped");
                        recyclerView.setVisibility(View.GONE);
                        txtMsg1.setVisibility(View.VISIBLE);
                        Log.d("Step_name", "Cart Item Not Shipped");
                        NextProcessBtn.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this,"You can purchase more products, Once you received your first order.",Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.METHOD, "Cart Item Not Shipped");
                        mFirebaseAnalytics.logEvent("Product_Items_NotShipped", bundle);
                        HashMap cData = new HashMap<String, String>();
                        cData.put("cd.OrderStatus", "Cart Item Not Shipped");
                        cData.put("cd.screenName", "CartActivityScreen");
                        MobileCore.trackState("CartActivityScreen", cData);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Step_name", "Cart Item Cancelled");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Cart Item Cancelled");
                mFirebaseAnalytics.logEvent("Cart_Items_Cancelled", bundle);
                HashMap cData = new HashMap<String, String>();
                cData.put("cd.CartItemStatus", "Cart Item Cancelled");
                cData.put("cd.screenName", "CartActivityScreen");
                MobileCore.trackState("CartActivityScreen", cData);
            }
        });
    }
}