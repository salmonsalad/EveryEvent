package com.example.everyevent.Login;

public class MemberInfo {
    private String name;
    private String phoneNumber;
    private String birthDay;
    private String address;
    private String category;

    public MemberInfo(String category,String name, String phoneNumber, String birthDay, String address) {
        this.category = category;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthDay = birthDay;
        this.address = address;

    }
    public String getName(){
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhoneNumber(){
        return this.phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getBirthDay(){
        return this.birthDay;
    }
    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }
    public String getAddress(){
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setCategory(String category) { this.category = category; }
    public String getCategory() { return this.category; }









}
