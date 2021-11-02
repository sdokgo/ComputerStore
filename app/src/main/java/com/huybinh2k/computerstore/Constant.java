package com.huybinh2k.computerstore;

/**
 * Created by BinhBH on 10/9/2021.
 */
public class Constant {
    //TODO BinhBH Cần sửa lại khi có item đúng.
    public static int[] arrImage= {R.drawable.ic_baseline_home_24, R.drawable.ic_baseline_store_24,
            R.drawable.ic_baseline_notifications_24, R.drawable.ic_baseline_person_24
    };
    public static int[] arrName= {R.string.home,
            R.string.category, R.string.notification, R.string.account
    };

    public static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static final String IS_REGISTER = "is_register";
    public static final String IS_FORGET_PASS = "IS_FORGET_PASS";
    public static final String OTP_HASH = "OTP_HASH";

    public static final String LOCALHOST ="http://10.0.2.2:8000";
    public static final String API_REGISTER = LOCALHOST + "/api/auth/register";
    public static final String API_LOGIN = LOCALHOST + "/api/auth/login";
    public static final String API_FORGET_PASS = LOCALHOST + "/api/auth/forget_password";
    public static final String API_VERIFY_ACCOUNT = LOCALHOST + "/api/auth/verify_account";
    public static final String API_SEND_OTP = LOCALHOST + "/api/auth/send_OTP";
    public static final String API_VERIFY_HANDLE = LOCALHOST + "/api/auth/verify_handle";



    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PASSWORD ="password";
    public static final String PASS_CONFIRM = "password_confirmation";
    public static final String OTP = "OTP";



}
