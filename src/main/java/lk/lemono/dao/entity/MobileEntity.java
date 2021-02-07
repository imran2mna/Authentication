package lk.lemono.dao.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Created by imran on 2/7/21.
 */
@Entity
@Table(name = "mobile_accounts")
public class MobileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "number")
    @NotBlank(message = "mobile number not provided")
    @Pattern(regexp = "(\\+94|0)[0-9]{9}", message = "mobile number pattern issue")
    private String number;

    @Column(name = "enter_attempts")
    @NotNull
    private Integer enterAttempts;

    @Column(name = "resend_attempts")
    @NotNull
    private Integer resendAttempts;

    @Column(name = "submit_attempts")
    @NotNull
    private Integer submitAttempts;

    @Column(name = "device_id")
    @NotNull
    private String deviceID;

    @Column(name = "session")
    @NotNull
    private String sessionID;

    @Column(name = "otp")
    @NotNull
    private String otp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getEnterAttempts() {
        return enterAttempts;
    }

    public void setEnterAttempts(Integer enterAttempts) {
        this.enterAttempts = enterAttempts;
    }

    public Integer getResendAttempts() {
        return resendAttempts;
    }

    public void setResendAttempts(Integer resendAttempts) {
        this.resendAttempts = resendAttempts;
    }

    public Integer getSubmitAttempts() {
        return submitAttempts;
    }

    public void setSubmitAttempts(Integer submitAttempts) {
        this.submitAttempts = submitAttempts;
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
