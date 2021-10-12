package com;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = "com.zhneg.mapper")
public class MillionTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MillionTestApplication.class, args);
    }

}
