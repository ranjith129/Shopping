package com.dhruva.shopping;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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

public class AdminAddNewProductActivity extends AppCompatActivity {
    private String CategoryName, Description, Price, Pname, saveCurrentDate, saveCurrentTime;
    private Button AddNewProductButton;
    private ImageView InputProductImage;
    private EditText InputProductName, InputProductDescription, InputProductPrice;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String productRandomKey, downloadImageUrl;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef;
    private ProgressDialog loadingBar;
    private String ProdCurrentDate;
    private String ProdCurrentTime;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setContentView(R.layout.activity_admin_add_new_product);
        CategoryName = getIntent().getExtras().get("category").toString();
        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("ProductImages");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        AddNewProductButton = (Button) findViewById(R.id.add_new_product);
        InputProductImage = (ImageView) findViewById(R.id.select_product_image);
        InputProductName = (EditText) findViewById(R.id.product_name);
        InputProductDescription = (EditText) findViewById(R.id.product_description);
        InputProductPrice = (EditText) findViewById(R.id.product_price);
        loadingBar = new ProgressDialog(this);
        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d("Step_name", "Input Product Images");
                //Log.v("Step_name", "Input Product Images - B");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Input Product Images");
                mFirebaseAnalytics.logEvent("Input_Product_Image", bundle);
                HashMap cData = new HashMap<String, String>();
                cData.put("cd.ProductImage", "Input Product Images");
                cData.put("cd.screenName", "AdminAddNewProductScreen");
                MobileCore.trackState("AdminAddNewProductScreen", cData);
                OpenGallery();
            }
        });

        AddNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Log.d("Step_name", "Add new Product");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Add new Product");
                mFirebaseAnalytics.logEvent("Add_New_Product", bundle);
                HashMap cData = new HashMap<String, String>();
                cData.put("cd.NewProductStatus", "Add new Product");
                cData.put("cd.screenName", "AdminAddNewProductScreen");
                MobileCore.trackState("AdminAddNewProductScreen", cData);
                ValidateProductData();
            }
        });
    }

    private void OpenGallery(){
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            ImageUri = data.getData();
            InputProductImage.setImageURI(ImageUri);
            Log.d("Step_name", "Set Product image");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Set Product image");
            mFirebaseAnalytics.logEvent("Set_Product_image", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.ProductImage", "Set Product image");
            cData.put("cd.screenName", "AdminAddNewProductScreen");
            MobileCore.trackState("AdminAddNewProductScreen", cData);
        }
    }

    private void ValidateProductData() {
        Description = InputProductDescription.getText().toString();
        Price = InputProductPrice.getText().toString();
        Pname = InputProductName.getText().toString();
        if (ImageUri == null)
        {
            Log.d("Step_name", "Product image is mandatory");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Product image is mandatory");
            mFirebaseAnalytics.logEvent("Product_Image_Error", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.ProductValidError", "Product image is mandatory");
            cData.put("cd.screenName", "AdminAddNewProductScreen");
            MobileCore.trackState("AdminAddNewProductScreen", cData);
            Toast.makeText(this, "Product image is mandatory. Kindly select any image.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Description))
        {
            Log.d("Step_name", "Please enter product description");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Please enter product description");
            mFirebaseAnalytics.logEvent("Product_Description_Error", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.ProductValidError", "Please enter product description");
            cData.put("cd.screenName", "AdminAddNewProductScreen");
            MobileCore.trackState("AdminAddNewProductScreen", cData);
            Toast.makeText(this, "Please enter product description.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Price))
        {
            Log.d("Step_name", "Please enter product Price");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Please enter product Price");
            mFirebaseAnalytics.logEvent("Product_Price_Error", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.ProductValidError", "Please enter product Price");
            cData.put("cd.screenName", "AdminAddNewProductScreen");
            MobileCore.trackState("AdminAddNewProductScreen", cData);
            Toast.makeText(this, "Please enter product Price.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Pname))
        {
            Log.d("Step_name", "Please enter product name");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Please enter product name");
            mFirebaseAnalytics.logEvent("Product_Name_Error", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.ProductValidError", "Please enter product name");
            cData.put("cd.screenName", "AdminAddNewProductScreen");
            MobileCore.trackState("AdminAddNewProductScreen", cData);
            Toast.makeText(this, "Please enter product name.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Log.d("Step_name", "Set Product all information");
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Message: Set Product all information");
            mFirebaseAnalytics.logEvent("Product_Information_Updated", bundle);
            HashMap cData = new HashMap<String, String>();
            cData.put("cd.ProductValidStatus", "Set Product all information");
            cData.put("cd.screenName", "AdminAddNewProductScreen");
            MobileCore.trackState("AdminAddNewProductScreen", cData);
            StoreProductInformation();
        }
    }

    private void StoreProductInformation()
    {
        loadingBar.setTitle("Add New Product");
        loadingBar.setMessage("Dear Admin, please wait while we are adding the new product.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        Log.d("Step_name", "Store Product all information");
        Pname = InputProductName.getText().toString().trim();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        currentDate = new SimpleDateFormat("ddMMyyyy");
        ProdCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentTime.format(calendar.getTime());
        currentTime = new SimpleDateFormat("HHmmss");
        ProdCurrentTime = currentTime.format(calendar.getTime());
        String productRandomKeyval = Pname + ProdCurrentDate + ProdCurrentTime;
        productRandomKey = productRandomKeyval.trim().replace(" ", "");

        final StorageReference filePath = ProductImagesRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Step_name", "Product Information Update error");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Error: Product Information Update error");
                mFirebaseAnalytics.logEvent("Product_InfoUpdate_Error", bundle);
                HashMap cData = new HashMap<String, String>();
                cData.put("cd.ProductValidError", "Product Information Update error");
                cData.put("cd.screenName", "AdminAddNewProductScreen");
                MobileCore.trackState("AdminAddNewProductScreen", cData);
                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("Step_name", "Product Image uploaded Successfully");
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.METHOD, "Message: Product Image uploaded Successfully");
                mFirebaseAnalytics.logEvent("Product_Image_Updated", bundle);
                HashMap cData = new HashMap<String, String>();
                cData.put("cd.ProductImageStatus", "Product Image uploaded Successfully.");
                cData.put("cd.screenName", "AdminAddNewProductScreen");
                Toast.makeText(AdminAddNewProductActivity.this, "Product Image uploaded Successfully.", Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }
                        Log.d("Step_name", "The Product image Update continued");
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.METHOD, "Message: The Product image Update continued");
                        mFirebaseAnalytics.logEvent("Product_ImageUpdate_Continued", bundle);
                        HashMap cData = new HashMap<String, String>();
                        cData.put("cd.ProductImageStatus", "The Product image Update continued");
                        cData.put("cd.screenName", "AdminAddNewProductScreen");
                        MobileCore.trackState("AdminAddNewProductScreen", cData);
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();
                            Log.d("Step_name", "Download the Product image Url Successfully");
                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Param.METHOD, "Message: Download the Product image Url Successfully");
                            mFirebaseAnalytics.logEvent("Download_ProductImageUrl_Succeed", bundle);
                            HashMap cData = new HashMap<String, String>();
                            cData.put("cd.ProductImageStatus", "Download the Product image Url Successfully");
                            cData.put("cd.screenName", "AdminAddNewProductScreen");
                            MobileCore.trackState("AdminAddNewProductScreen", cData);
                            Toast.makeText(AdminAddNewProductActivity.this, "Download the Product image Url Successfully.", Toast.LENGTH_SHORT).show();
                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }

    private void SaveProductInfoToDatabase()
    {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", Description);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", CategoryName);
        productMap.put("price", Price);
        productMap.put("pname", Pname);
        ProductsRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Log.d("Step_name", "New Product is added successfully");
                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Param.METHOD, "Message: New Product is added successfully");
                            mFirebaseAnalytics.logEvent("NewProduct_Added_Succeed", bundle);
                            HashMap cData = new HashMap<String, String>();
                            cData.put("cd.NewProductStatus", "New Product is added successfully");
                            cData.put("cd.screenName", "AdminAddNewProductScreen");
                            MobileCore.trackState("AdminAddNewProductScreen", cData);
                            Intent intent = new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class);
                            startActivity(intent);
                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this, "The new Product is added successfully.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Log.d("Step_name", "Add New Product is unsuccessful");
                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Param.METHOD, "Message: Add New Product is unsuccessful");
                            mFirebaseAnalytics.logEvent("Added_NewProduct_Failure", bundle);
                            HashMap cData = new HashMap<String, String>();
                            cData.put("cd.NewProductStatus", "Adding New Product is Failed");
                            cData.put("cd.screenName", "AdminAddNewProductScreen");
                            MobileCore.trackState("AdminAddNewProductScreen", cData);
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}