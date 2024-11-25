package com.yhzsk.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yhzsk.wiki.domain.Category;
import com.yhzsk.wiki.domain.CategoryExample;
import com.yhzsk.wiki.mapper.CategoryMapper;
import com.yhzsk.wiki.req.CategoryQueryReq;
import com.yhzsk.wiki.req.CategorySaveReq;
import com.yhzsk.wiki.resp.CategoryQueryResp;
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
public class Categoryservice {
    private static final Logger LOG = LoggerFactory.getLogger(Categoryservice.class);

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private SnowFlake snowFlake;


    public PageResp<CategoryQueryResp> list(CategoryQueryReq req){
        //创建一个CategoryExample的对象
        CategoryExample categoryExample = new CategoryExample();
        //调用createCriteria方法  创建一个Criteria的对象  这个Criteria是CategoryExample的一个内部类
        CategoryExample.Criteria criteria = categoryExample.createCriteria();

        //这边要获取PageReq这个父类的page 和  size 这样参数就变成动态的了
        PageHelper.startPage(req.getPage(),req.getSize());

        // 原本是直接return 现在改造成这样  用一个Category集合来接收
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);

        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);

        //通过pageInfo 获取总行数和总页数
        LOG.info("总行数 : {}", pageInfo.getTotal());
        LOG.info("总页数 : {}", pageInfo.getPages());




        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);

        PageResp<CategoryQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);


        return pageResp;
    }

    //查询全部数据
    public List<CategoryQueryResp> all(){
        //创建一个CategoryExample的对象
        CategoryExample categoryExample = new CategoryExample();

        //按顺序排序
        categoryExample.setOrderByClause("sort asc");



        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);


        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);




        return list;
    }



    /*
    * 保存数据
    *
    * */
    public void save(CategorySaveReq req){
        //  有两种保存方式   通过传进来的Id有没有值来判断是哪种
        Category category = CopyUtil.copy(req,Category.class);

        if (ObjectUtils.isEmpty(req.getId()))
        {
          //  第二种    新增保存
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);

        }else
        {
            //  第一种    更新保存
            categoryMapper.updateByPrimaryKey(category);
        }
    }

    public void delete(long id)
    {
        categoryMapper.deleteByPrimaryKey(id);
    }
}
