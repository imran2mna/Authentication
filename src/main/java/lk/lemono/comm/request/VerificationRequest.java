package lk.lemono.comm.request;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by imran on 2/6/21.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerificationRequest {
    private String mobile;
    private String deviceID;
    private String sessionID;
    private String otp;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
