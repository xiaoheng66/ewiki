package com.yhzsk.wiki.service;

import com.yhzsk.wiki.domain.test;
import com.yhzsk.wiki.mapper.testmapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class testservice {

    @Autowired
    private testmapper tm;

    public List<test> list()
    {
        return tm.list();
    }
}
