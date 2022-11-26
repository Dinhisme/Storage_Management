package com.storage.utils;

import com.storage.entity.AccountE;


public class Auth {
    public static AccountE user = null;
    public static void clear(){
        Auth.user = null;
    }
    
    public static boolean isLogin(){
        return Auth.user != null;
    }
    
    public static boolean isManager(){
        return Auth.isLogin() && user.isPosition();
    }
}
