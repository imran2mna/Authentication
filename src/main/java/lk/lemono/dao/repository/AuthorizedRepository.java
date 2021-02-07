package lk.lemono.dao.repository;


import lk.lemono.dao.entity.AuthorityEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by imran on 2/7/21.
 */
public interface AuthorizedRepository extends CrudRepository<AuthorityEntity, Integer> {

}
