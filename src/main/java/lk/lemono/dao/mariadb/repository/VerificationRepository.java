package lk.lemono.dao.mariadb.repository;

import lk.lemono.dao.mariadb.entity.MobileEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by imran on 2/7/21.
 */
public interface VerificationRepository extends CrudRepository<MobileEntity, Integer> {
    MobileEntity findByNumberAndDeviceID(String number, String deviceID);
    MobileEntity findByNumber(String number);
    MobileEntity findBySessionID(String sessionID);
    MobileEntity findByDeviceIDAndSessionID(String deviceID, String sessionID);
}
