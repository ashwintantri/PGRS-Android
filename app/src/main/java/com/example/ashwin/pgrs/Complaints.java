package com.example.ashwin.pgrs;

import java.util.Date;

public class Complaints
{
    String dept;
    String authority;
    String date;
    String details;
    String status;
    String type;
    int upvoted;
    String emailID;
    double lat,longitude;

    public Complaints(String dept,String authority,String date,String details,String status,String type,int upvoted,String emailID,double lat,double longitude)
    {
        this.dept = dept;
        this.authority = authority;
        this.date = date;
        this.details = details;
        this.status = status;
        this.type = type;
        this.upvoted = upvoted;
        this.emailID = emailID;
        this.lat = lat;
        this.longitude = longitude;
    }
    public Complaints(String authority,String date,String details,String status,String type,int upvoted,String emailID,double lat,double longitude)
    {
        this.authority = authority;
        this.date = date;
        this.details = details;
        this.status = status;
        this.type = type;
        this.upvoted = upvoted;
        this.emailID = emailID;
        this.lat = lat;
        this.longitude = longitude;
    }

    public Complaints()
    {
    }

    public double getLat() {
        return lat;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getDate() {
        return date;
    }

    public String getAuthority() {
        return authority;
    }

    public int getUpvoted() {
        return upvoted;
    }

    public String getDetails() {
        return details;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getEmailID() {
        return emailID;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUpvoted(int upvoted) {
        this.upvoted = upvoted;
    }
}