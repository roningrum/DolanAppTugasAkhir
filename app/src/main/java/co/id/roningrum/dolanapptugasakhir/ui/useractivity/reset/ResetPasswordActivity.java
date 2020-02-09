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

package co.id.roningrum.dolanapptugasakhir.ui.useractivity.reset;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import co.id.roningrum.dolanapptugasakhir.R;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private TextInputLayout edtEmailResetPass;
    private FirebaseAuth emailAuthReset;

    private ProgressDialog pdDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_forget_password);
        edtEmailResetPass = findViewById(R.id.edt_email_reset_pass_layout);
        Button btnResetEmail = findViewById(R.id.btn_reset_page);

        pdDialog.setTitle("Memproses Akun");
        pdDialog.setMessage("Loading....");
        pdDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        emailAuthReset = FirebaseAuth.getInstance();
        btnResetEmail.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_reset_page) {
            doResetPass();
        }
    }

    private void doResetPass() {
        String emailReset = Objects.requireNonNull(edtEmailResetPass.getEditText()).getText().toString();
        if (emailReset.isEmpty()) {
            edtEmailResetPass.setError("Masukkan Email");
        } else if (!isValidEmail(emailReset)) {
            edtEmailResetPass.setError("Email tidak valid");
        } else {
            emailAuthReset.sendPasswordResetEmail(emailReset).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(ResetPasswordActivity.this, ResetSendEmailSuccessActivity.class));
                        finish();
                    }
                }
            });
        }
    }

    private boolean isValidEmail(String emailReset) {
        return Patterns.EMAIL_ADDRESS.matcher(emailReset).matches();
    }
}
