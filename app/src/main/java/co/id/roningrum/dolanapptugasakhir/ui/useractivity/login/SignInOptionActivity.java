/*
 * Copyright 2019 RONINGRUM. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.id.roningrum.dolanapptugasakhir.ui.useractivity.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.ui.homeactivity.MainMenuActivity;
import co.id.roningrum.dolanapptugasakhir.ui.useractivity.register.RegisterAccountEmailActivity;

public class SignInOptionActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 901;
    private static final String TAG = "Sign In Google";
    boolean isGoogleSignIn;
    private FirebaseAuth fbAuth;
    private DatabaseReference userRef;
    private GoogleSignInClient googleSignInClient;


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = fbAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent goToHomeIntent = new Intent(SignInOptionActivity.this, MainMenuActivity.class);
            startActivity(goToHomeIntent);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_account_options);
        Button btnSignInEmailPage = findViewById(R.id.btn_login_email);
        TextView tvRegisterLinkPage = findViewById(R.id.register_link);
        Button signInGooglebtn = findViewById(R.id.btn_sign_in_google);

        btnSignInEmailPage.setOnClickListener(this);
        signInGooglebtn.setOnClickListener(this);
        tvRegisterLinkPage.setOnClickListener(this);

        fbAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_email:
                goToSignInEmail();
                break;
            case R.id.register_link:
                goToRegisterPage();
                break;
            case R.id.btn_sign_in_google:
                signInWithGoogle();
                break;
        }

    }

    private void goToRegisterPage() {
        startActivity(new Intent(SignInOptionActivity.this, RegisterAccountEmailActivity.class));
    }

    private void goToSignInEmail() {
        startActivity(new Intent(SignInOptionActivity.this, SignInEmailActivity.class));
    }

    private void signInWithGoogle() {
        Intent googleSignInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(googleSignInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            //variabel isGoogleSignIn is used for checking sign in google

                            isGoogleSignIn = true;
                            final FirebaseUser user = fbAuth.getCurrentUser();
                            assert user != null;
                            final String uid = user.getUid();

                            //the problem code is here! How I can detect the google user account has stored and registered
//                            pbLoading.setVisibility(View.VISIBLE);
                            userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists() && isGoogleSignIn) {
                                        Intent googleRegistered = new Intent(SignInOptionActivity.this, MainMenuActivity.class);
                                        googleRegistered.putExtra("isGoogle", isGoogleSignIn);
                                        startActivity(googleRegistered);
                                    } else if (!dataSnapshot.exists()) {

                                        HashMap<Object, String> hashMap = new HashMap<>();
                                        hashMap.put("uid", uid);
                                        hashMap.put("nama_user", user.getDisplayName());
                                        hashMap.put("password", "");
                                        hashMap.put("email", user.getEmail());
                                        hashMap.put("photo_user", "");
                                        hashMap.put("login", "Google");

                                        dataSnapshot.getRef().setValue(hashMap);
//                                        userRef.child("uid").setValue(uid);
//                                        userRef.child("nama_user").setValue(user.getDisplayName());
//                                        userRef.child("password").setValue("");
//                                        userRef.child("email").setValue(user.getEmail());
//                                        userRef.child("photo_user").setValue(String.valueOf(user.getPhotoUrl()));
//                                        userRef.child("login").setValue("Google");
//
//                                        pbLoading.setVisibility(View.GONE);
                                        Intent googleRegistered = new Intent(SignInOptionActivity.this, MainMenuActivity.class);
                                        startActivity(googleRegistered);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }

                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
