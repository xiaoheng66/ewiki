package com.yhzsk.wiki.job;


import com.yhzsk.wiki.service.Docservice;
import com.yhzsk.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DocJob {

    private static final Logger LOG = LoggerFactory.getLogger(DocJob.class);

    @Resource
    private Docservice docservice;

    @Resource
    private SnowFlake snowFlake;

    /**
     * 每30秒更新信息
     */
    @Scheduled(cron = "5/30 * * * * ?")
    public void cron() {

        MDC.put("LOG_ID", String.valueOf(snowFlake.nextId()));

        LOG.info("更新文档数据开始");
        long start = System.currentTimeMillis();
        docservice.updateEbookInfo();
        LOG.info("更新文档数据结束，耗时：{}毫秒", System.currentTimeMillis() - start);
    }

}

