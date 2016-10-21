package eu.arrowhead.arrowheaddemo.messages;


public class ReadyForCharge {

    private String chargingRequestId;
    private StateOfCharge stateOfCharge;
    private String latestStopTime;

    public ReadyForCharge() {
    }

    public ReadyForCharge(String chargingRequestId, StateOfCharge stateOfCharge, String latestStopTime) {
        this.chargingRequestId = chargingRequestId;
        this.stateOfCharge = stateOfCharge;
        this.latestStopTime = latestStopTime;
    }

    public String getChargingRequestId() {
        return chargingRequestId;
    }

    public void setChargingRequestId(String chargingRequestId) {
        this.chargingRequestId = chargingRequestId;
    }

    public StateOfCharge getStateOfCharge() {
        return stateOfCharge;
    }

    public void setStateOfCharge(StateOfCharge stateOfCharge) {
        this.stateOfCharge = stateOfCharge;
    }

    public String getLatestStopTime() {
        return latestStopTime;
    }

    public void setLatestStopTime(String latestStopTime) {
        this.latestStopTime = latestStopTime;
    }
}
