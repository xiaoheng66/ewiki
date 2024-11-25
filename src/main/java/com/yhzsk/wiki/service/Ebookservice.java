package com.yhzsk.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yhzsk.wiki.domain.Ebook;
import com.yhzsk.wiki.domain.EbookExample;
import com.yhzsk.wiki.mapper.EbookMapper;
import com.yhzsk.wiki.req.EbookQueryReq;
import com.yhzsk.wiki.req.EbookSaveReq;
import com.yhzsk.wiki.resp.EbookResp;
import com.yhzsk.wiki.resp.PageResp;
import com.yhzsk.wiki.util.CopyUtil;
import com.yhzsk.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class Ebookservice {
    private static final Logger LOG = LoggerFactory.getLogger(Ebookservice.class);

    @Autowired
    private EbookMapper ebookMapper;

    @Autowired
    private SnowFlake snowFlake;


    public PageResp<EbookResp> list(EbookQueryReq req){
        //创建一个EbookExample的对象
        EbookExample ebookExample = new EbookExample();
        //调用createCriteria方法  创建一个Criteria的对象  这个Criteria是EbookExample的一个内部类
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if(!ObjectUtils.isEmpty(req.getName())){
            criteria.andNameLike("%" + req.getName() + "%");
        }
        if(!ObjectUtils.isEmpty(req.getCategoryId2())){
            criteria.andCategory2IdEqualTo(req.getCategoryId2());
        }


        //这边要获取PageReq这个父类的page 和  size 这样参数就变成动态的了
        PageHelper.startPage(req.getPage(),req.getSize());

        // 原本是直接return 现在改造成这样  用一个Ebook集合来接收
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);

        //通过pageInfo 获取总行数和总列数
        LOG.info("总行数 : {}", pageInfo.getTotal());
        LOG.info("总页数 : {}", pageInfo.getPages());

       /*
        //创建一个EbookResp集合
        List<EbookResp> respList = new ArrayList<>();
        //在循环中把第一个集合中的转化到第二个  实现实体类的复制
        for (Ebook ebook : ebookList) {
            EbookResp ebookResp = new EbookResp();
            //ebookResp.setId(ebook.getId());  这样写要把所有的属性都来一遍  比较繁琐
            //所以直接用工具类  一句就能实现对象的复制
            BeanUtils.copyProperties(ebook,ebookResp);
            respList.add(ebookResp);
        }

        return respList;
        */

        List<EbookResp> list = CopyUtil.copyList(ebookList, EbookResp.class);

        PageResp<EbookResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }



    //保存数据
    public void save(EbookSaveReq req){
        //  有两种保存方式   通过传进来的Id有没有值来判断是哪种
        Ebook ebook = CopyUtil.copy(req,Ebook.class);

        if (ObjectUtils.isEmpty(req.getId()))
        {
          //  第二种    新增保存
            ebook.setId(snowFlake.nextId());
            ebook.setDocCount(0);
            ebook.setViewCount(



                    0);
            ebook.setVoteCount(0);
            ebookMapper.insert(ebook);

        }else
        {
            //  第一种    更新保存
            ebookMapper.updateByPrimaryKey(ebook);
        }
    }

    public void delete(long id)
    {
        ebookMapper.deleteByPrimaryKey(id);
    }
}
