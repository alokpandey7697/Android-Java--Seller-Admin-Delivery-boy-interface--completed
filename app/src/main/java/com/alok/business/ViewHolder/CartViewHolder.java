package com.alok.business.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alok.business.Interfaces.ItemClicklistener;
import com.alok.business.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
public TextView txtProductName,  txtProductPrice, TxtProductQuantity, TxtProductAddress;
private ItemClicklistener itemClicklistener;

public CartViewHolder(View itemView)
{
    super(itemView);
    txtProductName=itemView.findViewById(R.id.cart_product_name);
    txtProductPrice=itemView.findViewById(R.id.cart_product_price);
    TxtProductQuantity=itemView.findViewById(R.id.cart_product_quantity);
}
    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        itemClicklistener.onClick(v,getAdapterPosition(),false);
    }

    public void setItemClicklistener(ItemClicklistener itemClicklistener) {
        this.itemClicklistener = itemClicklistener;
    }
}
