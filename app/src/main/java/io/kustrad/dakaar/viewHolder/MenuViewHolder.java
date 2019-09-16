package io.kustrad.dakaar.viewHolder;

import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.kustrad.dakaar.Interface.ItemClickListener;
import io.kustrad.dakaar.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtMenuname;
    public ImageView imageView;
    private ItemClickListener itemClickListener;


    public MenuViewHolder(View itemView) {
        super(itemView);


        txtMenuname=(TextView)itemView.findViewById(R.id.menu_name);
        imageView=(ImageView)itemView.findViewById(R.id.menu_img);

        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}