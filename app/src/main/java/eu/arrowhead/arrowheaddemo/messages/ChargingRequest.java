package eu.arrowhead.arrowheaddemo.messages;


public class ChargingRequest {

    private String userId;
    private String EVId;
    private String latestStopTime;
    private Location location;

    public ChargingRequest() {
    }

    public ChargingRequest(String userId, String EVId, String latestStopTime, Location location) {
        this.userId = userId;
        this.EVId = EVId;
        this.latestStopTime = latestStopTime;
        this.location = location;
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

    public String getLatestStopTime() {
        return latestStopTime;
    }

    public void setLatestStopTime(String latestStopTime) {
        this.latestStopTime = latestStopTime;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


}
