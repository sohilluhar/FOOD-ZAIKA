package io.kustrad.dakaar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.kustrad.dakaar.Common.Common;
import io.kustrad.dakaar.Model.User;
import io.paperdb.Paper;

public class LogIn extends AppCompatActivity {
    MaterialEditText phone,password;
    Button btnSignIn;
    com.rey.material.widget.CheckBox ckbRemember;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        phone=(MaterialEditText)findViewById(R.id.Phone);
        password=(MaterialEditText)findViewById(R.id.Password);
        btnSignIn=(Button)findViewById(R.id.btnSingIn);
        ckbRemember=(com.rey.material.widget.CheckBox)findViewById(R.id.ckbRemeber);

        //init paper
        Paper.init(this);


        //Initn Fire Base
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Common.isConnectedToInternet(getBaseContext())) {

                    //save user and password
                    if (ckbRemember.isChecked())
                    {
                        Paper.book().write(Common.USER_KEY,phone.getText().toString());
                        Paper.book().write(Common.PWD_KEY,password.getText().toString());

                    }


                    final ProgressDialog nDialog = new ProgressDialog(LogIn.this);
                    nDialog.setMessage("Please Wait .....");
                    nDialog.show();


                    table_user.addValueEventListener(new ValueEventListener() {


                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.child(phone.getText().toString()).exists()) {
                                nDialog.dismiss();

                                //Get User
                                User user = dataSnapshot.child(phone.getText().toString()).getValue(User.class);
                                user.setPhone(phone.getText().toString());//set Phone number
                                if (user.getPassword().equals(password.getText().toString())) {
                                    Intent homeIntent = new Intent(LogIn.this, Home.class);
                                    Common.currentUser = user;
                                    startActivity(homeIntent);
                                    finish();

                                } else {
                                    Toast.makeText(LogIn.this, "Wrong password..", Toast.LENGTH_SHORT).show();

                                }
                            } else {
                                nDialog.dismiss();
                                Toast.makeText(LogIn.this, "User Does Not Exist...", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(LogIn.this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }//check this block

        });
    }
}
