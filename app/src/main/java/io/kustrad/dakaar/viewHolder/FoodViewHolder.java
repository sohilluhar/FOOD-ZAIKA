package io.kustrad.dakaar.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.kustrad.dakaar.Interface.ItemClickListener;
import io.kustrad.dakaar.R;

public class FoodViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

    public TextView food_name,food_price;
    public ImageView food_image,fav_img;
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FoodViewHolder(View itemView) {
        super(itemView);
        food_name=(TextView)itemView.findViewById(R.id.food_name);
        food_price=(TextView)itemView.findViewById(R.id.food_price);
        food_image=(ImageView)itemView.findViewById(R.id.food_image);
        fav_img=(ImageView)itemView.findViewById(R.id.fav);
        itemView.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}