package eu.arrowhead.arrowheaddemo.messages;


public class ChargingRequest {

    private String userId;
    private String evId;
    private Location location;
    private String chargerId;

    public ChargingRequest() {
    }

    public ChargingRequest(String userId, String evId, Location location, String chargerId) {
        this.userId = userId;
        this.evId = evId;
        this.location = location;
        this.chargerId = chargerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEvId() {
        return evId;
    }

    public void setEvId(String evId) {
        this.evId = evId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getChargerId() {
        return chargerId;
    }

    public void setChargerId(String chargerId) {
        this.chargerId = chargerId;
    }
}
