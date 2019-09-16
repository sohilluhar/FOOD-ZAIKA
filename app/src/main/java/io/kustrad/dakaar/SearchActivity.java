package io.kustrad.dakaar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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

import io.kustrad.dakaar.Database.DataBase;
import io.kustrad.dakaar.Interface.ItemClickListener;
import io.kustrad.dakaar.Model.Food;
import io.kustrad.dakaar.viewHolder.FoodViewHolder;

public class SearchActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    String categoryId=" ";
    FirebaseDatabase database;
    DatabaseReference foodList;
    FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;

    FirebaseRecyclerAdapter<Food,FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    DataBase localDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        database=FirebaseDatabase.getInstance();
        foodList=database.getReference("Foods");

        //Local DB
        localDB =new DataBase(this);

        recyclerView=(RecyclerView)findViewById(R.id.recycler_search);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

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

        //load all foods
        loadAllFoods();
    }

    private void loadAllFoods() {

        adapter= new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList//
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
                            Toast.makeText(SearchActivity.this, ""+model.getName()+" was added to Favorites",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            localDB.removeFromFavorites(adapter.getRef(position).getKey());
                            viewHolder.fav_img.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(SearchActivity.this, " "+model.getName()+" was removed from Favorites",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                final Food local=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongclick) {
                        // Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                        Intent foodDetail = new Intent(SearchActivity.this,FoodDetail.class);
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


    private void loadSuggest() {
        foodList.addListenerForSingleValueEvent(new ValueEventListener() {
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
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.food_image);






                final Food local=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongclick) {
                        // Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                        Intent foodDetail = new Intent(SearchActivity.this,FoodDetail.class);
                        foodDetail.putExtra("FoodId",searchAdapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });



            }
        };

        recyclerView.setAdapter(searchAdapter); // Set adapter for Recycler view is search result


    }


}


