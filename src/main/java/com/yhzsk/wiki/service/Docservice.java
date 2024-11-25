package com.yhzsk.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yhzsk.wiki.domain.Content;
import com.yhzsk.wiki.domain.Doc;
import com.yhzsk.wiki.domain.DocExample;
import com.yhzsk.wiki.exception.BusinessException;
import com.yhzsk.wiki.exception.BusinessExceptionCode;
import com.yhzsk.wiki.mapper.ContentMapper;
import com.yhzsk.wiki.mapper.DocMapper;
import com.yhzsk.wiki.mapper.DocMapperCust;
import com.yhzsk.wiki.req.DocQueryReq;
import com.yhzsk.wiki.req.DocSaveReq;
import com.yhzsk.wiki.resp.DocQueryResp;
import com.yhzsk.wiki.resp.PageResp;
import com.yhzsk.wiki.util.CopyUtil;
import com.yhzsk.wiki.util.RedisUtil;
import com.yhzsk.wiki.util.RequestContext;
import com.yhzsk.wiki.util.SnowFlake;
import com.yhzsk.wiki.websocket.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class Docservice {
    private static final Logger LOG = LoggerFactory.getLogger(Docservice.class);

    @Autowired
    private DocMapper docMapper;

    @Autowired
    private SnowFlake snowFlake;

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private DocMapperCust docMapperCust;

    @Autowired
    public RedisUtil redisUtil;

    @Autowired
    public WsService wsService;

    public PageResp<DocQueryResp> list(DocQueryReq req){
        //创建一个DocExample的对象
        DocExample docExample = new DocExample();
        //调用createCriteria方法  创建一个Criteria的对象  这个Criteria是DocExample的一个内部类
        DocExample.Criteria criteria = docExample.createCriteria();

        //这边要获取PageReq这个父类的page 和  size 这样参数就变成动态的了
        PageHelper.startPage(req.getPage(),req.getSize());

        // 原本是直接return 现在改造成这样  用一个Doc集合来接收
        List<Doc> docList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docList);

        //通过pageInfo 获取总行数和总页数
        LOG.info("总行数 : {}", pageInfo.getTotal());
        LOG.info("总页数 : {}", pageInfo.getPages());




        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        PageResp<DocQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);


        return pageResp;
    }

    //查询全部数据
    public List<DocQueryResp> all(Long ebookId){
        //创建一个DocExample的对象
        DocExample docExample = new DocExample();

        docExample.createCriteria().andEbookIdEqualTo(ebookId);

        //按顺序排序
        docExample.setOrderByClause("sort asc");



        List<Doc> docList = docMapper.selectByExample(docExample);


        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);




        return list;
    }



    /*
    * 保存数据
    *
    * */
    @Transactional
    public void save(DocSaveReq req){
        //  有两种保存方式   通过传进来的Id有没有值来判断是哪种
        Doc doc = CopyUtil.copy(req,Doc.class);
        Content content = CopyUtil.copy(req,Content.class);


        if (ObjectUtils.isEmpty(req.getId()))
        {
          //  第二种    新增保存
            doc.setId(snowFlake.nextId());
            doc.setViewCount(0);
            doc.setVoteCount(0);
            docMapper.insert(doc);

            content.setId(doc.getId());
            contentMapper.insert(content);

        }else
        {
            //  第一种    更新保存
            docMapper.updateByPrimaryKey(doc);

            int count = contentMapper.updateByPrimaryKeyWithBLOBs(content);
            if (count == 0)
            {
                contentMapper.insert(content);
            }
        }
    }

    public void delete(long id)
    {
        docMapper.deleteByPrimaryKey(id);
    }
    public void delete(List<String> ids)
    {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);

        docMapper.deleteByExample(docExample);
    }

    //根据id查找文档的content
    public String findContent(Long id)
    {
        Content content = contentMapper.selectByPrimaryKey(id);
        //阅读数自增1
        docMapperCust.increaseViewCount(id);

        if (ObjectUtils.isEmpty(content)) {
            return "";
        } else {
            return content.getContent();
        }

    }


    public void vote(Long id) {
        // docMapperCust.increaseVoteCount(id);
        // 远程IP+doc.id作为key，24小时内不能重复
        String ip = RequestContext.getRemoteAddr();
        if (redisUtil.validateRepeat("DOC_VOTE_" + id + "_" + ip, 3600 * 24)) {
            docMapperCust.increaseVoteCount(id);
        } else {
            throw new BusinessException(BusinessExceptionCode.VOTE_REPEAT);
        }

        Doc docDb = docMapper.selectByPrimaryKey(id);

        String logId = MDC.get("LOG_ID");

        wsService.sendInfo(docDb.getName() + "被点赞了！！！", logId);

    }


    public void updateEbookInfo()
    {
        docMapperCust.updateEbookInfo();
    }
}
