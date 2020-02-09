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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.ui.homeactivity.MainMenuActivity;
import co.id.roningrum.dolanapptugasakhir.ui.useractivity.register.RegisterAccountEmailActivity;
import co.id.roningrum.dolanapptugasakhir.ui.useractivity.reset.ResetPasswordActivity;

public class SignInEmailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "STATUS_LOGIN";
    private TextInputLayout edtEmailSignIn, edtPasswordSignIn;

    private FirebaseAuth authLogin;
    private ProgressDialog pdDialog;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = authLogin.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent goToHomeIntent = new Intent(SignInEmailActivity.this, MainMenuActivity.class);
            startActivity(goToHomeIntent);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_account_email);
        edtEmailSignIn = findViewById(R.id.edt_email_login_layout);
        edtPasswordSignIn = findViewById(R.id.edt_password_login_layout);
        ImageButton btnBack = findViewById(R.id.button_back);
        Button btnSignInAccount = findViewById(R.id.btn_login);
        TextView tvResetPage = findViewById(R.id.tv_reset_pass);
        TextView tvRegisterPage = findViewById(R.id.tv_register_link);

        pdDialog = new ProgressDialog(this);
        pdDialog.setTitle("Memproses Akun");
        pdDialog.setMessage("Loading....");
        pdDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        authLogin = FirebaseAuth.getInstance();

        tvRegisterPage.setOnClickListener(this);
        btnSignInAccount.setOnClickListener(this);
        tvResetPage.setOnClickListener(this);
        btnBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                pdDialog.show();
                pdDialog.setCancelable(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                            signInProcess();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        pdDialog.dismiss();
                    }
                }).start();
                break;
            case R.id.tv_register_link:
                goToRegisterPage();
                break;
            case R.id.tv_reset_pass:
                goToResetPage();
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

    private void goToResetPage() {
        Intent goToResetPageIntent = new Intent(this, ResetPasswordActivity.class);
        startActivity(goToResetPageIntent);
    }

    private void goToRegisterPage() {
        Intent goToRegisterIntent = new Intent(this, RegisterAccountEmailActivity.class);
        startActivity(goToRegisterIntent);
    }
    private void signInProcess() {

        String emailLogin = Objects.requireNonNull(edtEmailSignIn.getEditText()).getText().toString();
        String passwordLogin = Objects.requireNonNull(edtPasswordSignIn.getEditText()).getText().toString();
        if (emailLogin.isEmpty()) {
            edtEmailSignIn.setError("Masukkan Email");
        } else if (!isValidEmail(emailLogin)) {
            edtEmailSignIn.setError("Email tidak valid");
        } else if (passwordLogin.isEmpty()) {
            edtPasswordSignIn.setError("Masukkan Password");
        } else if (passwordLogin.length() <= 6) {
            edtPasswordSignIn.setError("Password minimal 6 karakter");
        } else {
            authLogin.signInWithEmailAndPassword(emailLogin, passwordLogin).addOnCompleteListener(SignInEmailActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser userLogin = authLogin.getCurrentUser();
                        assert userLogin != null;
                        if (userLogin.isEmailVerified()) {
                            {
                                Intent goToHomeIntent = new Intent(SignInEmailActivity.this, MainMenuActivity.class);
                                startActivity(goToHomeIntent);
                                finish();
                                Log.d(TAG, "Berhasil Masuk");
                            }
                        } else {
                            Toast.makeText(SignInEmailActivity.this, "Pastikan email sudah diverifikasi", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignInEmailActivity.this, " " + task.getException(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Gagal Masuk");
                    }
                }
            });

        }
    }

    private boolean isValidEmail(String emailLogin) {
        return Patterns.EMAIL_ADDRESS.matcher(emailLogin).matches();
    }
}
