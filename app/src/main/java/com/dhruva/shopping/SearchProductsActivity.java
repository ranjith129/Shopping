package com.dhruva.shopping;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dhruva.shopping.Model.Products;
import com.dhruva.shopping.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
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

public class SearchProductsActivity extends AppCompatActivity {
    private Button searchBtn;
    private EditText inputText;
    private RecyclerView searchList;
    private String searchInput;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_search_products);
        inputText = findViewById(R.id.search_product_name);
        searchBtn = findViewById(R.id.search_btn);
        searchList = findViewById(R.id.search_list);
        searchList.setLayoutManager(new LinearLayoutManager(SearchProductsActivity.this));
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchInput = inputText.getText().toString();
                Log.d("Step_name", "Searched given item name");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Button: Searched given item name");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
                HashMap cData = new HashMap<String, String>();
                cData.put("cd.SearchedProduct", searchInput);
                cData.put("cd.Searched", "Searched given item name");
                cData.put("cd.screenName", "SearchProductScreen");
                MobileCore.trackState("SearchProductScreen", cData);
                onStart();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(reference.orderByChild("pname").startAt(searchInput), Products.class)
                .build();
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {
                holder.txtProductName.setText(model.getPname());
                holder.txtProductDescription.setText(model.getDescription());
                holder.txtProductPrice.setText("Price: Rs" + model.getPrice());
                Picasso.get().load(model.getImage()).into(holder.imageView);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("Step_name", "Searched Product item Details view");
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.METHOD, "Searched Product item Details view");
                        bundle.putString("Product_Price", String.valueOf(model.getPrice()));
                        bundle.putString("Product_Name", String.valueOf(model.getPname()));
                        bundle.putString("Product_ID", String.valueOf(model.getDescription()));
                        bundle.putString("Product_ImageUrl", String.valueOf(model.getImage()));
                        mFirebaseAnalytics.logEvent("Searched_ProductDetails", bundle);
                        HashMap cData = new HashMap<String, String>();
                        cData.put("cd.ProductID", "Searched given item name");
                        cData.put("cd.ProductName", "Searched given item name");
                        cData.put("cd.ProductPrice", "Searched given item name");
                        cData.put("cd.ProductImageUrl", "Searched given item name");
                        cData.put("cd.SearchProduct", "Searched Product item Details view");
                        cData.put("cd.screenName", "SearchProductScreen");
                        MobileCore.trackState("SearchProductScreen", cData);
                        Intent intent =new Intent(SearchProductsActivity.this,ProductDetailsActivity.class);
                         intent.putExtra("pid", model.getPid());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        };
        searchList.setAdapter(adapter);
        adapter.startListening();
    }
}