package com.dhruva.shopping;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.dhruva.shopping.Model.Products;
import com.dhruva.shopping.Prevalent.Prevalent;
import com.dhruva.shopping.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DatabaseReference ProductsRef;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView textView;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_home);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        toolbar=findViewById(R.id.toolbar);
        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);
        userNameTextView.setText(Prevalent.currentOnlineUser.getName());
        Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);
        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Step_name", "View cart items");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "View cart items");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_CART, bundle);
                Intent intent = new Intent(HomeActivity.this,CartActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
            .setQuery(ProductsRef, Products.class)
            .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
            new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model)
                {
                    holder.txtProductName.setText(model.getPname());
                    holder.txtProductDescription.setText(model.getDescription());
                    holder.txtProductPrice.setText("Price: Rs" + model.getPrice());
                    Picasso.get().load(model.getImage()).into(holder.imageView);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("Step_name", "Product details view");
                            Bundle bundle = new Bundle();
                            bundle.putString("Product_Name", String.valueOf(model.getPname()));
                            bundle.putString("Product_Description", String.valueOf(model.getDescription()));
                            bundle.putString("Product_Price", String.valueOf(model.getPrice()));
                            bundle.putString(FirebaseAnalytics.Param.METHOD, "Product details view");
                            mFirebaseAnalytics.logEvent("Product_Detail_View", bundle);
                            Intent intent =new Intent(HomeActivity.this,ProductDetailsActivity.class);
                            intent.putExtra("pid",model.getPid());
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
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            Log.d("Step_name", "Previous Activity Opened");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Button: Back, Previous Activity Opened");
            mFirebaseAnalytics.logEvent("Back_Previous_Activity", bundle);
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            Log.d("Step_name", "Back button pressed");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Back button pressed Start");
            mFirebaseAnalytics.logEvent("Back_Button_Pressed", bundle);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.nav_cart) {
            Log.d("Step_name", "Action bar Navigating to Cart view");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Action bar Navigating to Cart view");
            mFirebaseAnalytics.logEvent("ActionBar_NavigationTo_View_Cart", bundle);
            Intent intent = new Intent(HomeActivity.this,CartActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_search) {
            Log.d("Step_name", "Action bar Navigation to Items Search");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Action bar Navigation to Items Search");
            mFirebaseAnalytics.logEvent("ActionBar_NavigationTo_Search", bundle);
            Intent intent = new Intent(HomeActivity.this,SearchProductsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_categories) {
            Log.d("Step_name", "Action bar Navigation to Categories");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Action bar Navigation to Categories");
            mFirebaseAnalytics.logEvent("ActionBar_NavigationTo_Categories", bundle);
            Toast.makeText(HomeActivity.this,"Categories Clicked.",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_settings) {
            Log.d("Step_name", "Action bar Navigation to Settings");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Action bar Navigation to Settings");
            mFirebaseAnalytics.logEvent("ActionBar_NavigationTo_Settings", bundle);
            Intent intent=new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            Log.d("Step_name", "Action bar Navigation to Logout Screen (Main)");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Action bar Navigation to Logout (Main) Screen");
            mFirebaseAnalytics.logEvent("ActionBar_NavigationTo_Logout", bundle);
            Paper.book().destroy();
            Intent intent=new Intent(HomeActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity(intent);
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_cart) {
            Log.d("Step_name", "Menu Navigating to Cart view");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Menu bar Navigating to Cart view");
            mFirebaseAnalytics.logEvent("Menu_NavigationTo_View_Cart", bundle);
            Intent intent = new Intent(HomeActivity.this,CartActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_search) {
            Log.d("Step_name", "Menu Navigation to Items Search");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Menu Navigation to Items Search");
            mFirebaseAnalytics.logEvent("Menu_NavigationTo_Search", bundle);
            Intent intent = new Intent(HomeActivity.this,SearchProductsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_categories) {
            Log.d("Step_name", "Menu Navigation to Categories");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Menu Navigation to Categories");
            mFirebaseAnalytics.logEvent("Menu_NavigationTo_Categories", bundle);
            Toast.makeText(HomeActivity.this,"Categories Clicked.",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_settings) {
            Log.d("Step_name", "Menu Navigation to Settings");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Menu Navigation to Settings");
            mFirebaseAnalytics.logEvent("Menu_NavigationTo_Setting", bundle);
            Intent intent=new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            Log.d("Step_name", "Menu Navigation to Logout Screen (Main)");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Menu Navigation to Logout (Main) Screen");
            mFirebaseAnalytics.logEvent("Menu_NavigationTo_Logout", bundle);
            Paper.book().destroy();
            Intent intent=new Intent(HomeActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity(intent);
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}