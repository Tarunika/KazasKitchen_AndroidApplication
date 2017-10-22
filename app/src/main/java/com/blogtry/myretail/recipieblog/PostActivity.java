package com.blogtry.myretail.recipieblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URL;

public class PostActivity extends AppCompatActivity {

    private ImageButton mSelect ;

    private EditText mpostTitle;
    private EditText mpostDesc;
    private EditText mpostRecipe;
    private EditText mYTid;

    private EditText Time;
    private EditText People;

    private Button mpostBtn;

    private Uri imageUri = null;

    private static final int GALLERY_REQUEST=1;

    private StorageReference mstorage;
    private DatabaseReference mdatabase;

    private ProgressDialog mProgress;

    private FirebaseAuth mAuth;

    private FirebaseUser mCurrentUser;

    private DatabaseReference mDatabaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth= FirebaseAuth.getInstance();
        mCurrentUser =mAuth.getCurrentUser();

        mSelect = (ImageButton) findViewById(R.id.imageSelect);

        mstorage= FirebaseStorage.getInstance().getReference();
        mdatabase = FirebaseDatabase.getInstance().getReference().child("Blog");

        mDatabaseUsers= FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        mpostTitle = (EditText) findViewById(R.id.titleField);
        mpostDesc =(EditText) findViewById(R.id.descField);
        mpostRecipe = (EditText) findViewById(R.id.recipeField);
        mYTid = (EditText) findViewById(R.id.YTidField);
        Time = (EditText) findViewById(R.id.timeField);
        People = (EditText) findViewById(R.id.servesField);

        mpostBtn =(Button) findViewById(R.id.submitBtn);

        mProgress= new ProgressDialog(this);




        mSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleyIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleyIntent.setType("image/*");
                startActivityForResult(galleyIntent,GALLERY_REQUEST);
            }
        });

        mpostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPosting();
            }
        });

    }

    private void startPosting(){

        mProgress.setMessage("Uploading...");
        mProgress.show();

        final String titleVal=mpostTitle.getText().toString().trim();
        final String descVal= mpostDesc.getText().toString().trim();
        final String recipieVal = mpostRecipe.getText().toString().trim();
        final String YTval = mYTid.getText().toString().trim();
        final String timeVal=Time.getText().toString().trim();
        final String peopleVal = People.getText().toString().trim();



        if(!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descVal) && imageUri!=null){

            StorageReference filePath = mstorage.child("BlogImage").child(imageUri.getLastPathSegment());
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    @SuppressWarnings("VisibleForTests") final Uri downloadUrl=taskSnapshot.getDownloadUrl();
                    //@SuppressWarnings("VisibleForTests")  String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                    mProgress.dismiss();

                    final DatabaseReference newPost= mdatabase.push();

                    mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            newPost.child("Title").setValue(titleVal);
                            newPost.child("Description").setValue(descVal);
                            newPost.child("Recipe").setValue(recipieVal);
                            newPost.child("YOUTUBEid").setValue(YTval);
                            newPost.child("Image").setValue(downloadUrl.toString());
                            newPost.child("uid").setValue(mCurrentUser.getUid());
                            newPost.child("time").setValue(timeVal);
                            newPost.child("people").setValue(peopleVal);
                            newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        startActivity(new Intent(PostActivity.this,MainActivity.class));



                                    }
                                }
                            });




                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK){

             imageUri= data.getData();

            mSelect.setImageURI(imageUri);

        }
    }
}
