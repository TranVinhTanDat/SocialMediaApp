package com.team8.socialmedia.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.team8.socialmedia.R;
import com.team8.socialmedia.fragment.GroupChatsFragment;
import com.team8.socialmedia.fragment.NotificationsFragment;
import com.team8.socialmedia.fragment.ChatListFragment;
import com.team8.socialmedia.fragment.HomeFragment;
import com.team8.socialmedia.fragment.ProfileFragment;
import com.team8.socialmedia.fragment.UsersFragment;
import com.team8.socialmedia.notifications.Token;

public class DashboardActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ActionBar actionBar;

    String mUID;
    BottomNavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        //Actionbar and its title
        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        //initialize the firebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance();

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        //default frame
        actionBar.setTitle("Home");
        HomeFragment fragment1 = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.container, fragment1, "");
        ft1.commit();

        checkUserStatus();
    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    public void updateToken(String token) {
        if (mUID != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
            Token mToken = new Token(token);
            ref.child(mUID).setValue(mToken);
        } else {
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.nav_home) {
                actionBar.setTitle("Home");
                HomeFragment fragment1 = new HomeFragment();
                FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                ft1.replace(R.id.container, fragment1, "");
                ft1.commit();
                return true;
            }
            if (menuItem.getItemId() == R.id.nav_profile) {
                actionBar.setTitle("Profile");
                ProfileFragment fragment2 = new ProfileFragment();
                FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                ft2.replace(R.id.container, fragment2, "");
                ft2.commit();
                return true;
            }
            if (menuItem.getItemId() == R.id.nav_users) {
                actionBar.setTitle("Users");
                UsersFragment fragment3 = new UsersFragment();
                FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                ft3.replace(R.id.container, fragment3, "");
                ft3.commit();
                return true;
            }
            if (menuItem.getItemId() == R.id.nav_chat) {
                actionBar.setTitle("Chats");
                ChatListFragment fragment4 = new ChatListFragment();
                FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                ft4.replace(R.id.container, fragment4, "");
                ft4.commit();
                return true;
            }
            if(menuItem.getItemId() == R.id.nav_more){
                showMoreOptions();
                return true;
            }
            return false;
        }
    };

    private void showMoreOptions() {
        //popup menu to show more options
        PopupMenu popupMenu = new PopupMenu(this, navigationView, Gravity.END);
        //items to show in menu
        SpannableString s = new SpannableString("Notifications");
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
        SpannableString s1 = new SpannableString("Group Chats");
        s1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s1.length(), 0);
        popupMenu.getMenu().add(Menu.NONE, 0, 0, s);
        popupMenu.getMenu().add(Menu.NONE, 1, 0, s1);

        //menu clicks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id= item.getItemId();
                if(id==0){
                    //notificaitons clicked
                    actionBar.setTitle("Notification");
                    NotificationsFragment fragment5 = new NotificationsFragment();
                    FragmentTransaction ft5 = getSupportFragmentManager().beginTransaction();
                    ft5.replace(R.id.container, fragment5, "");
                    ft5.commit();

                }else if(id==1){
                    //group chats clicked
                    actionBar.setTitle("Group Chats");
                    GroupChatsFragment fragment6 = new GroupChatsFragment();
                    FragmentTransaction ft6 = getSupportFragmentManager().beginTransaction();
                    ft6.replace(R.id.container, fragment6, "");
                    ft6.commit();
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //user is signed in stay here
            //set email of logged-in user

            mUID = user.getUid();
            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            editor.apply();

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // Token retrieved successfully
                            String token = task.getResult();
                            updateToken(token);
                        } else {
                        }
                    });
        } else {
            //user not signed in, go to main activity
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        //check on start of app
        checkUserStatus();
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}