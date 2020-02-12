/*
 * Copyright 2020 RONINGRUM. All rights reserved.
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

package co.id.roningrum.dolanapptugasakhir.ui.homeactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import co.id.roningrum.dolanapptugasakhir.R;
import co.id.roningrum.dolanapptugasakhir.ui.useractivity.login.SignInOptionActivity;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class WelcomingScreenActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String SHOWCASE_ID = "1";
    private Button btnWelcomeScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcoming_screen);
        btnWelcomeScreen = findViewById(R.id.btn_welcome_screen);
        btnWelcomeScreen.setOnClickListener(this);
        initialTutorApp();

    }

    private void initialTutorApp() {
        new MaterialShowcaseView.Builder(this)
                .setTarget(btnWelcomeScreen)
                .setTitleText("Klik tombol ini untuk memulai")
                .setDismissText("Ok")
                .setDelay(500)
                .singleUse(SHOWCASE_ID)
                .show();

    }

    @Override
    public void onClick(View v) {
        Intent welcomeIntent = new Intent(this, SignInOptionActivity.class);
        startActivity(welcomeIntent);
    }
}
