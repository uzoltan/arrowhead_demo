package eu.arrowhead.arrowheaddemo.messages;


public class ReadyForCharge {

    private long chargingReqId;
    private StateOfCharge stateOfCharge;

    public ReadyForCharge() {
    }

    public ReadyForCharge(long chargingReqId, StateOfCharge stateOfCharge) {
        this.chargingReqId = chargingReqId;
        this.stateOfCharge = stateOfCharge;
    }

    public long getChargingReqId() {
        return chargingReqId;
    }

    public void setChargingReqId(long chargingReqId) {
        this.chargingReqId = chargingReqId;
    }

    public StateOfCharge getStateOfCharge() {
        return stateOfCharge;
    }

    public void setStateOfCharge(StateOfCharge stateOfCharge) {
        this.stateOfCharge = stateOfCharge;
    }

    
}
