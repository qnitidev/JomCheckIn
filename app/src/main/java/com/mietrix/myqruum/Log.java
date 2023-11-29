package com.mietrix.myqruum;

public class Log {


    private String logID;
    private String enterDate;
    private String enterTime;
    private String exitDate;
    private String exitTime;
    private String placeID;
    private String placeName;
    private String logStatus;

    public Log(String logID, String enterDate, String enterTime, String exitDate, String exitTime,String placeID, String placeName, String logStatus) {

        this.logID = logID;
        this.enterDate = enterDate;
        this.enterTime = enterTime;
        this.exitDate = exitDate;
        this.exitTime = exitTime;
        this.placeID = placeID;
        this.placeName = placeName;
        this.logStatus = logStatus;

    }

    public String getLogID() {
        return logID;
    }

    public String getEnterDate() {
        return enterDate;
    }

    public String getEnterTime() {
        return enterTime;
    }

    public String getExitDate() {
        return exitDate;
    }


    public String getExitTime() {
        return exitTime;
    }

    public String getPlaceID() {
        return placeID;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getLogStatus() {
        return logStatus;
    }
}