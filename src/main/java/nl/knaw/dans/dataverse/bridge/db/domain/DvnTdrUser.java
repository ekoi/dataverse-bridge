package nl.knaw.dans.dataverse.bridge.db.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Class DvnTdrUser
 * Created by Eko Indarto on 17/10/17.
 */

@Entity
@Table(name = "dvn_tdr_user", uniqueConstraints =
@UniqueConstraint(columnNames = {"dvn_user", "tdr_username", "tdr_id"}))

public class DvnTdrUser implements Serializable {
    private static final long serialVersionUID = 1L;

    public DvnTdrUser() {
    }

    public DvnTdrUser(long id) {
        this.id = id;
    }

    public DvnTdrUser(String dvnUser, String dvnUserApitoken
            , String tdrUsername, String tdrPassword, Tdr tdr) {
        this.dvnUser = dvnUser;
        this.dvnUserApitoken = dvnUserApitoken;
        this.tdrUsername = tdrUsername;
        this.tdrPassword = tdrPassword;
        this.tdr = tdr;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dvn_user", nullable = false)
    private String dvnUser;

    @Column(name = "dvn_user_apitoken")
//it can be empty, but we need to decide it later. This api token is needed to dealt with permission of files.
    private String dvnUserApitoken;

    @Column(name = "tdr_username", nullable = false)
    private String tdrUsername;

    //TODO:ENCRYPTED FIELD
    @Column(name = "tdr_password", nullable = false)
    private String tdrPassword;

    @ManyToOne
    @JoinColumn(name = "tdr_id", referencedColumnName = "id", nullable = false)
    private Tdr tdr;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTdrUsername() {
        return tdrUsername;
    }

    public void setTdrUsername(String tdrUsername) {
        this.tdrUsername = tdrUsername;
    }

    public String getTdrPassword() {
        return tdrPassword;
    }

    public void setTdrPassword(String tdrPassword) {
        this.tdrPassword = tdrPassword;
    }

    public String getDvnUser() {
        return dvnUser;
    }

    public void setDvnUser(String dvnUser) {
        this.dvnUser = dvnUser;
    }

    public String getDvnUserApitoken() {
        return dvnUserApitoken;
    }

    public void setDvnUserApitoken(String dvnUserApitoken) {
        this.dvnUserApitoken = dvnUserApitoken;
    }

    public Tdr getTdr() {
        return tdr;
    }

    public void setTdr(Tdr tdr) {
        this.tdr = tdr;
    }
}
