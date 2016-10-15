package eu.arrowhead.arrowheaddemo.messages;


public class ChargingResponse {

    private String chargingReqId;
    private String status;
    private Location chargerLocation;

    public ChargingResponse() {
    }

    public ChargingResponse(String chargingReqId, String status, Location chargerLocation) {
        this.chargingReqId = chargingReqId;
        this.status = status;
        this.chargerLocation = chargerLocation;
    }

    public String getChargingReqId() {
        return chargingReqId;
    }

    public void setChargingReqId(String chargingReqId) {
        this.chargingReqId = chargingReqId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Location getChargerLocation() {
        return chargerLocation;
    }

    public void setChargerLocation(Location chargerLocation) {
        this.chargerLocation = chargerLocation;
    }
}
