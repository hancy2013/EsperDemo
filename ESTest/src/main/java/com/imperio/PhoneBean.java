package com.imperio;

/**
 * Created by wxmimperio on 2015/9/18.
 */
public class PhoneBean {
    private String name;
    private String gender;
    private int age;
    private int phone;

    public PhoneBean(String name, String gender, int age, int phone) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }
}
