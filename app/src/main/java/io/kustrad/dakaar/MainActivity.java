package io.kustrad.dakaar;

import android.*;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.kustrad.dakaar.Common.Common;
import io.kustrad.dakaar.Model.User;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    Button btnSignIn,btnSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = (Button)findViewById(R.id.btnLogIn);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);

        //init paper
        Paper.init(this);

         btnSignUp.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent signUp =new Intent(MainActivity.this,SignUp.class);
                 startActivity(signUp);
             }
         });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signIn =new Intent(MainActivity.this,LogIn.class);
                startActivity(signIn);
            }
        });

        //check Remember
        String user = Paper.book().read(Common.USER_KEY);
        String password=Paper.book().read(Common.PWD_KEY);
        if (user!=null && password!=null)
        {
            if (!user.isEmpty()&&!password.isEmpty())
            logIn(user,password);
        }


    }

    private void logIn(final String phone, final String password) {
        //       Initn Fire Base
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("User");
        if (Common.isConnectedToInternet(getBaseContext())) {




            final ProgressDialog nDialog = new ProgressDialog(MainActivity.this);
            nDialog.setMessage("Please Wait .....");
            nDialog.show();


            table_user.addValueEventListener(new ValueEventListener() {


                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(phone).exists()) {
                        nDialog.dismiss();

                        //Get User
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);//set Phone number
                        if (user.getPassword().equals(password)) {
                            Intent homeIntent = new Intent(MainActivity.this, Home.class);
                            Common.currentUser = user;
                            startActivity(homeIntent);
                            finish();

                        } else {
                            Toast.makeText(MainActivity.this, "Wrong password..", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        nDialog.dismiss();
                        Toast.makeText(MainActivity.this, "User Does Not Exist...", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            Toast.makeText(MainActivity.this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
