package io.kustrad.dakaar;

import android.content.DialogInterface;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.RemoteMessage;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;
import io.kustrad.dakaar.Common.Common;
import io.kustrad.dakaar.Database.DataBase;
import io.kustrad.dakaar.Model.MyResponse;
import io.kustrad.dakaar.Model.Notification;
import io.kustrad.dakaar.Model.Order;
import io.kustrad.dakaar.Model.Request;
import io.kustrad.dakaar.Model.Sender;
import io.kustrad.dakaar.Model.Token;
import io.kustrad.dakaar.Remotes.APIService;
import io.kustrad.dakaar.viewHolder.CartAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView txtTotalPrice;
    Button btnPlace;

    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;

    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //init
        recyclerView = (RecyclerView) findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mService=Common.getFCMService();

        txtTotalPrice = (TextView) findViewById(R.id.total);
        btnPlace = (FButton)findViewById(R.id.btnPlaceOrder);
        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cart.size()>0)
                showAlertDialog();
                else
                    Toast.makeText(Cart.this, "Your Cart is empty!!", Toast.LENGTH_SHORT).show();
                    
            }
        });

        loadListFood();
    }

    private void showAlertDialog() {
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(Cart.this);
            alertDialog.setTitle("One more Step!");
            alertDialog.setMessage("Enter Your address:   ");

        LayoutInflater inflater=this.getLayoutInflater();
        View order_address_comment=inflater.inflate(R.layout.order_address_comment,null);
        final MaterialEditText address=(MaterialEditText)order_address_comment.findViewById(R.id.address);
        final MaterialEditText comment=(MaterialEditText)order_address_comment.findViewById(R.id.comment);


        //Add edit text to alert dialog
        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Request request=new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        address.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        "0",
                        comment.getText().toString(),
                        cart
                );

                //submit to firebase  System.currentMilli as key
                String ordernumber=String.valueOf(System.currentTimeMillis());
                requests.child(ordernumber)
                        .setValue(request);
                //Delete cart
                new DataBase(getBaseContext()).cleancart();

                sendNotificationOrder(ordernumber);

      //          Toast.makeText(Cart.this, "Thank You , Order Place ", Toast.LENGTH_SHORT).show();
        //        finish();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();

    }

    private void sendNotificationOrder(final String ordernumber) {
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        final Query data=tokens.orderByChild("serverToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){

                    Token serverToken=postSnapshot.getValue(Token.class);

                    //create raw payload
                    Notification notification =new Notification("Dakaar"," You have new Order "+ordernumber+" from "+Common.currentUser.getName());
                    Sender content =new Sender(serverToken.getToken(),notification);
                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                    ///only  run when get result
                                if (response.code()==200){
                                    if (response.body().success==1){
                                        Toast.makeText(Cart.this, "Thank You ,Your Order Place ", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(Cart.this, "Order failed ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("ERROR",t.getMessage());
                                }
                            });
                    ;


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadListFood() {
        cart = new DataBase(this).getCarts();
        adapter = new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //calculate total
        int total = 0;
        for (Order order:cart)
            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));

        Locale locale = new Locale("en", "IN");
        NumberFormat fmt =  NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return  true;
    }

    private void deleteCart(int position) {

        //We will remove item at List<Order> by position
        cart.remove(position);
        //After that , we will delete all old data from SQLite
        new DataBase(this).cleancart();
        //And final , we will update new data from list<order> to SQLite
        for(Order item:cart)
            new DataBase(this).addToCart(item);
        //Refresh
        loadListFood();
    }
}
