package nl.knaw.dans.dataverse.bridge.api.controllers;

import nl.knaw.dans.dataverse.bridge.db.dao.ArchivingReportDao;
import nl.knaw.dans.dataverse.bridge.db.dao.DvnTdrUserDao;
import nl.knaw.dans.dataverse.bridge.db.dao.TdrDao;
import nl.knaw.dans.dataverse.bridge.db.domain.ArchivingReport;
import nl.knaw.dans.dataverse.bridge.db.domain.DvnTdrUser;
import nl.knaw.dans.dataverse.bridge.db.domain.Tdr;
import nl.knaw.dans.dataverse.bridge.util.EmptyJsonResponse;
import nl.knaw.dans.dataverse.bridge.util.Misc;
import nl.knaw.dans.dataverse.bridge.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Class ArchivingReportController
 * Created by Eko Indarto
 */
@RequestMapping("/archiving-report")
@Controller
public class ArchivingReportController {

    // Wire the ArchivingReportDao used inside this controller.
    @Autowired
    private ArchivingReportDao archivingReportDao;

    @Autowired
    private DvnTdrUserDao dvnTdrUserDao;

    @Autowired
    private TdrDao tdrDao;

    /**
     * Create a new archivingReport with an auto-generated id and dataset, status,
     * version and dvnTdrUserId as passed values.
     */
    @RequestMapping(
            value = "/create",
            method = RequestMethod.POST,
            params = {"hdl", "status", "version", "dvnTdrUserId"})

    public ArchivingReport create(String hdl, String status, int version, int dvnTdrUserId) {
        ArchivingReport ar = null;
        try {
            DvnTdrUser dvnTdrUser = dvnTdrUserDao.getById(dvnTdrUserId);
            ar = new ArchivingReport(hdl, Status.valueOf(status), version, dvnTdrUser);
            archivingReportDao.create(ar);
        } catch (Exception ex) {
            //return "Error creating the archivingRepository: " + ex.toString();
        }
        return ar;
    }

    /**
     * Delete the archivingReport with the passed id.
     */
    @RequestMapping(
            value = "/delete",
            method = RequestMethod.DELETE,
            params = {"id"})
    @ResponseBody
    public String delete(long id) {
        try {
            ArchivingReport archivingReport = new ArchivingReport(id);
            archivingReportDao.delete(archivingReport);
        } catch (Exception ex) {
            return "Error deleting the archivingReport: " + ex.toString();
        }
        return "ArchivingReport succesfully deleted!";
    }

    //@RequestParam(value="name", required=false, defaultValue="Stranger")

    /**
     * Update the doi, status, landingpage and the report for the archivingReport indentified by the passed id.
     */
    @RequestMapping(
            value = "/update",
            method = RequestMethod.PUT,
            params = {"id", "doi", "status", "landingpage", "report"})
    @ResponseBody
    public String updateName(long id, String doi, String status, String landingpage, String report) {
        try {
            ArchivingReport archivingReport = archivingReportDao.getById(id);
            archivingReport.setDoi(doi);
            archivingReport.setStatus(status);
            archivingReport.setLandingpage(landingpage);
            archivingReport.setReport(report);
            archivingReport.setEndIngestTime(new Date());
            archivingReportDao.update(archivingReport);
        } catch (Exception ex) {
            return "Error updating the archivingReport: " + ex.toString();
        }
        return "ArchivingReport succesfully updated!";
    }

    @RequestMapping(
            value = "/get-ingested-datasets/archived",
            method = RequestMethod.GET)
    public ResponseEntity getAllIngestedDatasetsWithStatusArchived() {

        List<ArchivingReport> archivingReports = archivingReportDao.getAllIngestedDatasetsByStatus(Status.ARCHIVED);
        if (archivingReports != null && !archivingReports.isEmpty())
            return new ResponseEntity(archivingReports, HttpStatus.OK);

        return Misc.emptyJsonResponse();
    }

    @RequestMapping(
            value = "/get-ingested-datasets/{status}",
            method = RequestMethod.GET)
    public ResponseEntity getAllIngestedDatasetsWithStatus(@PathVariable String status) {
        Optional<Status> givenStatus = Misc.valueOf(Status.class, status);
        if (givenStatus.isPresent()) {
            List<ArchivingReport> archivingReports = archivingReportDao.getAllIngestedDatasetsByStatus(Status.valueOf(status));
            if (archivingReports != null && !archivingReports.isEmpty())
                return new ResponseEntity(archivingReports, HttpStatus.OK);
        }

        return Misc.emptyJsonResponse();
    }

    /**
     * Retrieve the list of ArchivingReport.
     */
    @RequestMapping(
            value = "/get-repot-by-dataset-dvnuser/{tdrname}",
            method = RequestMethod.GET,
            params = {"dataset", "dvnUser"})
    public ResponseEntity getArchivingReportByDatasetDvnUserAndTdr(@PathVariable String tdrname, String dvnUser, String dataset) {

        Tdr tdr = tdrDao.getByName(tdrname);
        if (tdr == null)
            return Misc.emptyJsonResponse();

        DvnTdrUser dvnTdrUser = dvnTdrUserDao.getByDvnUserAndTdrName(dvnUser, tdr.getId());
        if (dvnTdrUser == null)
            return Misc.emptyJsonResponse();

        ArchivingReport archivingReport = archivingReportDao.findByDatasetAndStatusAndDvnTdrUserId(dataset, Status.ARCHIVED, dvnTdrUser);
        if (archivingReport == null)
            return Misc.emptyJsonResponse();

        return new ResponseEntity(archivingReport, HttpStatus.OK);
    }

    /**
     * Retrieve the list of ArchivingReport.
     * It retrieves only on object since dataset can only be archived ones in every tdr
     */
    @RequestMapping(
            value = "/get-report-by-dataset/{tdrname}",
            method = RequestMethod.GET,
            params = {"dataset"})
    public ResponseEntity getArchivingReportByDatasetAndTdr(@PathVariable String tdrname, String dataset) {
        Tdr tdr = tdrDao.getByName(tdrname);
        if (tdr == null)
            return Misc.emptyJsonResponse();

        List<DvnTdrUser> dvnTdrUsers = dvnTdrUserDao.getByTdrName(tdr);
        if (dvnTdrUsers == null || dvnTdrUsers.isEmpty())
            return Misc.emptyJsonResponse();

        ArchivingReport archivingReport = archivingReportDao.findByDatasetDvnTdrUserIds(dataset, dvnTdrUsers);
        if (archivingReport == null)
            return Misc.emptyJsonResponse();

        return new ResponseEntity(archivingReport, HttpStatus.OK);
    }

    /**
     * Retrieve the list of ArchivingReport.
     */
    @RequestMapping(
            value = "/get-all",
            method = RequestMethod.GET)
    @ResponseBody
    public List<ArchivingReport> getAll() {
            List<ArchivingReport> archivingReports = archivingReportDao.getAll();
            return archivingReports;
    }
}
