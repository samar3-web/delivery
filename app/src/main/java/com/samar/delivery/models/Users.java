package com.samar.delivery.models;

public class Users {
    public String name, lastName,  address, email, phone1, phone2, password,  profileUrl;

    public Users(){

    }
    public Users(String name, String email){
        this.name = name;
        this.email = email;
    }

    public Users(String name, String lastName, String address, String email, String phone1, String phone2, String password, String profileUrl) {
        this.name = name;
        this.lastName = lastName;
        this.address = address;
        this.email = email;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.password = password;
        this.profileUrl = profileUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
