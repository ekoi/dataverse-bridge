package nl.knaw.dans.dataverse.bridge.api.controllers;

import nl.knaw.dans.dataverse.bridge.db.dao.TdrDao;
import nl.knaw.dans.dataverse.bridge.db.domain.Tdr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Class TdrController
 * Created by Eko Indarto
 */
@RequestMapping("/tdr")
@Controller
public class TdrController {

    // Wire the TdrDao used inside this controller.
    @Autowired
    private TdrDao tdrDao;

    /**
     * Create a new TdrController with an auto-generated id
     * and Trust Digital Repository Name and IRI as passed
     * values.
     */
    @RequestMapping(
            value = "/create",
            method = RequestMethod.POST,
            params = {"tdrName", "tdrIri"})
    @ResponseBody
    public Tdr create(String tdrName
            , String tdrIri) {
        try {
            Tdr tdr = new Tdr(tdrName, tdrIri);
            tdrDao.create(tdr);
            return tdr;
        } catch (Exception ex) {

        }
        return null;
    }

    /**
     * Delete the Tdr with the passed id.
     */
    @RequestMapping(
            value = "/delete",
            method = RequestMethod.DELETE,
            params = {"id"})
    @ResponseBody
    public String delete(long id) {
        try {
            Tdr tdr = new Tdr(id);
            tdrDao.delete(tdr);
        } catch (Exception ex) {
            return "Error deleting the archivingRepository: " + ex.toString();
        }
        return "Tdr succesfully deleted!";
    }

    /**
     * Retrieve the Tdr with the passed name.
     */
    @RequestMapping(
            value = "/get-by-name",
            method = RequestMethod.GET,
            params = {"name"})
    @ResponseBody
    public Tdr getByName(String name) {
        try {
            Tdr tdr = tdrDao.getByName(name);
            return tdr;
        } catch (Exception ex) {

        }
        return null;
    }

    /**
     * Retrieve the list of Tdrs.
     */
    @RequestMapping(
            value = "/get-all",
            method = RequestMethod.GET)
    @ResponseBody
    public List<Tdr> getAll() {
        try {
            List<Tdr> tdrs = tdrDao.getAll();
            return tdrs;
        } catch (Exception ex) {

        }
        return null;
    }

}
