package lk.lemono.comm;

/**
 * Created by imran on 2/6/21.
 */
public class VerificationResponse {
    private int processed;

    public VerificationResponse(int processed) {
        this.processed = processed;
    }

    public int getProcessed() {
        return processed;
    }

    public void setProcessed(int processed) {
        this.processed = processed;
    }
}
