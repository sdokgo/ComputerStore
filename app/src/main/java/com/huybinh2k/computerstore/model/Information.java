package com.huybinh2k.computerstore.model;

public class Information {
    private int Id;
    private String Name;
    private String FullName;
    private String Email;
    private String Number;
    private String address;
    private int Province_id;
    private int District_id;
    private int Ward_id;

    public Information(int id, String name, String fullName, String email, String number, int province_id, int district_id, int ward_id) {
        Id = id;
        Name = name;
        FullName = fullName;
        Email = email;
        Number = number;
        Province_id = province_id;
        District_id = district_id;
        Ward_id = ward_id;
    }

    public Information(String name, String fullName, String email, String number, int province_id, int district_id, int ward_id) {
        Name = name;
        FullName = fullName;
        Email = email;
        Number = number;
        Province_id = province_id;
        District_id = district_id;
        Ward_id = ward_id;
    }

    public Information(int id, String name, String fullName, String email, String number) {
        Id = id;
        Name = name;
        FullName = fullName;
        Email = email;
        Number = number;
    }

    public Information(int id, String name, String fullName, String email, String number, String address) {
        Id = id;
        Name = name;
        FullName = fullName;
        Email = email;
        Number = number;
        this.address = address;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public int getProvince_id() {
        return Province_id;
    }

    public void setProvince_id(int province_id) {
        Province_id = province_id;
    }

    public int getDistrict_id() {
        return District_id;
    }

    public void setDistrict_id(int district_id) {
        District_id = district_id;
    }

    public int getWard_id() {
        return Ward_id;
    }

    public void setWard_id(int ward_id) {
        Ward_id = ward_id;
    }

    public String getAddress() {
        return address;
    }
}