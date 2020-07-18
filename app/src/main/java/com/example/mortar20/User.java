package com.example.mortar20;

class User {

    private String userLongitude;
    private String userLatitude;
    private String userAltitude;


    public User() {
    }

    public User( String userLongitude, String userLatitude, String userAltitude) {
        this.userLongitude = userLongitude;
        this.userLatitude = userLatitude;
        this.userAltitude = userAltitude;
    }



    public void setUserLongitude(String userLongitude) {
        this.userLongitude = userLongitude;
    }

    public void setUserLatitude(String userLatitude) {
        this.userLatitude = userLatitude;
    }

    public void setUserAltitude(String userAltitude) {
        this.userAltitude = userAltitude;
    }


    public String getUserLongitude() {
        return userLongitude;
    }

    public String getUserLatitude() {
        return userLatitude;
    }

    public String getUserAltitude() {
        return userAltitude;
    }
}
