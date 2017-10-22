package com.blogtry.myretail.recipieblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText mnameField;
    private EditText mEmailField;
    private EditText mPassField;
    private Button mRegisterbtn;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth=FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");

        mnameField=(EditText) findViewById(R.id.nameField);
        mEmailField=(EditText) findViewById(R.id.emailField);
        mPassField =(EditText) findViewById(R.id.passField);
        mRegisterbtn =(Button) findViewById(R.id.registerBtn);

        mRegisterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });

    }

    private void startRegister() {

        final String name=mnameField.getText().toString().trim();
        String pass=mPassField.getText().toString().trim();
        String email=mEmailField.getText().toString().trim();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)){

                   mProgress.setMessage("Signing up...");
                   mProgress.show();
                    mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                String user_id;
                                user_id= mAuth.getCurrentUser().getUid();

                                DatabaseReference current_user_db =mDatabase.child(user_id);

                                current_user_db.child("name").setValue(name);
                                current_user_db.child("image").setValue("default");

                                mProgress.dismiss();

                                Intent mainIntent= new Intent(RegisterActivity.this,MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(mainIntent);




                            }


                        }
                    });

        }

    }
}
