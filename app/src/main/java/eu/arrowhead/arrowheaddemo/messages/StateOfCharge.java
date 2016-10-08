package eu.arrowhead.arrowheaddemo.messages;


public class StateOfCharge {

    private double current;
    private double minTarget;

    public StateOfCharge() {
    }

    public StateOfCharge(double current, double minTarget) {
        this.current = current;
        this.minTarget = minTarget;
    }

    public double getCurrent() {
        return current;
    }

    public void setCurrent(double current) {
        this.current = current;
    }

    public double getMinTarget() {
        return minTarget;
    }

    public void setMinTarget(double minTarget) {
        this.minTarget = minTarget;
    }


}
