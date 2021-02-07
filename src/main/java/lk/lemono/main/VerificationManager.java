package lk.lemono.main;

import lk.lemono.comm.VerificationRequest;
import lk.lemono.comm.VerificationResponse;
import lk.lemono.dao.entity.MobileEntity;
import lk.lemono.dao.repository.VerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
        VerificationResponse response = new VerificationResponse(0);

        if(request.getMobile() == null) { return new VerificationResponse(1); }

        MobileEntity entity = verificationRepository.findByNumber(request.getMobile());

        if(entity == null) {
            entity = new MobileEntity();
            entity.setNumber(request.getMobile());
            entity.setNoAttempts(1);
        } else {
            if(entity.getNoAttempts() == 3) { return new VerificationResponse(2);}
            entity.setNoAttempts(entity.getNoAttempts() + 1);
        }

        try {
            verificationRepository.save(entity);
        }catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }



}
