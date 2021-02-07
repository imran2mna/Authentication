package lk.lemono.comm.response;

/**
 * Created by imran on 2/6/21.
 */
public class VerificationResponse {
    private int processed;
    private String sessionID;

    public VerificationResponse() {}

    public VerificationResponse(int processed) {
        this.processed = processed;
    }

    public int getProcessed() {
        return processed;
    }

    public void setProcessed(int processed) {
        this.processed = processed;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}
