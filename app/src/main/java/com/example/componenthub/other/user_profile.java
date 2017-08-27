package com.example.componenthub.other;

/**
 * Created by dheeraj on 22/07/17.
 */

public class user_profile {
    private String name;
    private String mobile_number;
    private String email_address;
    private String registration_number;

    public user_profile() {
      /* Blank default constructor essential for Firebase */
    }

    //Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public String getRegistration_number() {
        return registration_number;
    }

    public void setRegistration_number(String registration_number) {
        this.registration_number = registration_number;
    }
}
