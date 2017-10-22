package com.blogtry.myretail.recipieblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetupActivity extends AppCompatActivity {

    private ImageButton mSetupImageBtn;
    private EditText mSetupName;
    private Button mSetupBtn;
    private Uri mImageUri = null;

    private static int GALLERY_REQ_CODE=1;

    private DatabaseReference mDataBaseUsers;

    private StorageReference mStorageImage;

    private FirebaseAuth mAuth;

    private ProgressDialog mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setup);

        mSetupImageBtn = (ImageButton) findViewById(R.id.setupImageBtn);
        mSetupName = (EditText) findViewById(R.id.setupNameField);
        mSetupBtn =(Button) findViewById(R.id.setupSubmitBtn);

        mDataBaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth=FirebaseAuth.getInstance();

        mStorageImage = FirebaseStorage.getInstance().getReference().child("Profile Picture");

        mProgressBar= new ProgressDialog(this);

        mSetupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mImageUri==null){

                    Toast.makeText(SetupActivity.this,"you have to add a profile picutre",Toast.LENGTH_LONG).show();

                }
                else {
                    startSetupAccount();
                }
            }
        });

        mSetupImageBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/+");
                startActivityForResult(galleryIntent,GALLERY_REQ_CODE);

            }
        });
    }

    private void startSetupAccount() {

        final String name = mSetupName.getText().toString().trim();

        final String user_id=mAuth.getCurrentUser().getUid();

        if(!TextUtils.isEmpty(name)){

            mProgressBar.setMessage("Setting up Account");
            mProgressBar.show();

            StorageReference filepath= mStorageImage.child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    @SuppressWarnings("VisibleForTests")  String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    mDataBaseUsers.child(user_id).child("name").setValue(name);
                    mDataBaseUsers.child(user_id).child("image").setValue(downloadUri);

                    mProgressBar.dismiss();

                    Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);


                }
            });

           /* */

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_REQ_CODE && resultCode==RESULT_OK){

            Uri imageUri= data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();

                mSetupImageBtn.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }



    }
}
