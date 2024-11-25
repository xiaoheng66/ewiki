package com.yhzsk.wiki.controller;

import com.yhzsk.wiki.req.CategoryQueryReq;
import com.yhzsk.wiki.req.CategorySaveReq;
import com.yhzsk.wiki.resp.CommonResp;
import com.yhzsk.wiki.resp.CategoryQueryResp;
import com.yhzsk.wiki.resp.PageResp;
import com.yhzsk.wiki.service.Categoryservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private Categoryservice categoryservice;



    @GetMapping("/list")
    public CommonResp list(@Valid CategoryQueryReq req)
    {
        CommonResp<PageResp<CategoryQueryResp>> resp = new CommonResp<>();
        PageResp<CategoryQueryResp> list = categoryservice.list(req);
        //success的值默认写了true  所以就不用再单独设置了
        resp.setContent(list);
        return resp;
    }

    @GetMapping("/all")
    public CommonResp all()
    {
        CommonResp<List<CategoryQueryResp>> resp = new CommonResp<>();
        List<CategoryQueryResp> list = categoryservice.all();
        //success的值默认写了true  所以就不用再单独设置了
        resp.setContent(list);
        return resp;
    }



    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody CategorySaveReq req)
    {
        CommonResp resp = new CommonResp<>();
        categoryservice.save(req);

        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable long id)
    {
        CommonResp<Object> resp = new CommonResp<>();
        categoryservice.delete(id);
        return resp;
    }

}
