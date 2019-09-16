package io.kustrad.dakaar.viewHolder;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.kustrad.dakaar.Common.Common;
import io.kustrad.dakaar.Interface.ItemClickListener;
import io.kustrad.dakaar.Model.Order;
import io.kustrad.dakaar.R;

class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
,View.OnCreateContextMenuListener
{

    public TextView txt_cart_name, txt_price;
    public ImageView img_cart_count;

    private ItemClickListener itemClickListener;

    public void setTxt_cart_name(TextView txt_cart_name) {
        this.txt_cart_name = txt_cart_name;
    }

    public CardViewHolder(View itemView) {
        super(itemView);

        txt_cart_name = (TextView) itemView.findViewById(R.id.cart_item_name);
        txt_price = (TextView)itemView.findViewById(R.id.cart_item_price);
        img_cart_count = (ImageView)itemView.findViewById(R.id.cart_item_count);

        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        contextMenu.setHeaderTitle("Select action");
        contextMenu.add(0,0,getAdapterPosition(), Common.DELETE);
    }
}



public class CartAdapter extends RecyclerView.Adapter<CardViewHolder> {


    private List<Order> listData = new ArrayList<>();
    private Context context;

    public CartAdapter(List<Order> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.activity_cart_layout, parent,false);
        return new CardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(""+ listData.get(position).getQuantity(), Color.RED);
        holder.img_cart_count.setImageDrawable(drawable);

        Locale locale = new Locale("en", "IN");
        NumberFormat fmt =  NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(listData.get(position).getPrice())) * (Integer.parseInt(listData.get(position).getQuantity()));
        holder.txt_price.setText(fmt.format(price));

        holder.txt_cart_name.setText(listData.get(position).getProductName());

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}