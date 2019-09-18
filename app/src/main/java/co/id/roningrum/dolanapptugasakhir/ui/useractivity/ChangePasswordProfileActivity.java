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

package co.id.roningrum.dolanapptugasakhir.ui.useractivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import co.id.roningrum.dolanapptugasakhir.R;

public class ChangePasswordProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "UpdatePassword";
    private EditText edtChangePassword;

    private FirebaseAuth changePasswordAuth;
    private DatabaseReference dbProfileref;
    private FirebaseUser changePasswordUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_profile);
        Button btnSaveChangePassword = findViewById(R.id.btn_save_change_password);
        edtChangePassword = findViewById(R.id.edt_change_password);

        changePasswordAuth = FirebaseAuth.getInstance();
        changePasswordUser = changePasswordAuth.getCurrentUser();
        dbProfileref = FirebaseDatabase.getInstance().getReference("Users");
        btnSaveChangePassword.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_save_change_password) {
            saveChangePassword();
        }
    }

    private void saveChangePassword() {
        if (changePasswordUser != null) {
            final String uid = changePasswordUser.getUid();
            changePasswordUser.updatePassword(edtChangePassword.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        dbProfileref.child(uid).child("password").setValue(edtChangePassword.getText().toString().trim());
                        changePasswordAuth.signOut();
                        startActivity(new Intent(ChangePasswordProfileActivity.this, SignInOptionActivity.class));
                        finish();
                    } else {
                        Log.e(TAG, "" + task.getException());
                    }
                }
            });
        }
    }
}
