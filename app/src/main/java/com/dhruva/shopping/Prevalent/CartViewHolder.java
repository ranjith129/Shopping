package com.dhruva.shopping.Prevalent;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.dhruva.shopping.Interface.ItemClickListner;
import com.dhruva.shopping.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtProductName,txtProductPrice,txtProductQuantity,txtProductID,txtProductOrderDate,txtProductOrderTime,txtCustomerUniqueID;
    private ItemClickListner itemClickListner;

    public CartViewHolder(View itemView) {
        super(itemView);
        txtProductID = itemView.findViewById(R.id.cart_product_id);
        txtProductOrderDate = itemView.findViewById(R.id.cart_product_date);
        txtProductOrderTime = itemView.findViewById(R.id.cart_product_time);
        txtProductName = itemView.findViewById(R.id.cart_product_name);
        txtProductPrice = itemView.findViewById(R.id.cart_product_price);
        txtProductQuantity = itemView.findViewById(R.id.cart_product_quantity);
        //txtCustomerUniqueID = itemView.findViewById(R.id.cartCustomerUniqueID);
    }

    @Override
    public void onClick(View view) {
        itemClickListner.onClick(view,getAdapterPosition(),false);
    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }
}