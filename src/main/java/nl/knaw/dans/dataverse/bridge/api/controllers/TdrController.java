package nl.knaw.dans.dataverse.bridge.api.controllers;

import nl.knaw.dans.dataverse.bridge.db.dao.TdrDao;
import nl.knaw.dans.dataverse.bridge.db.domain.Tdr;
import nl.knaw.dans.dataverse.bridge.util.DvnBridgeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * The name value is converted to uppercase.
     */
    @RequestMapping(
            value = "/create",
            method = RequestMethod.POST,
            params = {"tdrName", "tdrIri"})
    @ResponseBody
    public ResponseEntity create(String tdrName, String tdrIri) {
        Tdr tdr = new Tdr(tdrName.toUpperCase(), tdrIri);
        tdrDao.create(tdr);
        return new ResponseEntity(tdr, HttpStatus.CREATED);
    }

    /**
     * Delete the Tdr with the passed id.
     */
    @RequestMapping(
            value = "/delete",
            method = RequestMethod.DELETE,
            params = {"id"})
    @ResponseBody
    public ResponseEntity<Void> delete(long id) {
        Tdr tdr = new Tdr(id);
        tdrDao.delete(tdr);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    /**
     * Retrieve the Tdr with the passed name.
     */
    @RequestMapping(
            value = "/get-by-name",
            method = RequestMethod.GET,
            params = {"name"})
    public ResponseEntity getByName(String name) {
        Tdr tdr = tdrDao.getByName(name.toUpperCase());
        if (tdr != null)
            return new ResponseEntity(tdr, HttpStatus.OK);

        return DvnBridgeHelper.emptyJsonResponse();
    }

    /**
     * Retrieve the list of Tdrs.
     */
    @RequestMapping(
            value = "/get-all",
            method = RequestMethod.GET)
    @ResponseBody
    public List<Tdr> getAll() {
        return tdrDao.getAll();
    }

}
