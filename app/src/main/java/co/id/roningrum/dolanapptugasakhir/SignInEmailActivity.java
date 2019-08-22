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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInEmailActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth firebaseAuthLogin;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser curUser;
    private TextView tvRegisterLink;
    private Button LoginEmail;
    private EditText emailLogin, passwordLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_email);
        tvRegisterLink = findViewById(R.id.register_link);
        LoginEmail = findViewById(R.id.btn_login);
        emailLogin = findViewById(R.id.edt_email_address_login);
        passwordLogin = findViewById(R.id.edt_password_login);

        tvRegisterLink.setOnClickListener(this);
        LoginEmail.setOnClickListener(this);

        firebaseAuthLogin = FirebaseAuth.getInstance();
        curUser = firebaseAuthLogin.getCurrentUser();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_link:
                startActivity(new Intent(SignInEmailActivity.this, RegisterEmailActivity.class));
                break;
            case R.id.btn_login:
                SignInEmail();
        }
    }

    private void SignInEmail() {
        String email = emailLogin.getText().toString().trim();
        final String password = passwordLogin.getText().toString().trim();

        boolean isEmptyFields = false;

        if (TextUtils.isEmpty(email)) {
            isEmptyFields = true;
            emailLogin.setError("Email harus diisi");
        }
        if (TextUtils.isEmpty(password)) {
            isEmptyFields = true;
            passwordLogin.setError("Password harus diisi");
        }
        if (!isEmptyFields) {
            firebaseAuthLogin.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    FirebaseUser user = firebaseAuthLogin.getCurrentUser();
                    assert user != null;
                    if (user.isEmailVerified()) {
                        updateUI(user);
                    } else {
                        Toast.makeText(SignInEmailActivity.this, "Silakan cek inbox untuk verifikasi", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(SignInEmailActivity.this, MainMenuActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignInEmailActivity.this, SignInActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // cek apakah pengguna sudah pernah masuk sehingga ada update UI disini
        if (authStateListener != null) {
            FirebaseUser currentUser = firebaseAuthLogin.getCurrentUser();
            updateUI(currentUser);
            authStateListener.onAuthStateChanged(firebaseAuthLogin);
        }

    }
}
