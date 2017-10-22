package com.blogtry.myretail.recipieblog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class BlogSingleActivity extends YouTubeBaseActivity {

    private String mPost_key = null;

    private DatabaseReference mDatabase;

    private ImageView mSinglePostView;
    private TextView mBlogSingleTitle;
    private TextView mBlogSingleText;
    private Button mRemoveBtn;
    private Button mPlaybtn;
    private FirebaseAuth mAuth;

    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer.OnInitializedListener onInitializedListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_single);

        getWindow().setAllowEnterTransitionOverlap(false);

        Slide slide=new Slide(Gravity.RIGHT);
        getWindow().setReturnTransition(slide);


        mSinglePostView = (ImageView) findViewById(R.id.singlePostImage);
        mBlogSingleTitle = (TextView) findViewById(R.id.singlePostTitle);
        mBlogSingleText = (TextView) findViewById(R.id.singlePostDesc);
        mRemoveBtn =(Button)findViewById(R.id.RemoveBtn);
        mPlaybtn = (Button) findViewById(R.id.PlayBtn);
        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtubeVideo);


        mAuth=FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");

        mPost_key = getIntent().getExtras().getString("blog_id");

        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String post_title= (String) dataSnapshot.child("Title").getValue();
                String post_desc = (String) dataSnapshot.child("Recipe").getValue();
                String post_image = (String) dataSnapshot.child("Image").getValue();
                String post_Uid = (String)dataSnapshot.child("uid").getValue();
                final String post_YTid = (String)dataSnapshot.child("YOUTUBEid").getValue();

                mBlogSingleTitle.setText(post_title);
                mBlogSingleText.setText(post_desc);

                Picasso.with(BlogSingleActivity.this).load(post_image).into(mSinglePostView);

                if(mAuth.getCurrentUser().getUid().toString().equals(post_Uid)){

                    mRemoveBtn.setVisibility(View.VISIBLE);
                }

                if(("").equals(post_YTid)){

                    mPlaybtn.setVisibility(View.GONE);
                    youTubePlayerView.setVisibility(View.GONE);

                }


                onInitializedListener = new YouTubePlayer.OnInitializedListener() {
                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

                            youTubePlayer.loadVideo(post_YTid);


                        }

                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                        }
                    };



                mPlaybtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        youTubePlayerView.initialize("AIzaSyA-sDRm9L936XB_FViVMfxheWyoipVNHCs",onInitializedListener);
                    }
                });



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Toast.makeText(BlogSingleActivity.this,post_key,Toast.LENGTH_LONG).show();

        mRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.child(mPost_key).removeValue();

                Intent mainIntent = new Intent(BlogSingleActivity.this,MainActivity.class);
                startActivity(mainIntent);


            }
        });

    }
}
