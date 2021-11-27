package com.huybinh2k.computerstore;

/**
 * Created by BinhBH on 10/9/2021.
 */
public class Constant {
    public static int[] arrImage= {R.drawable.ic_baseline_home_24, R.drawable.ic_baseline_store_24,
            R.drawable.ic_baseline_notifications_24, R.drawable.ic_baseline_person_24
    };
    public static int[] arrName= {R.string.home,
            R.string.category, R.string.notification, R.string.account
    };

    public static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static final String PHONE_PATTERN = "(^(0|\\+84)((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d){7}$)|(^(0|\\+84)((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d){7}$)";
    public static final String IS_REGISTER = "is_register";
    public static final String IS_FORGET_PASS = "IS_FORGET_PASS";
    public static final String OTP_HASH = "OTP_HASH";

    public static final String LOCALHOST ="http://10.0.2.2:8000/";
    public static final String API_REGISTER = LOCALHOST + "api/auth/register";
    public static final String API_LOGIN = LOCALHOST + "api/auth/login";
    public static final String API_FORGET_PASS = LOCALHOST + "api/auth/forget_password";
    public static final String API_VERIFY_ACCOUNT = LOCALHOST + "api/auth/verify_account";
    public static final String API_SEND_OTP = LOCALHOST + "api/auth/send_OTP";
    public static final String API_VERIFY_HANDLE = LOCALHOST + "api/auth/verify_handle";


    public static final String FULL_NAME = "fullname";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PASSWORD ="password";
    public static final String PASS_CONFIRM = "password_confirmation";
    public static final String OTP = "OTP";
    public static final String TOKEN_LOGIN = "token";
    public static final String USER = "user";
    public static final String IMAGE = "image";
    public static final String ID = "id";
    public static final String PARENT_ID = "parent_id";

    public static class Category{
        public static final String NAME = "asset_name";
    }

    public static class Item {
        public static final String NAME = "item_name";
        public static final String PRICE = "price";
        public static final String DISCOUNT = "promotional_price";
    }



}
