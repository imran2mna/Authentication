package lk.lemono.main;

/**
 * Created by imran on 2/7/21.
 */
public class Config {
    //JPA
    protected static final String REPOSITORY_PATH = "lk.lemono.dao.repository";
    protected static final String ENTITY_PATH = "lk.lemono.dao.entity";

    // Key size
    public static final int RANDOM_KEY_SIZE = 20;
    public static final int SESSION_KEY_SIZE = 28;
    public static final int RANDOM_OTP_SIZE = 4;
}
