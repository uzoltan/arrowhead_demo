package eu.arrowhead.arrowheaddemo.messages;


public class ChargingResponse {

    //TODO String ID
    private long chargingReqId;
    private String status;

    public ChargingResponse() {
    }

    public ChargingResponse(long chargingReqId, String status) {
        this.chargingReqId = chargingReqId;
        this.status = status;
    }

    public long getChargingReqId() {
        return chargingReqId;
    }

    public void setChargingReqId(long chargingReqId) {
        this.chargingReqId = chargingReqId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
