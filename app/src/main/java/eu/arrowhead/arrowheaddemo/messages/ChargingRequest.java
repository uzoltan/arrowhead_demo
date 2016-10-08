package eu.arrowhead.arrowheaddemo.messages;


import java.util.Date;

public class ChargingRequest {

    private String userId;
    private String EVId;
    private Date latestStopTime;
    private Location location;

    public ChargingRequest() {
    }

    public ChargingRequest(String EVId, Date latestStopTime, Location location, String userId) {
        this.EVId = EVId;
        this.latestStopTime = latestStopTime;
        this.location = location;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEVId() {
        return EVId;
    }

    public void setEVId(String EVId) {
        this.EVId = EVId;
    }

    public Date getLatestStopTime() {
        return latestStopTime;
    }

    public void setLatestStopTime(Date latestStopTime) {
        this.latestStopTime = latestStopTime;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


}
