package com.tarashor.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;


/**
 * Created by Taras on 3/2/2017.
 */
@Controller
@RequestMapping(value="/")
public class HomeController {

    @RequestMapping(method = GET)
    public String home(){
        return "home";
    }



}
