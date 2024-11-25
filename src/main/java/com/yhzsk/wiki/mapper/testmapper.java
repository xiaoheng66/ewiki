package com.yhzsk.wiki.mapper;

import com.yhzsk.wiki.domain.test;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface testmapper {

    public List<test> list();
}
