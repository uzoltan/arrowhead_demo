package eu.arrowhead.arrowheaddemo.messages;


public class ReadyForCharge {

    private String chargingReqId;
    private StateOfCharge stateOfCharge;

    public ReadyForCharge() {
    }

    public ReadyForCharge(String chargingReqId, StateOfCharge stateOfCharge) {
        this.chargingReqId = chargingReqId;
        this.stateOfCharge = stateOfCharge;
    }

    public String getChargingReqId() {
        return chargingReqId;
    }

    public void setChargingReqId(String chargingReqId) {
        this.chargingReqId = chargingReqId;
    }

    public StateOfCharge getStateOfCharge() {
        return stateOfCharge;
    }

    public void setStateOfCharge(StateOfCharge stateOfCharge) {
        this.stateOfCharge = stateOfCharge;
    }


}
