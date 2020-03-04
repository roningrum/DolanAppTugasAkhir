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

package co.id.roningrum.dolanapptugasakhir.model;

public class Users {
    private String uid;
    private String nama_user;
    private String password;
    private String email;
    private String photo_user;
    private String login;

    public Users() {
    }

    public Users(String uid, String nama_user, String password, String email, String photo_user, String login) {
        this.uid = uid;
        this.nama_user = nama_user;
        this.password = password;
        this.email = email;
        this.photo_user = photo_user;
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public String getNama_user() {
        return nama_user;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoto_user() {
        return photo_user;
    }

    public String getLogin() {
        return login;
    }
}
