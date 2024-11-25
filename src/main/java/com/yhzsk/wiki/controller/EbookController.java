package com.yhzsk.wiki.controller;

import com.yhzsk.wiki.req.EbookQueryReq;
import com.yhzsk.wiki.req.EbookSaveReq;
import com.yhzsk.wiki.resp.CommonResp;
import com.yhzsk.wiki.resp.EbookResp;
import com.yhzsk.wiki.resp.PageResp;
import com.yhzsk.wiki.service.Ebookservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Autowired
    private Ebookservice ebookservice;



    @GetMapping("/list")
    public CommonResp list(@Valid EbookQueryReq req)
    {
        CommonResp<PageResp<EbookResp>> resp = new CommonResp<>();
        PageResp<EbookResp> list = ebookservice.list(req);
        //success的值默认写了true  所以就不用再单独设置了
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody EbookSaveReq req)
    {
        CommonResp resp = new CommonResp<>();
        ebookservice.save(req);

        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable long id)
    {
        CommonResp<Object> resp = new CommonResp<>();
        ebookservice.delete(id);
        return resp;
    }

}
