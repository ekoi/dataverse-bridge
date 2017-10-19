package nl.knaw.dans.dataverse.bridge.db.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by akmi on 17/05/17.
 */

@Entity
@Table(name="tdr")
public class Tdr implements Serializable {
    private static final long serialVersionUID = 1L;

    public Tdr() { }

    public Tdr(long id) {
        this.id = id;
    }

    public Tdr(String name, String iri) {
        this.name = name;
        this.iri = iri;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "iri", nullable = false)
    private String iri;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIri() {
        return iri;
    }

    public void setIri(String iri) {
        this.iri = iri;
    }

    public String getName() {
        return name;
    }

    public void setName(String tdrName) {
        this.name = name;
    }


}
