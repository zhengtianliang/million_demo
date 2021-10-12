package com.entity;

import com.util.ExcelColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: ZhengTianLiang
 * @date: 2021/10/12  21:21
 * @desc: user类
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @ExcelColumn(col = 1,value = "主键")
    private Integer id;

    @ExcelColumn(col = 2,value = "学科")
    private String subject;

    @ExcelColumn(col = 3,value = "描述")
    private String description;

    @ExcelColumn(col = 4,value = "老师")
    private String teacherId;

    @ExcelColumn(col = 5,value = "学生")
    private String studentId;

    @ExcelColumn(col = 6,value = "状态")
    private Boolean state;
}
