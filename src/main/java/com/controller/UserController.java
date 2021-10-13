package com.controller;

import ch.qos.logback.core.db.dialect.DBUtil;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.entity.User;
import com.graphbuilder.math.func.TanFunction;
import com.service.UserService;
//import com.util.ExcelUtils;
import javafx.scene.control.Tab;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author: ZhengTianLiang
 * @date: 2021/10/12  21:23
 * @desc:
 */

@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * @author: ZhengTianLiang
     * @date: 2021/10/12  21:30
     * @desc: list测试
     */
    @PostMapping(value = "/list")
    public List<User> testList() {
        return userService.testList();
    }

    /**
     * @author: ZhengTianLiang
     * @date: 2021/10/12  21:51
     * @desc: 插入百万数据测试
     */
    @PostMapping("/insert")
    public void insertMillion() {

        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/million_test?serverTimezone=UTC&useUnicode=true&zeroDateTimeBehavior=convertToNull&autoReconnect=true&characterEncoding=utf-8";// ip  port  dbname
        String username = "root";
        String password = "123456";

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, username, password);
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
                System.out.print("inner" + i + ": ");
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
     * <p>
     * 若要使用这个excelUtil，poi的版本用3.14才行(现在换成了3.17)
     */
    @PostMapping(value = "/export")
    public void testExport(HttpServletResponse response) {
        List<User> users = userService.testList();
        System.out.println(123);
//        ExcelUtils.writeExcel(response,users,User.class);
        System.out.println(234);
    }

    /**
     * @author: ZhengTianLiang
     * @date: 2021/10/13  22:05
     * @desc: 导出百万数据到excel测试_用easyExcel，高效的导出_一个sheet页面，一次查询导出
     */
    @PostMapping(value = "/test1")
    public void testEasyExcelExport() throws FileNotFoundException {
//        List<User> users = userService.testList();
        log.info("之前的时间");
        System.out.println(new Date(System.currentTimeMillis()).toString());


        // 生成EXCEL并指定输出路径
        OutputStream out = new FileOutputStream("E:\\temp\\withoutHead1.xlsx");
        ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);

        // 设置SHEET
        Sheet sheet = new Sheet(1, 0);
        sheet.setSheetName("sheet1");

        // 设置标题
        Table table = new Table(1);
        List<List<String>> titles = new ArrayList<List<String>>();
        titles.add(Arrays.asList("用户ID"));
        titles.add(Arrays.asList("名称"));
        titles.add(Arrays.asList("年龄"));
        titles.add(Arrays.asList("生日"));
        table.setHead(titles);

        // 查询数据导出即可 比如说一次性总共查询出100条数据
        List<List<String>> userList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            userList.add(Arrays.asList("ID_" + i, "小明" + i, String.valueOf(i), new Date().toString()));
        }

        writer.write0(userList, sheet, table);
        writer.finish();

    }

    /**
     * @author: ZhengTianLiang
     * @date: 2021/10/13  22:40
     * @desc: 导出百万数据到excel测试_用easyExcel，高效的导出_一个sheet页面，分批次查询导出
     */
    @PostMapping(value = "/test2")
    public void test2() throws FileNotFoundException {
        OutputStream out = new FileOutputStream("E:\\temp\\withoutHead2.xlsx");
        ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);

        // 设置sheet
        Sheet sheet = new Sheet(1, 0);
        sheet.setSheetName("shee");
        // 设置标题
        Table table = new Table(1);
        List<List<String>> titles = new ArrayList<>();
        titles.add(Arrays.asList("用户ID"));
        titles.add(Arrays.asList("名称"));
        titles.add(Arrays.asList("年龄"));
        titles.add(Arrays.asList("生日"));

        table.setHead(titles);

        // 模拟分批查询，总记录数50条，每次查询20条，分三次查询，最后一次的查询就10条
        Integer totalRowCount = 50;
        Integer pageSize = 20;
        Integer writeCount = totalRowCount % pageSize == 0 ?
                (totalRowCount / pageSize) : (totalRowCount / pageSize + 1);

        // 注： 此处仅仅为了模拟数据，实用环境不需要将最后一次分开，合成一个即可， 参数为： currentPage = i+1;  pageSize = pageSize
        for (int i = 0; i < writeCount; i++) {

            // 前两次查询 每次查20条数据
            if (i < writeCount - 1) {

                List<List<String>> userList = new ArrayList<>();
                for (int j = 0; j < pageSize; j++) {
                    userList.add(Arrays.asList("ID_" + Math.random(), "小明", String.valueOf(Math.random()), new Date().toString()));
                }
                writer.write0(userList, sheet, table);

            } else if (i == writeCount - 1) {

                // 最后一次查询 查多余的10条记录
                List<List<String>> userList = new ArrayList<>();
                Integer lastWriteRowCount = totalRowCount - (writeCount - 1) * pageSize;
                for (int j = 0; j < lastWriteRowCount; j++) {
                    userList.add(Arrays.asList("ID_" + Math.random(), "小明", String.valueOf(Math.random()), new Date().toString()));
                }
                writer.write0(userList, sheet, table);
            }
        }

        writer.finish();
    }

    /**
     * @author: ZhengTianLiang
     * @date: 2021/10/13  22:40
     * @desc: 导出百万数据到excel测试_用easyExcel，高效的导出_多个sheet页面，分批次查询导出
     */
    @PostMapping(value = "/test3")
    public void test3() throws FileNotFoundException {
        // 生成EXCEL并指定输出路径
        OutputStream out = new FileOutputStream("E:\\temp\\withoutHead3.xlsx");
        ExcelWriter writer = new ExcelWriter(out,ExcelTypeEnum.XLSX);

        String sheetName= "测试sheet";

        Table table= new Table(1);
        List<List<String>> titles = new ArrayList<List<String>>();
        titles.add(Arrays.asList("用户ID"));
        titles.add(Arrays.asList("名称"));
        titles.add(Arrays.asList("年龄"));
        titles.add(Arrays.asList("生日"));
        table.setHead(titles);

        // 模拟分批查询：总记录数250条，每个SHEET存100条，每次查询20条  则生成3个SHEET，前俩个SHEET查询次数为5， 最后一个SHEET查询次数为3 最后一次写的记录数是10
        // 注：该版本为了较少数据判断的复杂度，暂时perSheetRowCount要能够整除pageSize， 不去做过多处理  合理分配查询数据量大小不会内存溢出即可。
        Integer totalRowCount = 250;
        Integer perSheetRowCount = 100;
        Integer pageSize = 20;
        Integer sheetCount = totalRowCount % perSheetRowCount == 0 ? (totalRowCount / perSheetRowCount) : (totalRowCount / perSheetRowCount + 1);
        Integer previousSheetWriteCount = perSheetRowCount / pageSize;
        Integer lastSheetWriteCount = totalRowCount % perSheetRowCount == 0 ?
                previousSheetWriteCount :
                (totalRowCount % perSheetRowCount % pageSize == 0 ? totalRowCount % perSheetRowCount / pageSize : (totalRowCount % perSheetRowCount / pageSize + 1));

        for (int i = 0; i < sheetCount; i++) {

            // 创建SHEET
            Sheet sheet = new Sheet(i, 0);
            sheet.setSheetName(sheetName + i);

            if (i < sheetCount - 1) {

                // 前2个SHEET, 每个SHEET查5次 每次查20条 每个SHEET写满100行  2个SHEET合计200行  实用环境：参数： currentPage: j+1 + previousSheetWriteCount*i, pageSize: pageSize
                for (int j = 0; j < previousSheetWriteCount; j++) {
                    List<List<String>> userList = new ArrayList<>();
                    for (int k = 0; k < 20; k++) {
                        userList.add(Arrays.asList("ID_" + Math.random(), "小明", String.valueOf(Math.random()), new Date().toString()));
                    }
                    writer.write0(userList, sheet, table);
                }

            } else if (i == sheetCount - 1) {

                // 最后一个SHEET 实用环境不需要将最后一次分开，合成一个即可， 参数为： currentPage = i+1;  pageSize = pageSize
                for (int j = 0; j < lastSheetWriteCount; j++) {

                    // 前俩次查询 每次查询20条
                    if (j < lastSheetWriteCount - 1) {

                        List<List<String>> userList = new ArrayList<>();
                        for (int k = 0; k < 20; k++) {
                            userList.add(Arrays.asList("ID_" + Math.random(), "小明", String.valueOf(Math.random()), new Date().toString()));
                        }
                        writer.write0(userList, sheet, table);

                    } else if (j == lastSheetWriteCount - 1) {

                        // 最后一次查询 将剩余的10条查询出来
                        List<List<String>> userList = new ArrayList<>();
                        Integer lastWriteRowCount = totalRowCount - (sheetCount - 1) * perSheetRowCount - (lastSheetWriteCount - 1) * pageSize;
                        for (int k = 0; k < lastWriteRowCount; k++) {
                            userList.add(Arrays.asList("ID_" + Math.random(), "小明1", String.valueOf(Math.random()), new Date().toString()));
                        }
                        writer.write0(userList, sheet, table);

                    }
                }
            }
        }

        writer.finish();

    }


    /**
     * @author: ZhengTianLiang
     * @date: 2021/10/13  23:02
     * @desc: 导出百万数据到excel测试_用easyExcel，高效的导出_一个sheet页面，分批次查询导出_正式查库，不再是测试
     */
    @PostMapping(value = "/prod2")
    public void prod2(HttpServletResponse response) throws Exception {
        log.info("開始了");
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);

            // 设置EXCEL名称
            String fileName = new String(("SystemExcel").getBytes(), "UTF-8");

            // 设置SHEET名称
            Sheet sheet = new Sheet(1, 0);
            sheet.setSheetName("系统列表sheet1");

            // 设置标题
            Table table = new Table(1);
            List<List<String>> titles = new ArrayList<List<String>>();
            titles.add(Arrays.asList("系统名称"));
            titles.add(Arrays.asList("系统标识"));
            titles.add(Arrays.asList("描述"));
            titles.add(Arrays.asList("状态"));
            titles.add(Arrays.asList("创建人"));
            titles.add(Arrays.asList("创建时间"));
            table.setHead(titles);

            // 查询总数并 每个sheet存储的记录数 100W
            Integer totalRowCount = 1000000;
            // 每次向EXCEL写入的记录数(查询每页数据大小) 20W
            Integer pageSize =200000;
            Integer writeCount = totalRowCount % pageSize == 0 ? (totalRowCount / pageSize) : (totalRowCount / pageSize + 1);

            // 写数据 这个i的最大值直接拷贝就行了 不要改
            for (int i = 0; i < writeCount; i++) {
                List<List<String>> dataList = new ArrayList<>();

                // 此处查询并封装数据即可 currentPage, pageSize这个变量封装好的 不要改动
                List<User> users = userService.testList();
                if (!CollectionUtils.isEmpty(users)) {
                    users.forEach(eachSysSystemVO -> {
                        dataList.add(Arrays.asList(
                                eachSysSystemVO.getStudentId(),
                                eachSysSystemVO.getSubject(),
                                eachSysSystemVO.getDescription(),
                                eachSysSystemVO.getState().toString(),
                                eachSysSystemVO.getId().toString(),
                                eachSysSystemVO.getTeacherId()
                        ));
                    });
                }
                writer.write0(dataList, sheet, table);
            }

            // 下载EXCEL
            response.setHeader("Content-Disposition", "attachment;filename=" + new String((fileName).getBytes("gb2312"), "ISO-8859-1") + ".xls");
            response.setContentType("multipart/form-data");
            response.setCharacterEncoding("utf-8");
            writer.finish();
            out.flush();

        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        log.info("結束了");
    }

    /**
     * @author: ZhengTianLiang
     * @date: 2021/10/13  23:02
     * @desc: 导出百万数据到excel测试_用easyExcel，高效的导出_多个sheet页面，分批次查询导出_正式查库，不再是测试
     */
    @PostMapping(value = "/prod3")
    public void prod3(HttpServletResponse response) throws Exception{
        log.info("開始了");
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);

            // 设置EXCEL名称
            String fileName = new String(("SystemExcel").getBytes(), "UTF-8");

            // 设置SHEET名称
            String sheetName = "系统列表sheet";

            // 设置标题
            Table table = new Table(1);
            List<List<String>> titles = new ArrayList<List<String>>();
            titles.add(Arrays.asList("系统名称"));
            titles.add(Arrays.asList("系统标识"));
            titles.add(Arrays.asList("描述"));
            titles.add(Arrays.asList("状态"));
            titles.add(Arrays.asList("创建人"));
            titles.add(Arrays.asList("创建时间"));
            table.setHead(titles);

            // 查询总数并封装相关变量(这块直接拷贝就行了不要改)
            Integer totalRowCount = 1000000;
            Integer perSheetRowCount = 1000000;
            Integer pageSize = 200000;
            Integer sheetCount = totalRowCount % perSheetRowCount == 0 ? (totalRowCount / perSheetRowCount) : (totalRowCount / perSheetRowCount + 1);
            Integer previousSheetWriteCount = perSheetRowCount / pageSize;
            Integer lastSheetWriteCount = totalRowCount % perSheetRowCount == 0 ?
                    previousSheetWriteCount :
                    (totalRowCount % perSheetRowCount % pageSize == 0 ? totalRowCount % perSheetRowCount / pageSize : (totalRowCount % perSheetRowCount / pageSize + 1));


            for (int i = 0; i < sheetCount; i++) {

                // 创建SHEET
                Sheet sheet = new Sheet(i, 0);
                sheet.setSheetName(sheetName + i);

                // 写数据 这个j的最大值判断直接拷贝就行了，不要改动
                for (int j = 0; j < (i != sheetCount - 1 ? previousSheetWriteCount : lastSheetWriteCount); j++) {
                    List<List<String>> dataList = new ArrayList<>();

                    // 此处查询并封装数据即可 currentPage, pageSize这俩个变量封装好的 不要改动
//                    PageHelper.startPage(j + 1 + previousSheetWriteCount * i, pageSize);
//                    List<SysSystemVO> sysSystemVOList = this.sysSystemReadMapper.selectSysSystemVOList(sysSystemVO);
                    List<User> users = userService.testList();
                    if (!CollectionUtils.isEmpty(users)) {
                        users.forEach(eachSysSystemVO -> {
                            dataList.add(Arrays.asList(
                                    eachSysSystemVO.getStudentId(),
                                    eachSysSystemVO.getSubject(),
                                    eachSysSystemVO.getDescription(),
                                    eachSysSystemVO.getState().toString(),
                                    eachSysSystemVO.getId().toString(),
                                    eachSysSystemVO.getTeacherId()
                            ));
                        });
                    }
                    writer.write0(dataList, sheet, table);
                }
            }

            // 下载EXCEL
            response.setHeader("Content-Disposition", "attachment;filename=" + new String((fileName).getBytes("gb2312"), "ISO-8859-1") + ".xls");
            response.setContentType("multipart/form-data");
            response.setCharacterEncoding("utf-8");
            writer.finish();
            out.flush();

        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        log.info("結束了");
    }
}
