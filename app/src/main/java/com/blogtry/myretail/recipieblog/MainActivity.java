package com.blogtry.myretail.recipieblog;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mBloglist;
    private DatabaseReference mdata;

    private FirebaseAuth mAuth ;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mDatabaseUsers;

    private DatabaseReference mDatabaseLike;

    private boolean mProcessLike = false;

    private int count=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("Blog");

        Explode explode= new Explode();
        getWindow().setExitTransition(explode);


        mAuth=FirebaseAuth.getInstance();
        mAuthListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

        mdata= FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseUsers=FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");




        mDatabaseUsers.keepSynced(true);
        mDatabaseLike.keepSynced(true);

        mBloglist =(RecyclerView) findViewById(R.id.blog_list);
        mBloglist.setHasFixedSize(true);
        mBloglist.setLayoutManager(new LinearLayoutManager(this));



    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserExist();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter <Blog,BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mdata




        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Blog model, int position) {



                final String post_key = getRef(position).getKey();
                final String[] Count = {null};

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDescription());
                viewHolder.setTime(model.getTime());
                viewHolder.setpeople(model.getPeople());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setLikeBtn(post_key);
                viewHolder.setLikes(Count[0]);




                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent singleBlogIntent = new Intent(MainActivity.this,BlogSingleActivity.class);
                        singleBlogIntent.putExtra("blog_id", post_key);
                        startActivity(singleBlogIntent);
                       // Toast.makeText(MainActivity.this,post_key,Toast.LENGTH_LONG).show();
                    }
                });

                viewHolder.mLikeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mProcessLike=true;



                            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(mProcessLike) {
                                        if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {


                                            mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();



                                            mProcessLike = false;


                                        } else {

                                            mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");
                                            mProcessLike = false;


                                        }
                                        count = (int) dataSnapshot.getChildrenCount();
                                        Count[0] = String.valueOf(count);

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });




                    }
                });



            }


        };

        mBloglist.setAdapter(firebaseRecyclerAdapter);




    }
    private void checkUserExist() {

        if(mAuth.getCurrentUser()!=null) {

            final String user_id = mAuth.getCurrentUser().getUid();

            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(user_id)) {

                        Intent setupIntent = new Intent(MainActivity.this, LoginActivity.class);
                        setupIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }



    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageButton mLikeBtn;
        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuth;
        private TextView mCount;

        public BlogViewHolder(View itemView) {

            super(itemView);

            mView=itemView;
            mLikeBtn = (ImageButton) mView.findViewById(R.id.likeBtn);
            mCount =(TextView) mView.findViewById(R.id.likeCount);
            mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");

            mAuth =FirebaseAuth.getInstance();

            mDatabaseLike.keepSynced(true);



        }


        public void setLikeBtn(final String post_key){

          // final String Likes_count=Integer.toString(count);

            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){

                        mLikeBtn.setImageResource(R.mipmap.ic_favorite_black_24dp);
                       // mCount.setText(Likes_count);


                    }else{


                        mLikeBtn.setImageResource(R.mipmap.ic_favorite_border_black_24dp);

                    }



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



        }

        public void setTitle(String Title){

            TextView post_title= (TextView) mView.findViewById(R.id.post_title);
            post_title.setText(Title);
        }

        public void setDesc(String Desc){
            TextView post_desc= (TextView) mView.findViewById(R.id.post_text);
            post_desc.setText(Desc);

        }
        public void setLikes(String count){



            TextView Like_Count = (TextView) mView.findViewById(R.id.likeCount);
            Like_Count.setText(count);

        }

         public void setUsername(String username){
             TextView post_username= (TextView) mView.findViewById(R.id.post_userName);
             post_username.setText(username);


         }



        public void setImage(Context ctx,String Image){

            ImageView post_image= (ImageView) mView.findViewById(R.id.poast_image);
            Picasso.with(ctx).load(Image).into(post_image);
        }

        public void setTime(String time) {
            TextView post_time= (TextView) mView.findViewById(R.id.timeToCook);
            post_time.setText(time);
        }

        public void setpeople(String people) {

            TextView post_people= (TextView) mView.findViewById(R.id.noOfPeople);
            post_people.setText(people);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        if(mAuth.getCurrentUser().getEmail().toString().equals("taruni.eragon@gmail.com")||mAuth.getCurrentUser().getEmail().toString().equals("tarunkaza1995@gmail.com")){

            if(item.getItemId()==R.id.action_add){

                startActivity(new Intent(MainActivity.this,PostActivity.class));
            }
        }else{


            if(item.getItemId()==R.id.action_add){

                item.setVisible(false);
            }

        }

        if(item.getItemId()==R.id.action_logout){

            logout();
        }
        if(item.getItemId() == R.id.action_settings){

            Intent aboutIntent = new Intent(MainActivity.this, AboutTarun.class);
            aboutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(aboutIntent);


        }


        return super.onOptionsItemSelected(item);
    }

    private void logout() {

        mAuth.signOut();
    }

}
