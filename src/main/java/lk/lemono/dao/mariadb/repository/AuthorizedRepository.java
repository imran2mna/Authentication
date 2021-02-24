package lk.lemono.dao.mariadb.repository;


import lk.lemono.dao.mariadb.entity.AuthorityEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by imran on 2/7/21.
 */
public interface AuthorizedRepository extends CrudRepository<AuthorityEntity, Integer> {
    public AuthorityEntity findBySessionID(String sessionID);

}
