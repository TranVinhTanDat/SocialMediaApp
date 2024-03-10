package com.team8.socialmedia.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.team8.socialmedia.R;
import com.team8.socialmedia.adapters.AdapterUsers;
import com.team8.socialmedia.adapters.ModelUser;

import java.util.ArrayList;
import java.util.List;

public class PostLikedByActivity extends AppCompatActivity {
    String postId;
    private RecyclerView recyclerView;
    private List<ModelUser> userList;
    private AdapterUsers adapterUsers;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_liked_by);

        //actionbar
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Post Liked By");

        //add back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        actionBar.setSubtitle(firebaseAuth.getCurrentUser().getEmail());

        recyclerView = findViewById(R.id.recyclerView);

        //get the post id
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");

        userList = new ArrayList<>();
        //get the list of UIDs of users who liked the post
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Likes");
        ref.child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    String hisUid = ""+ ds.getRef().getKey();

                    //get user info from each id
                    getUsers(hisUid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getUsers(String hisUid) {
        //get information of each user using uid
        DatabaseReference reference =  FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(hisUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                            ModelUser modelUser= dataSnapshot.getValue(ModelUser.class);
                            userList.add(modelUser);
                        }
                        //setup adapter
                        adapterUsers = new AdapterUsers(PostLikedByActivity.this, userList);
                        //set adapter to recyclerview
                        recyclerView.setAdapter(adapterUsers);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}