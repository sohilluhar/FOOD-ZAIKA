package io.kustrad.dakaar;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import io.kustrad.dakaar.Common.Category;
import io.kustrad.dakaar.Common.Common;
import io.kustrad.dakaar.Interface.ItemClickListener;
import io.kustrad.dakaar.Model.Token;
import io.kustrad.dakaar.viewHolder.MenuViewHolder;
import io.paperdb.Paper;

/*
* #if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end #parse("File Header.java") public class ${NAME} { }
*
* */
public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference category;
    TextView txtFullname;
    RecyclerView recyclerMenu;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Category,MenuViewHolder> adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);


        //init firebase
        database=FirebaseDatabase.getInstance();
        category=database.getReference("Category");

        Paper.init(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntend = new Intent(Home.this,Cart.class);
                startActivity(cartIntend);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set USer name
        View headerView =navigationView.getHeaderView(0);
        txtFullname = (TextView)headerView.findViewById(R.id.txtFullname);
        txtFullname.setText(Common.currentUser.getName());


        //load Menu
        recyclerMenu = (RecyclerView)findViewById(R.id.recycler_menu);
        recyclerMenu.setHasFixedSize(true);
        //layoutManager=new LinearLayoutManager(this);
        //recyclerMenu.setLayoutManager(layoutManager);
        recyclerMenu.setLayoutManager(new GridLayoutManager(this,2));

        if(Common.isConnectedToInternet(this))
            loadMenu();
        else
        {
            Toast.makeText(this, "Please check your connection !!",Toast.LENGTH_SHORT).show();
            //return;
        }

        updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    private void updateToken(String token) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token data=new Token(token,false);
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }

    private void loadMenu() {
         adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class,R.layout.menu_item,MenuViewHolder.class,category) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {
                viewHolder.txtMenuname.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);
                final Category clickItem=model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongclick) {
                       // Toast.makeText(Home.this, ""+clickItem.getName(), Toast.LENGTH_SHORT).show();
                        // Toast.makeText(Home.this, ""+clickItem.getImage(), Toast.LENGTH_SHORT).show(
                        // );
                      //get category id and send to new activity
                        Intent foodList = new Intent(Home.this,FoodList.class);
                        //categoryId id key
                        foodList.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(foodList);

                    }
                });
            }
        };

        recyclerMenu.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.search)
        startActivity(new Intent(Home.this,SearchActivity.class));
        return super.onOptionsItemSelected(item);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        } else if (id == R.id.nav_cart) {
            Intent cardIntent= new Intent(Home.this,Cart.class);
            startActivity(cardIntent);

        } else if (id == R.id.nav_logout) {

            //delete Remember user and password
            Paper.book().destroy();


            Intent signin = new Intent(Home.this, LogIn.class);
            signin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signin);

        } else if (id == R.id.nav_orders) {
            Intent orderIntent= new Intent(Home.this,OrderStatus.class);
            startActivity(orderIntent);

        } else if (id == R.id.nav_view) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
