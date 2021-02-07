package lk.lemono.dao.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Created by imran on 2/7/21.
 */

@Entity
@Table(name = "authorized_accounts")
public class AuthorityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "number")
    @NotBlank(message = "mobile number not provided")
    @Pattern(regexp = "(\\+94|0)[0-9]{9}", message = "mobile number pattern issue")
    private String number;

    @Column(name = "session")
    @NotNull
    private String sessionID;

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

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}
