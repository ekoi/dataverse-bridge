package nl.knaw.dans.dataverse.bridge.api.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Class MainController
 * Created by Eko Indarto
 */
@Controller
@RequestMapping(method = RequestMethod.GET)
public class MainController {

    @RequestMapping("/")
    @ResponseBody
    public String index() {
        return "Dataverse Bridge build based on the following technology: SWORD 2 + JPA + Hibernate + HSQLDB (Encrypted) + Swagger with Spring Boot App started!";
    }

}
