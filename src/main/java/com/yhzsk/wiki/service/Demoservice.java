package com.yhzsk.wiki.service;

import com.yhzsk.wiki.domain.Demo;

import com.yhzsk.wiki.mapper.DemoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class Demoservice {


    @Autowired
    private DemoMapper demoMapper;


    public List<Demo> list(){
        return demoMapper.selectByExample(null);
    }
}
