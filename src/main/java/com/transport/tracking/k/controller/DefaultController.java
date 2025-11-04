package com.transport.tracking.k.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
//@CrossOrigin(origins = {"http://tmsportal.tema-systems.com:8081", "http://tmsportal.tema-systems.com:8082"})
@CrossOrigin(origins={"http://localhost:3000","http://localhost:8054","http://localhost:8055","https://routeplanner.centraloil.com:8054","https://routeplanner.centraloil.com:8055","http://routeplanner.centraloil.com:8055","http://routeplanner.centraloil.com:8054"})
public class DefaultController {

    @RequestMapping ("/home")
    public String getHome() {
        return "index.html";
    }
}
