package com.service.impl;

import com.entity.User;
import com.mapper.UserMapper;
import com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: ZhengTianLiang
 * @date: 2021/10/12  21:23
 * @desc:
 */

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * @author: ZhengTianLiang
     * @date: 2021/10/12  21:30
     * @desc: list测试
     */
    @Override
    public List<User> testList() {
        return userMapper.selectList();
    }
}
