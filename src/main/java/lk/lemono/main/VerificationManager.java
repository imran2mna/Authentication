package lk.lemono.main;

import lk.lemono.comm.VerificationRequest;
import lk.lemono.comm.VerificationResponse;
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

    @PostMapping("/mobile")
    public VerificationResponse mobileNumber(@RequestBody VerificationRequest request){
        VerificationResponse response = new VerificationResponse();



        return response;
    }



}
