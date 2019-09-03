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

package co.id.roningrum.dolanapptugasakhir;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterEmailActivity extends AppCompatActivity implements View.OnClickListener {

    String EMAIL_KEY = "emailkey";
    String email_key = "";
    private EditText emailRegister, passRegister;
    private TextView loginLinkPage;
    private Button btnRegister1;
    private FirebaseAuth firebaseAuthReg1;
    private FirebaseUser firebaseUserReg;
    private DatabaseReference registerUser1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email);

        emailRegister = findViewById(R.id.edt_email_register);
        passRegister = findViewById(R.id.edt_password_register);
        loginLinkPage = findViewById(R.id.login_link);

        btnRegister1 = findViewById(R.id.btn_register1_page);
        btnRegister1.setOnClickListener(this);
        loginLinkPage.setOnClickListener(this);

        FirebaseApp.initializeApp(this);
        firebaseAuthReg1 = FirebaseAuth.getInstance();
        registerUser1 = FirebaseDatabase.getInstance().getReference();
        firebaseUserReg = firebaseAuthReg1.getCurrentUser();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register1_page:
                registerPhase1();
                break;
            case R.id.login_link:
                startActivity(new Intent(RegisterEmailActivity.this, SignInEmailActivity.class));
                break;
        }

    }

    private void registerPhase1() {
        String email = emailRegister.getText().toString().trim();
        final String password = passRegister.getText().toString().trim();

        boolean isEmptyFields = false;

        if (TextUtils.isEmpty(email)) {
            isEmptyFields = true;
            emailRegister.setError("Email harus diisi");
        }
        if (TextUtils.isEmpty(password)) {
            isEmptyFields = true;
            emailRegister.setError("Password harus diisi");
        }
        if (!isEmptyFields) {
            SharedPreferences sharedPreferences = getSharedPreferences(EMAIL_KEY, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(email_key, email);
            editor.apply();

            firebaseAuthReg1.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterEmailActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        registerUser1.child("Users").child(firebaseUserReg.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                dataSnapshot.getRef().child("email").setValue(emailRegister.getText().toString());
                                dataSnapshot.getRef().child("password").setValue(passRegister.getText().toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                //kalau gagal
                            }
                        });
                        sendEmailVerification();
                        startActivity(new Intent(RegisterEmailActivity.this, RegisterEmail2.class));
                    }

                }
            });
        }

    }

    private void sendEmailVerification() {
        firebaseUserReg.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterEmailActivity.this,
                            "Verification email sent to " + firebaseUserReg.getEmail(),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(RegisterEmailActivity.this,
                            "Failed to send verification email.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RegisterEmailActivity.this, SignInActivity.class));
    }
}
