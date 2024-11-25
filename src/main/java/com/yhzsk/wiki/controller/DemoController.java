package com.yhzsk.wiki.controller;

import com.yhzsk.wiki.domain.Demo;
import com.yhzsk.wiki.service.Demoservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DemoController {

    @Autowired
    private Demoservice demoservice;



    @GetMapping("/demo/list")
    public List<Demo> list()
    {
        return demoservice.list();
    }






}
