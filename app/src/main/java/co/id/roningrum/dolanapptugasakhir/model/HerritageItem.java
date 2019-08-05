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

package co.id.roningrum.dolanapptugasakhir.model;

public class HerritageItem {
    private String name_herritage;
    private String info_herritage;
    private String url_photo_herritage;

    public HerritageItem() {
    }

    public HerritageItem(String name_herritage, String info_herritage, String url_photo_herritage) {
        this.name_herritage = name_herritage;
        this.info_herritage = info_herritage;
        this.url_photo_herritage = url_photo_herritage;
    }

    public String getName_herritage() {
        return name_herritage;
    }

    public void setName_herritage(String name_herritage) {
        this.name_herritage = name_herritage;
    }

    public String getInfo_herritage() {
        return info_herritage;
    }

    public void setInfo_herritage(String info_herritage) {
        this.info_herritage = info_herritage;
    }

    public String getUrl_photo_herritage() {
        return url_photo_herritage;
    }

    public void setUrl_photo_herritage(String url_photo_herritage) {
        this.url_photo_herritage = url_photo_herritage;
    }
}
