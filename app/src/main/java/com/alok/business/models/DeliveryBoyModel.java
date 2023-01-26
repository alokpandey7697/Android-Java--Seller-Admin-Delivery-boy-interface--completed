package com.alok.business.models;

public class DeliveryBoyModel {

    private String name;
    private String email;
    private String phone;
    private String address;
    private String lattitude;
    private String longitude;
    private String sid;
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }


    public  DeliveryBoyModel() {

    }

    public DeliveryBoyModel(String name, String email, String phone, String address, String lattitude, String longitude, String sid, String image) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.sid = sid;
        this.image = image;
    }


}
