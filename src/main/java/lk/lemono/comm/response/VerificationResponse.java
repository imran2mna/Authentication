package lk.lemono.comm.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by imran on 2/6/21.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerificationResponse {
    private int status;

    public VerificationResponse() {}

    public VerificationResponse(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
