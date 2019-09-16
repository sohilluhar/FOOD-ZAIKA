package io.kustrad.dakaar;

import android.app.ProgressDialog;
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
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.kustrad.dakaar.Common.Common;
import io.kustrad.dakaar.Model.User;

public class SignUp extends AppCompatActivity {


    MaterialEditText  phone,name,password;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        phone=(MaterialEditText)findViewById(R.id.Phone);
        name=(MaterialEditText)findViewById(R.id.Name);
        password=(MaterialEditText)findViewById(R.id.Password);
        btnSignUp=(Button)findViewById(R.id.btnSignUp);

        //Initn Fire Base
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("User");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Common.isConnectedToInternet(getBaseContext())) {
                    final ProgressDialog nDialog = new ProgressDialog(SignUp.this);
                    nDialog.setMessage("Please Wait .....");
                    nDialog.show();


                    table_user.addValueEventListener(new ValueEventListener() {
                                                         @Override
                                                         public void onDataChange(DataSnapshot dataSnapshot) {

                                                             //check if user exist
                                                             if (dataSnapshot.child(phone.getText().toString()).exists()) {
                                                                 nDialog.dismiss();
                                                                 Toast.makeText(SignUp.this, "Phone Number Already Exist", Toast.LENGTH_SHORT).show();
                                                             } else {

                                                                 nDialog.dismiss();
                                                                 User user = new User(name.getText().toString(), password.getText().toString());
                                                                 table_user.child(phone.getText().toString()).setValue(user);
                                                                 Toast.makeText(SignUp.this, "Sign Up Successfully", Toast.LENGTH_SHORT).show();
                                                                 finish();

                                                             }

                                                         }

                                                         @Override
                                                         public void onCancelled(DatabaseError databaseError) {

                                                         }
                                                     }
                    );
                }
                else    {
                    Toast.makeText(SignUp.this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
}
