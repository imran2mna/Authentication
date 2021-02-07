package lk.lemono.main;

import lk.lemono.comm.common.StatCodes;
import lk.lemono.comm.request.VerificationRequest;
import lk.lemono.comm.response.VerificationResponse;
import lk.lemono.dao.entity.AuthorityEntity;
import lk.lemono.dao.entity.MobileEntity;
import lk.lemono.dao.repository.AuthorizedRepository;
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
@RequestMapping(value = "verification")
public class VerificationManager {

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private AuthorizedRepository authorizedRepository;

    @PostMapping("/enter")
    public VerificationResponse mobileNumber(@RequestBody VerificationRequest request){

        if(request.getMobile() == null || request.getDeviceID() == null) { return new VerificationResponse(StatCodes.TECHNICAL); }

        MobileEntity entity = verificationRepository.findByNumber(request.getMobile());

        if(entity == null) {
            entity = new MobileEntity();
            entity.setNumber(request.getMobile());
            entity.setEnterAttempts(1);
            entity.setResendAttempts(1);
            entity.setSubmitAttempts(0);
            entity.setDeviceID(request.getDeviceID());
            entity.setSessionID(randomKey(Config.RANDOM_KEY_SIZE));
            entity.setOtp(randomOTP(Config.RANDOM_OTP_SIZE));
        } else {

            // overwrite the previous device ID
            if(!entity.getDeviceID().equals(request.getDeviceID())) {
                entity.setDeviceID(request.getDeviceID());
                entity.setSessionID(randomKey(Config.RANDOM_KEY_SIZE));
                entity.setOtp(randomOTP(Config.RANDOM_OTP_SIZE));
                entity.setEnterAttempts(0); // set to zero - below code will adjust
            }

            // block the mobile number
            if(entity.getEnterAttempts() == StatCodes.ATTEMPTS) { return new VerificationResponse(StatCodes.BLOCKED);}
            entity.setEnterAttempts(entity.getEnterAttempts() + 1);
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
        if(request.getSessionID() == null || request.getDeviceID() == null) { return new VerificationResponse(StatCodes.TECHNICAL); }

        MobileEntity mobileEntity = verificationRepository.findByDeviceIDAndSessionID(request.getDeviceID(), request.getSessionID());
        if (mobileEntity == null) {
            return new VerificationResponse(StatCodes.BLOCKED);
        }

        if(mobileEntity.getResendAttempts() == StatCodes.RESEND_ATTEMPTS) { return new VerificationResponse(StatCodes.BLOCKED);}
        mobileEntity.setResendAttempts(mobileEntity.getResendAttempts() + 1);

        try {
            verificationRepository.save(mobileEntity);
        } catch (Exception e){
            e.printStackTrace();
            return new VerificationResponse(StatCodes.TECHNICAL);
        }
        VerificationResponse response = new VerificationResponse();
        response.setProcessed(StatCodes.SUCCESS);
        response.setSessionID(mobileEntity.getSessionID());
        return response;
    }


    @PostMapping("/submit")
    public VerificationResponse submitCode(@RequestBody VerificationRequest request){

        if(request.getSessionID() == null
                || request.getDeviceID() == null
                || request.getOtp() == null) { return new VerificationResponse(StatCodes.TECHNICAL); }

        MobileEntity entity = verificationRepository.findByDeviceIDAndSessionID(request.getDeviceID(), request.getSessionID());
        if (entity == null) {
            return new VerificationResponse(StatCodes.BLOCKED);
        }

        // happy story
        if(request.getOtp().trim().equals(entity.getOtp().trim())) {
            AuthorityEntity authEntity = new AuthorityEntity();
            authEntity.setNumber(entity.getNumber());
            authEntity.setSessionID(randomKey(Config.SESSION_KEY_SIZE));

            try {
                authorizedRepository.save(authEntity);
                verificationRepository.delete(entity);
            } catch (Exception e) {
                e.printStackTrace();
                return new VerificationResponse(StatCodes.TECHNICAL);
            }

            // actually we need to log the login in separate table, also include date
            VerificationResponse response = new VerificationResponse();
            response.setSessionID(authEntity.getSessionID());
            response.setProcessed(StatCodes.SUCCESS);
            return response;
        }

        if(entity.getSubmitAttempts() == StatCodes.RESEND_ATTEMPTS) { return new VerificationResponse(StatCodes.BLOCKED);}
        entity.setSubmitAttempts(entity.getSubmitAttempts() + 1);
        verificationRepository.save(entity);

        return new VerificationResponse(StatCodes.BLOCKED);
    }


    /*
    Random string generation
    */

    protected String randomKey(int length){
        //String elements = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*_=+-/.?<>)";
        String elements = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
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
