package com.tarashor.web;

import org.springframework.boot.autoconfigure.web.WebMvcProperties;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import java.util.Locale;

import static org.springframework.web.bind.annotation.RequestMethod.GET;


/**
 * Created by Taras on 3/2/2017.
 */
@Controller
@RequestMapping(value="/")
public class HomeController {

    public static final String ACCEPT_LANGUAGE = "Accept-Language";

    @RequestMapping(method = GET)
    public String home(){
        return "home";
    }

    @RequestMapping("/test")
    @ResponseBody
    public String test(HttpServletRequest request, Locale locale)
    {
        LocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        Locale resolvedLocale = localeResolver.resolveLocale(request);
        String language = request.getHeader(ACCEPT_LANGUAGE);
        return ACCEPT_LANGUAGE + " = '" + language + "' locale = " + locale + " resolvedLocale = " + resolvedLocale;
    }

}
