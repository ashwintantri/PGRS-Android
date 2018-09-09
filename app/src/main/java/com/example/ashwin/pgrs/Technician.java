package com.example.ashwin.pgrs;

public class Technician {
    String name;
    double lat,longitude;
    String department;
    String number;
    String email;

    public Technician(String name,double lat,double longitude,String department,String number,String email)
    {
        this.name = name;
        this.lat = lat;
        this.longitude = longitude;
        this.department = department;
        this.number = number;
        this.email = email;
    }

    public Technician()
    {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLat() {
        return lat;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
