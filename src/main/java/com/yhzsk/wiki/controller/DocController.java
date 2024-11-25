package com.yhzsk.wiki.controller;

import com.yhzsk.wiki.req.DocQueryReq;
import com.yhzsk.wiki.req.DocSaveReq;
import com.yhzsk.wiki.resp.DocQueryResp;
import com.yhzsk.wiki.resp.CommonResp;
import com.yhzsk.wiki.resp.PageResp;
import com.yhzsk.wiki.service.Docservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/doc")
public class DocController {

    @Autowired
    private Docservice docservice;



    @GetMapping("/list")
    public CommonResp list(@Valid DocQueryReq req)
    {
        CommonResp<PageResp<DocQueryResp>> resp = new CommonResp<>();
        PageResp<DocQueryResp> list = docservice.list(req);
        //success的值默认写了true  所以就不用再单独设置了
        resp.setContent(list);
        return resp;
    }

    @GetMapping("/all/{ebookId}")
    public CommonResp all(@PathVariable Long ebookId)
    {
        CommonResp<List<DocQueryResp>> resp = new CommonResp<>();
        List<DocQueryResp> list = docservice.all(ebookId);
        //success的值默认写了true  所以就不用再单独设置了
        resp.setContent(list);
        return resp;
    }



    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody DocSaveReq req)
    {
        CommonResp resp = new CommonResp<>();
        docservice.save(req);

        return resp;
    }

    @DeleteMapping("/delete/{idsStr}")
    public CommonResp delete(@PathVariable String idsStr)
    {
        CommonResp resp = new CommonResp<>();

        List<String> list = Arrays.asList(idsStr.split(","));

        docservice.delete(list);
        return resp;
    }

    @GetMapping("/find-content/{id}")
    public CommonResp findContent(@PathVariable Long id)
    {
        CommonResp<String> resp = new CommonResp<>();
        String content = docservice.findContent(id);
        //success的值默认写了true  所以就不用再单独设置了
        resp.setContent(content);
        return resp;
    }

    @GetMapping("/vote/{id}")
    public CommonResp vote(@PathVariable Long id) {
        CommonResp commonResp = new CommonResp();
        docservice.vote(id);
        return commonResp;
    }

}
