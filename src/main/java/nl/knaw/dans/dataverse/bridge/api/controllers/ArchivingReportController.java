package nl.knaw.dans.dataverse.bridge.api.controllers;

import nl.knaw.dans.dataverse.bridge.db.dao.ArchivingReportDao;
import nl.knaw.dans.dataverse.bridge.db.dao.DvnTdrUserDao;
import nl.knaw.dans.dataverse.bridge.db.domain.ArchivingReport;
import nl.knaw.dans.dataverse.bridge.db.domain.DvnTdrUser;
import nl.knaw.dans.dataverse.bridge.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

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
    @ResponseBody
    public List<ArchivingReport> getAllIngestedDatasetsWithStatusArchived() {
        try {
            List<ArchivingReport> archivingReports = archivingReportDao.getAllIngestedDatasetsByStatus(Status.ARCHIVED);
            return archivingReports;
        } catch (Exception ex) {
            //todo
        }
        return null;//todo
    }

    @RequestMapping(
            value = "/get-ingested-datasets/{status}",
            method = RequestMethod.GET)
    @ResponseBody
    public List<ArchivingReport> getAllIngestedDatasetsWithStatus(@PathVariable String status) {
        try {
            List<ArchivingReport> archivingReports = archivingReportDao.getAllIngestedDatasetsByStatus(Status.valueOf(status));
            return archivingReports;
        } catch (Exception ex) {
            //todo
        }
        return null;//todo
    }

    /**
     * Retrieve the list of ArchivingReport.
     */
    @RequestMapping(
            value = "/get-all",
            method = RequestMethod.GET)
    @ResponseBody
    public List<ArchivingReport> getAll() {
        try {
            List<ArchivingReport> archivingReports = archivingReportDao.getAll();
            return archivingReports;
        } catch (Exception ex) {

        }
        return null;
    }

}
