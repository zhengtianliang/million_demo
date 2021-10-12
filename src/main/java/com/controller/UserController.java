package com.controller;

import ch.qos.logback.core.db.dialect.DBUtil;
import com.entity.User;
import com.service.UserService;
import com.util.ExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @author: ZhengTianLiang
 * @date: 2021/10/12  21:23
 * @desc:
 */

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * @author: ZhengTianLiang
     * @date: 2021/10/12  21:30
     * @desc: list测试
     */
    @PostMapping(value = "/list")
    public List<User> testList(){
        return userService.testList();
    }

    /**
     * @author: ZhengTianLiang
     * @date: 2021/10/12  21:51
     * @desc: 插入百万数据测试
     */
    @PostMapping("/insert")
    public void insertMillion(){

        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/million_test?serverTimezone=UTC&useUnicode=true&zeroDateTimeBehavior=convertToNull&autoReconnect=true&characterEncoding=utf-8";// ip  port  dbname
        String username = "root";
        String password = "123456";

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url,username,password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql = "insert into user(subject, description, teacher_id, student_id) values (?,?,?,?)";
        try {
            PreparedStatement prep = conn.prepareStatement(sql);
            // 将连接的自动提交关闭，数据在传送到数据库的过程中相当耗时
            conn.setAutoCommit(false);
            long start = System.currentTimeMillis();
            for (int i = 0; i < 10; i++) {
                long start2 = System.currentTimeMillis();
                // 一次性执行插入10万条数据
                for (int j = 0; j < 100000; j++) {
                    prep.setString(1, "test2");
                    prep.setString(2, "test3");
                    prep.setInt(3, 1234562);
                    prep.setInt(4, 12354545);
                    // 将预处理添加到批中
                    prep.addBatch();
                }
                // 预处理批量执行
                prep.executeBatch();
                prep.clearBatch();
                conn.commit();
                long end2 = System.currentTimeMillis();
                // 批量执行一次批量打印执行依次的时间
                System.out.print("inner"+i+": ");
                System.out.println(end2 - start2);
            }
            long end = System.currentTimeMillis();
            System.out.print("total: ");
            System.out.println(end - start);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @author: ZhengTianLiang
     * @date: 2021/10/12  22:09
     * @desc: 导出百万数据到excel测试_期待出现oom 并解决掉它
     */
    @PostMapping(value = "/export")
    public void testExport(HttpServletResponse response){
        List<User> users = userService.testList();
        ExcelUtils.writeExcel(response,users,User.class);
    }

}
