package com.alok.business.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.alok.business.Buyer.ProductsDetailActivity;
import com.alok.business.Interfaces.ItemClicklistener;
import com.alok.business.R;
import com.alok.business.models.Products;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyRecyclerViewAdapterProductSearch extends RecyclerView.Adapter<MyRecyclerViewAdapterProductSearch.ProductViewHolder> {

    Context context;
    ArrayList<Products> profiles;

    public MyRecyclerViewAdapterProductSearch(Context c , ArrayList<Products> p)
    {
        context = c;
        profiles = p;
    }
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(context).inflate(R.layout.products_item_layout,parent,false));
    }



    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {





        holder.txtProductName.setText(profiles.get(position).getPname());
        holder.txtProductDescription.setText(profiles.get(position).getDescription());
        if(profiles.get(position).getProductState().equals("Approved")){
            holder.txtProductPrice.setText(" " + profiles.get(position).getPrice() );}
        else {
            holder.txtProductPrice.setText("COMING SOON !");
        }
        Glide.with(holder.imageView).load(profiles.get(position).getImage()).into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   if(profiles.get(position).getProductState().equals("Not Approved")){
                    CharSequence options[] = new CharSequence[]
                            {
                                    "ok"
                            };
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(("COMING SOON !"));

                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(which==0)
                            {
                                dialog.cancel();
                            }
                        }
                    });
                    builder.show();
                }
                else {
                    Intent intent = new Intent(context, ProductsDetailActivity.class);
                    intent.putExtra("pid", profiles.get(position).getPid());
                    context.startActivity(intent);
                }*/
            }
        });






    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public interface ItemClickListener {
    }

    class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView txtProductName,txtProductDescription,txtProductPrice,txtProductCategory;
        public ImageView imageView;
        public ItemClicklistener listener;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=(ImageView) itemView.findViewById(R.id.product_image);
            txtProductDescription=(TextView)itemView.findViewById(R.id.product_Description);
            txtProductName=(TextView)itemView.findViewById(R.id.product_name);
            txtProductPrice=(TextView)itemView.findViewById(R.id.product_Price);
            txtProductCategory = (TextView)itemView.findViewById(R.id.product_category);
        }

        public void setItemClickListener(ItemClicklistener listener){
            this.listener=listener;
        }
        @Override
        public void onClick(View view) {
            listener.onClick(view,getAdapterPosition(),false);
        }
    }
}
