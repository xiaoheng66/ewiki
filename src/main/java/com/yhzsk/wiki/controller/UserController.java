package com.yhzsk.wiki.controller;

import com.alibaba.fastjson.JSONObject;
import com.yhzsk.wiki.req.UserLoginReq;
import com.yhzsk.wiki.req.UserQueryReq;
import com.yhzsk.wiki.req.UserResetPasswordReq;
import com.yhzsk.wiki.req.UserSaveReq;
import com.yhzsk.wiki.resp.CommonResp;
import com.yhzsk.wiki.resp.PageResp;
import com.yhzsk.wiki.resp.UserLoginResp;
import com.yhzsk.wiki.resp.UserQueryResp;
import com.yhzsk.wiki.service.UserService;
import com.yhzsk.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Resource
    private UserService userService;


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SnowFlake snowFlake;

    @GetMapping("/list")
    public CommonResp list(@Valid UserQueryReq req) {
        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp<>();
        PageResp<UserQueryResp> list = userService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody UserSaveReq req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));

        CommonResp resp = new CommonResp<>();
        userService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable Long id) {
        CommonResp resp = new CommonResp<>();
        userService.delete(id);
        return resp;
    }



    @PostMapping("/reset-password")
    public CommonResp resetPassword(@Valid @RequestBody UserResetPasswordReq req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));

        CommonResp resp = new CommonResp<>();
        userService.resetPassword(req);
        return resp;
    }




    @PostMapping("/login")
    public CommonResp login(@Valid @RequestBody UserLoginReq req) {
        //这里要加密  因为注册时候的密码也是两层加密的 这边比对也需要加密两次才能和数据库中的密码正确比对
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));

        CommonResp<UserLoginResp> resp = new CommonResp<>();
        UserLoginResp userLoginResp = userService.login(req);
        //使用包装类的Long  下面直接转成String 放到登录后的返回信息里面
        Long token = snowFlake.nextId();
        LOG.info("生成单点登录的token: {},放到了redis里面", token);
        redisTemplate.opsForValue().set(token.toString(), JSONObject.toJSONString(userLoginResp),3600 * 240, TimeUnit.SECONDS);

        userLoginResp.setToken(token.toString());

        resp.setContent(userLoginResp);
        return resp;
    }


    //退出登录接口
    @GetMapping("/logout/{token}")
    public CommonResp logout(@PathVariable String token) {
        CommonResp resp = new CommonResp<>();
        //把token从redis里面删除就可以了
        redisTemplate.delete(token);

        LOG.info("从redis中删除token: {}", token);
        return resp;
    }

}