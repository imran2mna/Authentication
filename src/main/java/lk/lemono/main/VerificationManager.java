package lk.lemono.main;

import lk.lemono.comm.common.StatCodes;
import lk.lemono.comm.request.VerificationRequest;
import lk.lemono.comm.response.VerificationResponse;
import lk.lemono.dao.entity.MobileEntity;
import lk.lemono.dao.repository.VerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Random;

/**
 * Created by imran on 2/6/21.
 */
@Controller
@ResponseBody
@RequestMapping(value = "verify")
public class VerificationManager {

    @Autowired
    private VerificationRepository verificationRepository;

    @PostMapping("/mobile")
    public VerificationResponse mobileNumber(@RequestBody VerificationRequest request){

        if(request.getMobile() == null || request.getDeviceID() == null) { return new VerificationResponse(StatCodes.TECHNICAL); }

        MobileEntity entity = verificationRepository.findByNumber(request.getMobile());

        if(entity == null) {
            entity = new MobileEntity();
            entity.setNumber(request.getMobile());
            entity.setNoAttempts(1);
            entity.setDeviceID(request.getDeviceID());
            entity.setSessionID(randomKey(Config.RANDOM_KEY_SIZE));
            entity.setOtp(randomOTP(Config.RANDOM_OTP_SIZE));
        } else {

            // overwrite the previous device ID
            if(!entity.getDeviceID().equals(request.getDeviceID())) {
                entity.setDeviceID(request.getDeviceID());
                entity.setSessionID(randomKey(Config.RANDOM_KEY_SIZE));
                entity.setOtp(randomOTP(Config.RANDOM_OTP_SIZE));
                entity.setNoAttempts(0); // set to zero - below code will adjust
            }

            // block the mobile number
            if(entity.getNoAttempts() == StatCodes.ATTEMPTS) { return new VerificationResponse(StatCodes.BLOCKED);}
            entity.setNoAttempts(entity.getNoAttempts() + 1);
        }

        try {
            verificationRepository.save(entity);
            // below send verification code to kafka
        }catch (Exception e){
            e.printStackTrace();
            return new VerificationResponse(StatCodes.TECHNICAL);
        }

        VerificationResponse response = new VerificationResponse();
        response.setProcessed(StatCodes.SUCCESS);
        response.setSessionID(entity.getSessionID());
        return response;
    }

    @PostMapping("/resend")
    public VerificationResponse resendCode(@RequestBody VerificationRequest request){
        System.out.println(request.getMobile());

        return new VerificationResponse(StatCodes.SUCCESS);
    }



    protected String randomKey(int length){
        String elements = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*_=+-/.?<>)";
        char[] key = new char[length];
        Random random = new Random();

        for(int i=0; i<length; i++) {
            key[i] = elements.charAt(random.nextInt(elements.length()));
        }
        return new String(key);
    }

    protected String randomOTP(int length){
        String elements = "0123456789";
        char[] key = new char[length];
        Random random = new Random();

        for(int i=0; i<length; i++) {
            key[i] = elements.charAt(random.nextInt(elements.length()));
        }
        return new String(key);
    }

}
