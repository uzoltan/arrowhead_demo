package eu.arrowhead.arrowheaddemo.messages;


public class ReadyForChargeResponse {

    private int responseCode;
    private String responseMessage;
    private String status;

    public ReadyForChargeResponse() {
    }

    public ReadyForChargeResponse(int responseCode, String responseMessage, String status) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
