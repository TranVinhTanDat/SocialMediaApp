package com.team8.socialmedia.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.auth.api.identity.*;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.team8.socialmedia.R;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    EditText emailEt, passwordEt;
    Button loginBtn;
    ProgressDialog progressDialog;
    TextView notHaveAccTv,recoverPassTv;
    SignInButton gLoginBtn;
    BeginSignInRequest signInRequest;
    SignInClient oneTapClient;
    //Declare an instance of firebaseAuth
    private FirebaseAuth mAuth;
    private static final int REQ_ONE_TAP = 100;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;
    // ...
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Actionbar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create new account with SoMe");
        //enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        loginBtn = findViewById(R.id.loginBtn);
        notHaveAccTv = findViewById(R.id.not_have_account_tv);
        recoverPassTv=findViewById(R.id.recover_tv);
        gLoginBtn=findViewById(R.id.gLogin_btn);
        //initialize the firebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //to do
        }
        progressDialog=new ProgressDialog(this);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //input email,password
                String email=emailEt.getText().toString().trim();
                String password=passwordEt.getText().toString().trim();
                //validate
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //set error and focus to email edittext
                    emailEt.setError("Invalid Email");
                    emailEt.setFocusable(true);
                }else if(password.length()<6){
                    //set error and focus to email edittext
                    emailEt.setError("Password length at least 6 characters");
                    emailEt.setFocusable(true);
                }else {
                    loginUser(email,password);
                }
            }
        });
        notHaveAccTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
        recoverPassTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });

        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();

        oneTapClient = Identity.getSignInClient(LoginActivity.this);

        gLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oneTapClient.beginSignIn(signInRequest)
                        .addOnSuccessListener(LoginActivity.this, new OnSuccessListener<BeginSignInResult>() {
                            @Override
                            public void onSuccess(BeginSignInResult result) {
                                try {
                                    startIntentSenderForResult(
                                            result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                            null, 0, 0, 0);
                                } catch (IntentSender.SendIntentException e) {
                                    Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(LoginActivity.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // No saved credentials found. Launch the One Tap sign-up flow, or
                                // do nothing and continue presenting the signed-out UI.
                                Toast.makeText(LoginActivity.this,"No saved credentials found...",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void showRecoverPasswordDialog() {
        //AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");
        //set layout linear layout
        LinearLayout linearLayout=new LinearLayout(this);
        //views to set in dialog
        EditText emailEt=new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEt.setMinEms(16);
        emailEt.setTextColor(Color.BLACK);
        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);

        builder.setView(linearLayout);
        //buttons recover
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input email
                String email = emailEt.getText().toString().trim();
                beginRecoverPass(email);
            }
        });
        //button cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dismiss dialog
                dialog.dismiss();
            }
        });
        //show dialog
        builder.create().show();
    }

    private void beginRecoverPass(String email) {
        //show progress diaglog
        progressDialog.setMessage("Sending Email ...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,"Email is sent to "+email,Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(LoginActivity.this,"Failed ...",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                progressDialog.dismiss();
                //get and show proper error message
                Toast.makeText(LoginActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(String email, String password) {
        //show progress diaglog
        progressDialog.setMessage("Logging In...");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //dismiss process dialog
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        //error, dismiss progress dialog and get and show the error message
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,"Authentication failed.",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();//go previous activity
        return super.onSupportNavigateUp();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SignInClient oneTapClient = Identity.getSignInClient(this);
        switch (requestCode) {
            case REQ_ONE_TAP:
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                    String idToken = credential.getGoogleIdToken();
                    if (idToken !=  null) {
                        // Got an ID token from Google. Use it to authenticate
                        // with Firebase.
                        AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                        mAuth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            // Sign in success, update UI with the signed-in user's information
                                            FirebaseUser user = mAuth.getCurrentUser();

                                            //Begin : vu
                                            if(task.getResult().getAdditionalUserInfo().isNewUser())
                                            {
                                                //get user email and uid
                                                String email = user.getEmail();
                                                String uid = user.getUid();
                                                //when user is registered store user info in firebase realtime database too
                                                //using HashMap
                                                HashMap<Object, String> hashMap = new HashMap<>();
                                                //put info to Hashmap
                                                hashMap.put("email", email);
                                                hashMap.put("uid", uid);
                                                hashMap.put("name", "");
                                                hashMap.put("onlineStatus", "online");
                                                hashMap.put("typingTo", "noOne");
                                                hashMap.put("phone", "");
                                                hashMap.put("image", "");
                                                hashMap.put("cover", "");
                                                //firebase database instance
                                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                //path
                                                DatabaseReference reference = database.getReference("Users");
                                                //put data within hashmap in database
                                                reference.child(uid).setValue(hashMap);
                                            }

                                            //End

                                            //show user email in Toast
                                            Toast.makeText(LoginActivity.this,"Hi, "+user.getEmail(),Toast.LENGTH_SHORT).show();

                                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                                            finish();
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Toast.makeText(LoginActivity.this,"Login Failed... ",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        //get and show error message
                                        Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                } catch (ApiException e) {
                    // ...
                }
                break;
        }
    }
}