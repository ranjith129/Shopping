package com.dhruva.shopping;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dhruva.shopping.Model.AdminOrders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AdminNewOrdersActivity extends AppCompatActivity {
    private RecyclerView ordersList;
    private DatabaseReference ordersRef;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_admin_new_orders);
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        ordersList = findViewById(R.id.orders_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        String saveCurrentTime,saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calForDate.getTime());
        FirebaseRecyclerOptions<AdminOrders> options= new FirebaseRecyclerOptions.Builder<AdminOrders>()
            .setQuery(ordersRef, AdminOrders.class)
            .build();
        FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder> adapter =
            new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {

                @Override
                protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final AdminOrders model) {
                    holder.userName.setText("Name: "+model.getName());
                    holder.userPhoneNumber.setText("Mobile: "+model.getPhone());
                    holder.userTotalPrice.setText("Total Amount: Rs"+model.getTotalAmount());
                    holder.userDateTime.setText("Order at: "+saveCurrentDate+":"+ saveCurrentTime);
                    holder.userShippingAddress.setText("Shipping Address: "+model.getAddress()+", "+model.getCity());
                    holder.showOrdersBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String uID = getRef(position).getKey();
                            Log.d("Step_name", "User order details view");
                            Bundle bundle = new Bundle();
                            bundle.putString("User_Name", String.valueOf(model.getName()));
                            bundle.putString("User_PhoneNumber", String.valueOf(model.getPhone()));
                            bundle.putString("User_Total_Price", String.valueOf(model.getTotalAmount()));
                            bundle.putString("User_Order_Date", String.valueOf(model.getDate()));
                            bundle.putString("User_Order_Time", String.valueOf(model.getTime()));
                            bundle.putString("User_Shipping_Address", String.valueOf(model.getAddress()));
                            bundle.putString("User_Shipping_City", String.valueOf(model.getCity()));
                            mFirebaseAnalytics.logEvent("User_Details", bundle);
                            Intent intent = new Intent(AdminNewOrdersActivity.this,AdminUserProductsActivity.class);
                            intent.putExtra("uid",uID);
                            startActivity(intent);
                        }
                    });

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CharSequence options[] =new CharSequence[]
                                    {"Yes","No"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                            builder.setTitle("Have you shipped this order products?");
                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i==0){
                                        Log.d("Step_name", "Update order status");
                                        Bundle bundle = new Bundle();
                                        bundle.putString("Order_Status", "Update order status");
                                        mFirebaseAnalytics.logEvent("Order_Status", bundle);
                                        String uID = getRef(position).getKey();
                                        RemoverOrder(uID);
                                    }
                                    else {
                                        Log.d("Step_name", "Order status not updated");
                                        Bundle bundle = new Bundle();
                                        bundle.putString("Order_Status", "Order status not updated");
                                        mFirebaseAnalytics.logEvent("Order_Status", bundle);
                                        finish();
                                    }
                                }
                            });
                            builder.show();
                        }
                    });
                }

                @NonNull
                @Override
                public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout,parent,false);
                    return new AdminOrdersViewHolder(view);
                }
            };
        ordersList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder{
        public TextView userName, userPhoneNumber,userTotalPrice,userDateTime,userShippingAddress;
        public Button showOrdersBtn;
        public AdminOrdersViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.order_user_name);
            userPhoneNumber = itemView.findViewById(R.id.order_phone_number);
            userTotalPrice = itemView.findViewById(R.id.order_total_price);
            userDateTime = itemView.findViewById(R.id.order_date_time);
            userShippingAddress = itemView.findViewById(R.id.order_address_city);
            showOrdersBtn = itemView.findViewById(R.id.show_all_product_btn);
        }
    }

    private void RemoverOrder(String uID) {
        Log.d("Step_name", "User Order Removed");
        Bundle bundle = new Bundle();
        bundle.putString("Order_Status", "User Order Removed");
        mFirebaseAnalytics.logEvent("Order_Status", bundle);
        ordersRef.child(uID).removeValue();
    }
}