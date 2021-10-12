package com.mapper;

import com.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: ZhengTianLiang
 * @date: 2021/10/12  21:23
 * @desc:
 */

@Mapper
public interface UserMapper {

    /**
     * @author: ZhengTianLiang
     * @date: 2021/10/12  21:30
     * @desc: list测试
     */
    List<User> selectList();
}
