package eu.arrowhead.arrowheaddemo.messages;


public class ChargingResponse {

    private String chargingRequestId;
    private String occpChargePointStatus;
    private Location chargePointLocation;
    private int responseCode;
    private String responseMessage;

    public ChargingResponse() {
    }

    public ChargingResponse(String chargingRequestId, String occpChargePointStatus, Location chargePointLocation, int responseCode, String responseMessage) {
        this.chargingRequestId = chargingRequestId;
        this.occpChargePointStatus = occpChargePointStatus;
        this.chargePointLocation = chargePointLocation;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
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

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
}
