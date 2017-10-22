package nl.knaw.dans.dataverse.bridge.api.controllers;

import nl.knaw.dans.dataverse.bridge.db.dao.DvnTdrUserDao;
import nl.knaw.dans.dataverse.bridge.db.dao.TdrDao;
import nl.knaw.dans.dataverse.bridge.db.domain.DvnTdrUser;
import nl.knaw.dans.dataverse.bridge.db.domain.Tdr;
import nl.knaw.dans.dataverse.bridge.util.Misc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Class DvnTdrUserController
 * Created by Eko Indarto
 */
@RequestMapping("/dvn-tdr-user")
@Controller
public class DvnTdrUserController {

    // Wire the DvnTdrUserDao used inside this controller.
    @Autowired
    private DvnTdrUserDao dvnTdrUserDao;

    // Wire the TdrDao used inside this controller.
    @Autowired
    private TdrDao tdrDao;

    /**
     * Create a new DvnTdrUserController with an auto-generated id
     * and dvnUser, dvnUserApitoken, tdrUsername and tdrPassword
     * as passed values.
     */
    @RequestMapping(
            value = "/create",
            method = RequestMethod.POST,
            params = {"dvnUser", "dvnUserApitoken", "tdrUsername", "tdrPassword", "tdrName"})
    @ResponseBody
    public DvnTdrUser create(String dvnUser, String dvnUserApitoken, String tdrUsername, String tdrPassword, String tdrName) {
        try {
            Tdr tdr = tdrDao.getByName(tdrName);
            if (tdr == null)
                return null;
            DvnTdrUser dvnTdrUser = new DvnTdrUser(dvnUser, dvnUserApitoken, tdrUsername, tdrPassword, tdr);
            dvnTdrUserDao.create(dvnTdrUser);
            return dvnTdrUser;
        } catch (Exception ex) {

        }
        return null;
    }

    /**
     * Delete the dvnTdrUser with the passed id.
     */
    @RequestMapping(
            value = "/delete",
            method = RequestMethod.DELETE,
            params = {"id"})
    @ResponseBody
    public String delete(long id) {
        try {
            DvnTdrUser dvnTdrUser = new DvnTdrUser(id);
            dvnTdrUserDao.delete(dvnTdrUser);
        } catch (Exception ex) {
            return "Error deleting the dvnTdrUser: " + ex.toString();
        }
        return "Tdr succesfully deleted!";
    }

    /**
     * Update the dvnUser, dvnUserApitoken, tdrUsername, tdrPassword,
     * tdrName for the dvnTdrUser indentified by the passed id.
     */
    @RequestMapping(
            value = "/update/{id}",
            method = RequestMethod.PUT,
            params = {"id", "dvnUser", "dvnUserApitoken", "tdrUsername", "tdrPassword"})
    @ResponseBody
    public DvnTdrUser updateName(@PathVariable int id, String dvnUser, String dvnUserApitoken
            , String tdrUsername, String tdrPassword) {
        try {
            DvnTdrUser dvnTdrUser = dvnTdrUserDao.getById(id);
            dvnTdrUser.setDvnUser(dvnUser);
            dvnTdrUser.setDvnUserApitoken(dvnUserApitoken);
            dvnTdrUser.setTdrUsername(tdrUsername);
            dvnTdrUser.setTdrPassword(tdrPassword);
            dvnTdrUserDao.update(dvnTdrUser);
            return dvnTdrUser;
        } catch (Exception ex) {
            //todo
        }
        return null;
    }

    /*
     * Retrieve the DvnTdrUser with the passed name.
     */
    @RequestMapping(
            value = "/get-by-name",
            method = RequestMethod.GET,
            params = {"dvnUser"})
    public ResponseEntity getByName(String dvnUser) {
        List<DvnTdrUser> dvnTdrUsers = dvnTdrUserDao.getByDvnUsername(dvnUser);
        if (dvnTdrUsers == null || dvnTdrUsers.isEmpty())
            return Misc.emptyJsonResponse();

        return new ResponseEntity(dvnTdrUsers, HttpStatus.OK);
    }

    /*
     * Retrieve the DvnTdrUser with the passed name.
     */
    @RequestMapping(
            value = "/get-by-name/{tdrname}",
            method = RequestMethod.GET,
            params = {"dvnUser"})
    public ResponseEntity getByNameAndTdr(@PathVariable String tdrname, String dvnUser) {
        Tdr tdr = tdrDao.getByName(tdrname);
        if (tdr == null)
            return Misc.emptyJsonResponse();

        DvnTdrUser dvnTdrUser = dvnTdrUserDao.getByDvnUserAndTdrName(dvnUser, tdr.getId());
        if (dvnTdrUser == null)
            return Misc.emptyJsonResponse();
        return new ResponseEntity(dvnTdrUser, HttpStatus.OK);
    }

    /*
     * Retrieve the DvnTdrUser with the passed tdr.
     */
    @RequestMapping(
            value = "/get-by-tdr",
            method = RequestMethod.GET,
            params = {"tdrname"})
    public ResponseEntity getByTdr(String tdrname) {

        Tdr tdr = tdrDao.getByName(tdrname);
        if (tdr == null)
            return Misc.emptyJsonResponse();

        List<DvnTdrUser> dvnTdrUsers = dvnTdrUserDao.getByTdrName(tdr);
        if (dvnTdrUsers == null || dvnTdrUsers.isEmpty())
            return Misc.emptyJsonResponse();

        return new ResponseEntity(dvnTdrUsers, HttpStatus.OK);

    }

    /**
     * Retrieve the list of DvnTdrUser.
     */
    @RequestMapping(
            value = "/get-all",
            method = RequestMethod.GET)
    @ResponseBody
    public List<DvnTdrUser> getAll() {
        return dvnTdrUserDao.getAll();
    }
}
