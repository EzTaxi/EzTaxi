package com.example.eztaxi;

public class Users {
    String userName,number,email,pass, latitude, longitude, TaxiPlateNumber, userType, address,request_status;
    double points;


    public Users(){

    }
    public Users(String userName, String number, String userType, double points){
        this.userName = userName;
        this.number = number;
        this.userType = userType;
        this.points = points;

    }
    public Users(String userName, String number, String email, String pass ) {
        this.userName = userName;
        this.number = number;
        this.email = email;
        this.pass = pass;
    }

    public Users(String userName, String number, String email, String pass, String userType) {
        this.userName = userName;
        this.number = number;
        this.email = email;
        this.pass = pass;
        this.userType = userType;
    }


    public Users (String userName, String number, String email, String pass, String TaxiPlateNumber, String userType) {
        this.userName = userName;
        this.number = number;
        this.email = email;
        this.pass = pass;
        this.TaxiPlateNumber = TaxiPlateNumber;
        this.userType = userType;
    }

    public Users (String userName, String number, String email, String pass , String userType, double points){
        this.userName = userName;
        this.number = number;
        this.email = email;
        this.pass = pass;
        this.userType = userType;
        this.points = points;
    }
    public Users(String request_status){
        this.request_status = request_status;
    }
    public String getUserName() {
        return userName;
    }
    public String getNumber() {
        return number;
    }
    public String getEmail() {
        return email;
    }
    public String getPass() {
        return pass;
    }
    public String getTaxiPlateNumber() {
        return TaxiPlateNumber;
    }
    public String getLatitude() {
        return latitude;
    }
    public String getLongitude() {
        return longitude;
    }
    public String getUserType(){
        return userType;
    }
    public String getAddress(){
        return address;
    }
    public String getRequest_status(){
        return request_status;
    }
    public Double getPoints(){return points;}

}