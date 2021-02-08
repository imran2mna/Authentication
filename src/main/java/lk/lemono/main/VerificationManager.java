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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
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
    public VerificationResponse mobileNumber(@RequestBody VerificationRequest request,
                                             HttpServletResponse servletResponse){

        if(request.getMobile() == null || request.getDeviceID() == null) { return new VerificationResponse(StatCodes.TECHNICAL); }

        MobileEntity mobileEntity = verificationRepository.findByNumber(request.getMobile());

        if(mobileEntity == null) {
            mobileEntity = new MobileEntity();
            mobileEntity.setNumber(request.getMobile());
            mobileEntity.setEnterAttempts(1);
            mobileEntity.setResendAttempts(1);
            mobileEntity.setSubmitAttempts(0);
            mobileEntity.setDeviceID(request.getDeviceID());
            mobileEntity.setSessionID(randomKey(Config.RANDOM_KEY_SIZE));
            mobileEntity.setOtp(randomOTP(Config.RANDOM_OTP_SIZE));
        } else {

            // overwrite the previous device ID
            if(!mobileEntity.getDeviceID().equals(request.getDeviceID())) {
                mobileEntity.setDeviceID(request.getDeviceID());
                mobileEntity.setSessionID(randomKey(Config.RANDOM_KEY_SIZE));
                mobileEntity.setOtp(randomOTP(Config.RANDOM_OTP_SIZE));
                mobileEntity.setEnterAttempts(0); // set to zero - below code will adjust
            }

            // block the mobile number
            if(mobileEntity.getEnterAttempts() == StatCodes.ATTEMPTS) { return new VerificationResponse(StatCodes.BLOCKED);}
            mobileEntity.setEnterAttempts(mobileEntity.getEnterAttempts() + 1);
        }

        try {
            verificationRepository.save(mobileEntity);
            // below send verification code to kafka
        }catch (Exception e){
            e.printStackTrace();
            return new VerificationResponse(StatCodes.TECHNICAL);
        }

        Cookie tidCookie = new Cookie(Cookies.TID, mobileEntity.getSessionID());
        tidCookie.setMaxAge(24 * 3600);
        servletResponse.addCookie(tidCookie);

        VerificationResponse response = new VerificationResponse();
        response.setProcessed(StatCodes.SUCCESS);
        return response;
    }




    @PostMapping("/resend")
    public VerificationResponse resendCode(@RequestBody VerificationRequest request,
                                           @CookieValue(name = Cookies.TID) String tid){
        if(request.getDeviceID() == null) { return new VerificationResponse(StatCodes.TECHNICAL); }

        MobileEntity mobileEntity = verificationRepository.findByDeviceIDAndSessionID(request.getDeviceID(), tid);
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
        return response;
    }


    @PostMapping("/submit")
    public VerificationResponse submitCode(@RequestBody VerificationRequest request,
                                           HttpServletResponse servletResponse,
                                           @CookieValue(name = Cookies.TID) String tid){

        if( request.getDeviceID() == null
                || request.getOtp() == null) { return new VerificationResponse(StatCodes.TECHNICAL); }

        MobileEntity mobileEntity = verificationRepository.findByDeviceIDAndSessionID(request.getDeviceID(), tid);
        if (mobileEntity == null) {
            return new VerificationResponse(StatCodes.BLOCKED);
        }

        // happy path
        if(request.getOtp().trim().equals(mobileEntity.getOtp().trim())) {
            AuthorityEntity authEntity = new AuthorityEntity();
            authEntity.setNumber(mobileEntity.getNumber());
            authEntity.setSessionID(randomKey(Config.SESSION_KEY_SIZE));

            try {
                authorizedRepository.save(authEntity);
                verificationRepository.delete(mobileEntity);
            } catch (Exception e) {
                e.printStackTrace();
                return new VerificationResponse(StatCodes.TECHNICAL);
            }

            // actually we need to log the login in separate table, also include date
            Cookie sidCookie = new Cookie(Cookies.SID, authEntity.getSessionID());
            sidCookie.setPath("/");
            sidCookie.setMaxAge(365 * 24 * 3600);
            servletResponse.addCookie(sidCookie);

            Cookie tidCookie = new Cookie(Cookies.TID, null);
            tidCookie.setMaxAge(0);
            servletResponse.addCookie(tidCookie);

            VerificationResponse response = new VerificationResponse();
            response.setProcessed(StatCodes.SUCCESS);
            return response;
        }

        if(mobileEntity.getSubmitAttempts() == StatCodes.SUBMIT_ATTEMPTS) { return new VerificationResponse(StatCodes.BLOCKED);}
        mobileEntity.setSubmitAttempts(mobileEntity.getSubmitAttempts() + 1);
        verificationRepository.save(mobileEntity);
        return new VerificationResponse(StatCodes.BLOCKED);
    }

    @PostMapping("/logout")
    public VerificationResponse logout(HttpServletResponse servletResponse,
                                           @CookieValue(name = Cookies.SID) String sid){
        AuthorityEntity authorityEntity = authorizedRepository.findBySessionID(sid);

        if (authorityEntity == null) {
            System.out.println();
            return new VerificationResponse(StatCodes.TECHNICAL);
        }

        try {
            authorizedRepository.delete(authorityEntity);
            Cookie sidCookie = new Cookie(Cookies.SID, null);
            sidCookie.setPath("/");
            sidCookie.setMaxAge(0);
            servletResponse.addCookie(sidCookie);
        } catch (Exception e) {
            e.printStackTrace();
            return new VerificationResponse(StatCodes.TECHNICAL);
        }

        return new VerificationResponse(StatCodes.SUCCESS);
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
