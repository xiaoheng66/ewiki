package com.yhzsk.wiki.service;

import com.yhzsk.wiki.domain.Doc;
import com.yhzsk.wiki.websocket.WebSocketServer;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class WsService {



    @Autowired
    public WebSocketServer webSocketServer;

    @Async
    public void sendInfo(String message, String logId){

        MDC.put("LOG_ID", logId);

        //推送消息
        webSocketServer.sendInfo(message);
    }

}

