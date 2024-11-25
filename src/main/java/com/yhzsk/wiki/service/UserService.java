package com.yhzsk.wiki.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.yhzsk.wiki.domain.User;
import com.yhzsk.wiki.domain.UserExample;
import com.yhzsk.wiki.exception.BusinessException;
import com.yhzsk.wiki.exception.BusinessExceptionCode;
import com.yhzsk.wiki.mapper.UserMapper;
import com.yhzsk.wiki.req.UserLoginReq;
import com.yhzsk.wiki.req.UserQueryReq;
import com.yhzsk.wiki.req.UserResetPasswordReq;
import com.yhzsk.wiki.req.UserSaveReq;
import com.yhzsk.wiki.resp.PageResp;
import com.yhzsk.wiki.resp.UserLoginResp;
import com.yhzsk.wiki.resp.UserQueryResp;
import com.yhzsk.wiki.util.CopyUtil;
import com.yhzsk.wiki.util.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<UserQueryResp> list(UserQueryReq req) {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getLoginName())) {
            criteria.andLoginNameEqualTo(req.getLoginName());
        }
        PageHelper.startPage(req.getPage(), req.getSize());
        List<User> userList = userMapper.selectByExample(userExample);

        PageInfo<User> pageInfo = new PageInfo<>(userList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());

        // List<UserResp> respList = new ArrayList<>();
        // for (User user : userList) {
        //     // UserResp userResp = new UserResp();
        //     // BeanUtils.copyProperties(user, userResp);
        //     // 对象复制
        //     UserResp userResp = CopyUtil.copy(user, UserResp.class);
        //
        //     respList.add(userResp);
        // }

        // 列表复制
        List<UserQueryResp> list = CopyUtil.copyList(userList, UserQueryResp.class);

        PageResp<UserQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);

        return pageResp;
    }

    /**
     * 保存
     */
    public void save(UserSaveReq req) {
        User user = CopyUtil.copy(req, User.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            User userDB = selectByLoginName(req.getLoginName());
            // 新增
            if (ObjectUtils.isEmpty(userDB))
            {
                user.setId(snowFlake.nextId());
                userMapper.insert(user);
            }else {
                //用户名已经存在  这个时候用到自定义异常
                throw new BusinessException(BusinessExceptionCode.USER_LOGIN_NAME_EXIST);
            }

        } else {
            // 更新
            user.setLoginName(null);
            user.setPassword(null);
            userMapper.updateByPrimaryKeySelective(user);
        }
    }

    public void delete(Long id) {
        userMapper.deleteByPrimaryKey(id);
    }

    public User selectByLoginName(String loginName)
    {
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();

        criteria.andLoginNameEqualTo(loginName);
        //这个方法只能用list来接收
        List<User> userList = userMapper.selectByExample(userExample);
        if (CollectionUtils.isEmpty(userList))
        {
            return null;
        }else {
            return userList.get(0);
        }
    }


    //修改密码
    public void resetPassword(UserResetPasswordReq req) {
        User user = CopyUtil.copy(req, User.class);
        userMapper.updateByPrimaryKeySelective(user);
    }


    //登录
    public UserLoginResp login(UserLoginReq req) {

        /*要先按用户名去数据库查   查到了再拿出密码来比对
        * 如果登录不正确  应该抛出一个异常 用异常来中断程序
        * 无论是密码错误还是用户名不正确   都应该统一提示信息  出于安全考虑不要给出具体的错误提示
        * */

        //调用之前写的方法  从数据库把这个用户查出来
        User userDb = selectByLoginName(req.getLoginName());

        if (ObjectUtils.isEmpty(userDb))
        {
            LOG.info("用户名不存在,{}",req.getLoginName());
            //用户名不存在
            throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
        }else{
            if (userDb.getPassword().equals(req.getPassword()))
            {
                //登录成功
                UserLoginResp userLoginResp = CopyUtil.copy(userDb, UserLoginResp.class);
                return userLoginResp;
            }else {
                //密码不对
                LOG.info("密码不对, 输入密码: {}, 数据库密码: {}", req.getPassword(), userDb.getPassword());
                throw new BusinessException(BusinessExceptionCode.LOGIN_USER_ERROR);
            }
        }


    }


}