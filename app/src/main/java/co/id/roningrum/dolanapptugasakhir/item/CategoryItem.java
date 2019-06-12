package co.id.roningrum.dolanapptugasakhir.item;

public class CategoryItem {
    public String name_tourism;
    public String location_tourism;
    public String info_tourism;
    public String telepon;
    public String url_photo;
    public float lat_location_tourism;
    public float lng_location_tourism;

    public CategoryItem() {
        //constructor untuk panggilan ke DataSnapshot.getValue
    }

    public CategoryItem(String name_tourism, String location_tourism, String info_tourism, String telepon, String url_photo, float lat_location_tourism, float lng_location_tourism) {
        this.name_tourism = name_tourism;
        this.location_tourism = location_tourism;
        this.info_tourism = info_tourism;
        this.telepon = telepon;
        this.url_photo = url_photo;
        this.lat_location_tourism = lat_location_tourism;
        this.lng_location_tourism = lng_location_tourism;
    }

    public String getName_tourism() {
        return name_tourism;
    }

    public void setName_tourism(String name_tourism) {
        this.name_tourism = name_tourism;
    }

    public String getLocation_tourism() {
        return location_tourism;
    }

    public void setLocation_tourism(String location_tourism) {
        this.location_tourism = location_tourism;
    }

    public String getInfo_tourism() {
        return info_tourism;
    }

    public void setInfo_tourism(String info_tourism) {
        this.info_tourism = info_tourism;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getUrl_photo() {
        return url_photo;
    }

    public void setUrl_photo(String url_photo) {
        this.url_photo = url_photo;
    }

    public float getLat_location_tourism() {
        return lat_location_tourism;
    }

    public void setLat_location_tourism(float lat_location_tourism) {
        this.lat_location_tourism = lat_location_tourism;
    }

    public float getLng_location_tourism() {
        return lng_location_tourism;
    }

    public void setLng_location_tourism(float lng_location_tourism) {
        this.lng_location_tourism = lng_location_tourism;
    }
}
