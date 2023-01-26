package com.alok.business.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alok.business.Interfaces.ItemClicklistener;
import com.alok.business.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName,txtProductDescription,txtProductPrice,txtProductCategory,txtShopname,txtShopemail,txtShopphone;
    public ImageView imageView;
    public ItemClicklistener listener;
    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView=(ImageView) itemView.findViewById(R.id.product_image);
        txtProductDescription=(TextView)itemView.findViewById(R.id.product_Description);
        txtProductName=(TextView)itemView.findViewById(R.id.product_name);
        txtProductPrice=(TextView)itemView.findViewById(R.id.product_Price);
        txtProductCategory = (TextView)itemView.findViewById(R.id.product_category);
        txtShopname=(TextView)itemView.findViewById(R.id.shop_name);
        txtShopemail=(TextView)itemView.findViewById(R.id.shop_email);
        txtShopphone=(TextView)itemView.findViewById(R.id.shop_phone);



    }

    public void setItemClickListener(ItemClicklistener listener){
        this.listener=listener;
    }
    @Override
    public void onClick(View view) {
    listener.onClick(view,getAdapterPosition(),false);
    }
}
