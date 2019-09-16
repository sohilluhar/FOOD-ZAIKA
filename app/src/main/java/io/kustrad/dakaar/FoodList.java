package io.kustrad.dakaar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.kustrad.dakaar.Common.Common;
import io.kustrad.dakaar.Database.DataBase;
import io.kustrad.dakaar.Interface.ItemClickListener;
import io.kustrad.dakaar.Model.Food;
import io.kustrad.dakaar.viewHolder.FoodViewHolder;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    String categoryId=" ";
    FirebaseDatabase database;
    DatabaseReference foodList;
    FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;

   //Search Function
    FirebaseRecyclerAdapter<Food,FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;


    //Favorites
    DataBase localDB;






    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);


        //firebase
        database=FirebaseDatabase.getInstance();
        foodList=database.getReference("Foods");

        //Local DB
        localDB =new DataBase(this);

        recyclerView=(RecyclerView)findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //get intent here

        if (getIntent()!=null)
                categoryId=getIntent().getStringExtra("CategoryId");
        if (!categoryId.isEmpty() && categoryId!=null)
        {
            if(Common.isConnectedToInternet(getBaseContext()))
                loadListFood(categoryId);
            else
            {
                Toast.makeText(FoodList.this, "Please check your connection !!",Toast.LENGTH_SHORT).show();
                return;
            }




        }

        //Search

        materialSearchBar =  (MaterialSearchBar)findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter your food");
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // When user type thier text , we will change suggest list

            List<String> suggest = new ArrayList<String>();
            for(String search:suggestList) //loop in suggest List :D sorry my mistake
            {
                if(search.toLowerCase().contains( materialSearchBar.getText().toLowerCase()))
                    suggest.add(search);
            }
            materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //When search bar is close
                //Restore original adapter
                if(!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //When search finish
                //Show result of search adapter
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });


    }

    private void startSearch(CharSequence text) {

        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("name").equalTo(text.toString())
        ) {
            //foodList.orderByChild("Name").equalTo(text.toString()) // Compare name
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, final Food model, final int position) {

                viewHolder.food_name.setText(model.getName());
                viewHolder.food_price.setText(String.format("$ %s",model.getPrice().toString()));
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.food_image);






                final Food local=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongclick) {
                        // Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                        Intent foodDetail = new Intent(FoodList.this,FoodDetail.class);
                        foodDetail.putExtra("FoodId",searchAdapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });



            }
        };

        recyclerView.setAdapter(searchAdapter); // Set adapter for Recycler view is search result


    }

    private void loadSuggest() {
        foodList.orderByChild("menuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Food item=postSnapshot.getValue(Food.class);
                            suggestList.add(item.getName());  //Add name of food to sugggest list
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }




    private void loadListFood(String categoryId) {

        adapter= new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("menuId").equalTo(categoryId)//
                )
        {

            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, final Food model, final int position) {

            viewHolder.food_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.food_image);

                //Add Favorites
                if(localDB.isFavorites(adapter.getRef(position).getKey()))
                    viewHolder.fav_img.setImageResource(R.drawable.ic_favorite_black_24dp);

                //Click to change state of Favorites
                viewHolder.fav_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!localDB.isFavorites(adapter.getRef(position).getKey()))
                        {
                            localDB.addToFavorites(adapter.getRef(position).getKey());
                            viewHolder.fav_img.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this, ""+model.getName()+" was added to Favorites",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            localDB.removeFromFavorites(adapter.getRef(position).getKey());
                            viewHolder.fav_img.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(FoodList.this, ""+model.getName()+" was removed from Favorites",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            final Food local=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongclick) {
                       // Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                        Intent foodDetail = new Intent(FoodList.this,FoodDetail.class);
                        foodDetail.putExtra("FoodId",adapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });

            }
        };

        //set Adapter
        //Log.d("TAG",""+adapter.getItemCount());
        recyclerView.setAdapter(adapter);

    }
}
