package com.storage.entity;

/**
 *
 * @author Phùng Quốc Vinh
 */
public class AccountE {

    String userName, fullName, passWord, phone, email;
    boolean position;

    public AccountE() {
    }

    public AccountE(String userName, String fullName, String passWord, String phone, String email, boolean position) {
        this.userName = userName;
        this.fullName = fullName;
        this.passWord = passWord;
        this.phone = phone;
        this.email = email;
        this.position = position;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isPosition() {
        return position;
    }

    public void setPosition(boolean position) {
        this.position = position;
    }

}
