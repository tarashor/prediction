package com.tarashor.web;

import com.tarashor.data.IStatisticsRepository;
import com.tarashor.models.StatisticItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

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
