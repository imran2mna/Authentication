package lk.lemono.comm.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by imran on 2/6/21.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
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
