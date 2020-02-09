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

package co.id.roningrum.dolanapptugasakhir.ui.useractivity.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.ui.useractivity.login.SignInEmailActivity;
import co.id.roningrum.dolanapptugasakhir.ui.useractivity.login.SignInOptionActivity;

public class RegisterAccountEmailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "STATUS_REGISTER";
    private TextInputLayout edtEmailRegister, edtPasswordRegister;
    private FirebaseAuth authRegister;
    private DatabaseReference dbRegisterRef;

    private String emailRegister, passwordRegister;
    private ProgressDialog pdDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account_email);
        edtEmailRegister = findViewById(R.id.edt_email_register_layout);
        edtPasswordRegister = findViewById(R.id.edt_password_register_layout);
        Button btnRegisterEmail = findViewById(R.id.btn_register1_page);
        TextView tvLoginPage = findViewById(R.id.tv_login_page_link);
        ImageButton btnBack = findViewById(R.id.button_back);

        authRegister = FirebaseAuth.getInstance();
        dbRegisterRef = FirebaseDatabase.getInstance().getReference();

        pdDialog = new ProgressDialog(this);
        pdDialog.setTitle("Memproses Daftar Akun");
        pdDialog.setMessage("Loading....");
        pdDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        FirebaseApp.initializeApp(this);
        tvLoginPage.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnRegisterEmail.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register1_page:
                pdDialog.show();
                pdDialog.setCancelable(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                            registerEmailProcess();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        pdDialog.dismiss();
                    }
                }).start();
                break;
            case R.id.tv_login_page_link:
                intentLoginPage();
                break;
            case R.id.button_back:
                backToSignInOption();
                break;

        }

    }

    private void backToSignInOption() {
        Intent goToSignInOpt = new Intent(this, SignInOptionActivity.class);
        startActivity(goToSignInOpt);
    }

    private void intentLoginPage() {
        Intent goToLoginPage = new Intent(RegisterAccountEmailActivity.this, SignInEmailActivity.class);
        startActivity(goToLoginPage);
    }

    private void registerEmailProcess() {
        emailRegister = Objects.requireNonNull(edtEmailRegister.getEditText()).getText().toString();
        passwordRegister = Objects.requireNonNull(edtPasswordRegister.getEditText()).getText().toString();

        if (emailRegister.isEmpty()) {
            edtEmailRegister.setError("Masukkan Email");
        } else if (!isValidEmail(emailRegister)) {
            edtEmailRegister.setError("Email tidak valid");
        } else if (passwordRegister.isEmpty()) {
            edtEmailRegister.setErrorEnabled(false);
            edtPasswordRegister.setError("Masukkan Password");
        } else if (passwordRegister.length() <= 6) {
            edtPasswordRegister.setError("Password minimal 6 karakter");
        } else {
            authRegister.createUserWithEmailAndPassword(emailRegister, passwordRegister).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Register Account: " + task.getResult());
                        final FirebaseUser userRegister = authRegister.getCurrentUser();
                        if (userRegister != null) {
                            final String uid = userRegister.getUid();

                            final DatabaseReference userRegisterStoreDB = dbRegisterRef.child("Users").child(uid);
                            userRegisterStoreDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        HashMap<Object, String> hashMap = new HashMap<>();
                                        hashMap.put("uid", uid);
                                        hashMap.put("nama_user", "");
                                        hashMap.put("password", passwordRegister);
                                        hashMap.put("email", emailRegister);
                                        hashMap.put("photo_user", "");
                                        hashMap.put("login", "email");
//                                        userRegisterStoreDB.child("uid").setValue(uid);
//                                        userRegisterStoreDB.child("email").setValue(emailRegister);
//                                        userRegisterStoreDB.child("password").setValue(passwordRegister);
//                                        userRegisterStoreDB.child("login").setValue("email");
                                        dataSnapshot.getRef().setValue(hashMap);
                                        userRegister.sendEmailVerification();
                                        Intent registerNextStep = new Intent(RegisterAccountEmailActivity.this, RegisterAccountProfileActivity.class);
                                        startActivity(registerNextStep);
                                        finish();
                                    } else {
                                        Log.d(TAG, "Data sudah ada di Database");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e(TAG, "Status : " + databaseError.getMessage());
                                }
                            });
                        }

                    } else {
                        Log.w(TAG, "Register Account : " + task.getException());
                        Toast.makeText(getApplicationContext(), " " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }


    private boolean isValidEmail(String emailRegister) {
        return Patterns.EMAIL_ADDRESS.matcher(emailRegister).matches();
    }
}
