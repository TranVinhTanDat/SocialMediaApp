package com.team8.socialmedia.activities;

import android.content.SharedPreferences;
import android.widget.CompoundButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SwitchCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.team8.socialmedia.R;
import org.jetbrains.annotations.NotNull;

public class SettingsActivity extends AppCompatActivity {
    SwitchCompat postSwitch;
    //use shared preferences to save the state of switch
    SharedPreferences sp;
    SharedPreferences.Editor editor;//to edit value of shared pref

    //constant for topic
    private static final String TOPIC_POST_NOTIFICATION = "POST";//assign any value but use same for this kind of notifications

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Settings");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        postSwitch= findViewById(R.id.postSwitch);

        //init sp
        sp = getSharedPreferences("Notification_SP", MODE_PRIVATE);
        boolean isPostEnabled= sp.getBoolean(""+ TOPIC_POST_NOTIFICATION, false);
        //if enabled check switch, otherwise uncheck switch - by default unchecked/false
        if(isPostEnabled){
            postSwitch.setChecked(true);
        }else {
            postSwitch.setChecked(false);
        }
        //implement switch change listener
        postSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //edit switch state
                editor = sp.edit();
                editor.putBoolean(""+TOPIC_POST_NOTIFICATION, isChecked);
                editor.apply();

                if(isChecked){
                    subscribePostNotification();//call to subscribe
                }else{
                    unsubscribePostNotification();//call to unsubscribe
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void unsubscribePostNotification() {
        //unsubscribe to a topic (POST) to disable it's notification
        FirebaseMessaging.getInstance().unsubscribeFromTopic(""+TOPIC_POST_NOTIFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        String msg="You will not receive post notifications";
                        if(!task.isSuccessful()){
                            msg = "UnSubscription failed";
                        }
                        Toast.makeText(SettingsActivity.this,msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void subscribePostNotification() {
        //subscribe to a topic (POST) to disable it's notification
        FirebaseMessaging.getInstance().subscribeToTopic(""+TOPIC_POST_NOTIFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        String msg="You will receive post notifications";
                        if(!task.isSuccessful()){
                            msg = "Subscription failed";
                        }
                        Toast.makeText(SettingsActivity.this,msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}