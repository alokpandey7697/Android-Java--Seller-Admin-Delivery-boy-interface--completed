package com.alok.business.models;

public class AdminOrders {

    private String name;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String date;
    private String time;
    private String totalAmount;
    private String lattitude;
    private String longitude;
    private String sid;
    private String uid;
    private String deliveryBoy;
    private String deliCharge;

    public String getDeliCharge() {
        return deliCharge;
    }

    public void setDeliCharge(String deliCharge) {
        this.deliCharge = deliCharge;
    }


    public String getDeliveryBoy() {
        return deliveryBoy;
    }

    public void setDeliveryBoy(String deliveryBoy) {
        this.deliveryBoy = deliveryBoy;
    }

    public String getDeliBoy() {
        return deliBoy;
    }

    public void setDeliBoy(String deliBoy) {
        this.deliBoy = deliBoy;
    }

    private String deliBoy;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
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

    public AdminOrders()
    {}

    public AdminOrders(String name, String phone, String address, String city, String state, String date, String time, String totalAmount, String uid, String sid, String deliBoy, String deliveryBoy, String deliCharge) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.date = date;
        this.time = time;
        this.totalAmount = totalAmount;
        this.sid=sid;
        this.uid = uid;
        this.deliBoy = deliBoy;
        this.deliveryBoy = deliveryBoy;
        this.deliCharge = deliCharge;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
