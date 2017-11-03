/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.knaw.dans.dataverse.bridge.db.domain;


import nl.knaw.dans.dataverse.bridge.util.Status;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Class ArchivingReport
 * Created by Eko Indarto
 */
@Entity
@Table(name = "archiving_report", uniqueConstraints =
@UniqueConstraint(columnNames = {"dataset", "status", "dvn_tdr_id"}))
public class ArchivingReport implements Serializable {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "dvn_tdr_id", referencedColumnName = "id")
    private DvnTdrUser dvnTdrUser;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "start_ingest_time")
    private Date startIngestTime;

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "end_ingest_time")
    private Date endIngestTime;

    @Type(type = "org.hibernate.type.TextType")
    private String report;

    @NotNull
    @Column(name = "status")
    private String status;

    private String landingpage;

    private String doi;

    @NotNull
    @Column(name = "dataset")
    private String dataset;

    @NotNull
    @Column(name = "version")
    private Integer version;

    public ArchivingReport() {
    }

    public ArchivingReport(long id) {
    }

    public ArchivingReport(String dataset, Status status, int version, DvnTdrUser dvnTdrUser) {
        this.dataset = dataset;
        this.status = status.toString();
        this.setDvnTdrUser(dvnTdrUser);
        this.startIngestTime = new Date();
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStartIngestTime() {
        return startIngestTime;
    }

    public void setStartIngestTime(Date startIngestTime) {
        this.startIngestTime = startIngestTime;
    }

    public Date getEndIngestTime() {
        return endIngestTime;
    }

    public void setEndIngestTime(Date endIngestTime) {
        this.endIngestTime = endIngestTime;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getStatus() {
        if (status == null || status.isEmpty())
            return Status.NOT_ARCHIVED_YET.toString();
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLandingpage() {
        return landingpage;
    }

    public void setLandingpage(String landingpage) {
        this.landingpage = landingpage;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }


    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public DvnTdrUser getDvnTdrUser() {
        return dvnTdrUser;
    }

    public void setDvnTdrUser(DvnTdrUser dvnTdrUser) {
        this.dvnTdrUser = dvnTdrUser;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }
}
