package com.example.softdeslogin.model;

public class Student {
    private String firstName;
    private String lastName;
    private String school;
    private String gradelevel;
    private String strand;
    private String address;
    private String date;
    private String email;
    private String phone;
    private String firstCourse;
    private String secondCourse;
    private String base64;
    private String Imagename;
    private String key;

    public Student() {
    }

    public Student(String firstName, String lastName, String school, String gradelevel, String address, String date, String email, String phone, String firstCourse, String secondCourse, String strand) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.school = school;
        this.gradelevel = gradelevel;
        this.address = address;
        this.date = date;
        this.email = email;
        this.phone = phone;
        this.firstCourse = firstCourse;
        this.secondCourse = secondCourse;
        this.strand = strand;

    }

    public String getStrand() { return strand; }

    public void setStrand(String strand) { this.strand = strand; }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getGradeLevel() {
        return gradelevel;
    }

    public void setGradeLevel(String gradelevel) {
        this.gradelevel = gradelevel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstCourse() {
        return firstCourse;
    }

    public void setFirstCourse(String firstCourse) {
        this.firstCourse = firstCourse;
    }

    public String getSecondCourse() {
        return secondCourse;
    }

    public void setSecondCourse(String secondCourse) {
        this.secondCourse = secondCourse;
    }

    public String getBase64() {
        return base64;
    }

    public String getImagename() {
        return Imagename;
    }

    public void setImagename(String imagename) {
        Imagename = imagename;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
