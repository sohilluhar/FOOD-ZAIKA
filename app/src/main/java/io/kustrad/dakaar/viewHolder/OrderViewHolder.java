package io.kustrad.dakaar.viewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.TextView;

import io.kustrad.dakaar.Interface.ItemClickListener;
import io.kustrad.dakaar.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public TextView txtOrderId,txtOrderStatus,txtOrderPhone,txtOrderAdress;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(View itemView) {
        super(itemView);

        txtOrderAdress=(TextView)itemView.findViewById(R.id.order_address);
        txtOrderId=(TextView)itemView.findViewById(R.id.order_id);
        txtOrderPhone=(TextView)itemView.findViewById(R.id.order_phone);
        txtOrderStatus=(TextView)itemView.findViewById(R.id.order_status);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

        //itemClickListener.onClick(v,getAdapterPosition(),false);

    }
}