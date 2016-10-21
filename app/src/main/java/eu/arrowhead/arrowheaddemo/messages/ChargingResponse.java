package eu.arrowhead.arrowheaddemo.messages;


public class ChargingResponse {

    private String chargingRequestId;
    private String occpChargePointStatus;
    private Location chargePointLocation;

    public ChargingResponse() {
    }

    public ChargingResponse(String chargingRequestId, String occpChargePointStatus, Location chargePointLocation) {
        this.chargingRequestId = chargingRequestId;
        this.occpChargePointStatus = occpChargePointStatus;
        this.chargePointLocation = chargePointLocation;
    }

    public String getChargingRequestId() {
        return chargingRequestId;
    }

    public void setChargingRequestId(String chargingRequestId) {
        this.chargingRequestId = chargingRequestId;
    }

    public String getOccpChargePointStatus() {
        return occpChargePointStatus;
    }

    public void setOccpChargePointStatus(String occpChargePointStatus) {
        this.occpChargePointStatus = occpChargePointStatus;
    }

    public Location getChargePointLocation() {
        return chargePointLocation;
    }

    public void setChargePointLocation(Location chargePointLocation) {
        this.chargePointLocation = chargePointLocation;
    }
}
