package com.example.mortar20;

class User {

    private String userLongitude;
    private String userLatitude;
    private String userAltitude;
    private String userIsAlive;


    public User() {
    }

    public User( String userLongitude, String userLatitude,
                 String userAltitude, String userIsAlive) {
        this.userLongitude = userLongitude;
        this.userLatitude = userLatitude;
        this.userAltitude = userAltitude;
        this.userIsAlive = userIsAlive;
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

    public void setUserIsAlive(String userIsAlive) {
        this.userIsAlive = userIsAlive;
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

    public String getUserIsAlive() {
        return userIsAlive;
    }
}
