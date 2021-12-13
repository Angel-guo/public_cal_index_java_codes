package com.catl.cal_index;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.catl.cal_index.mapper"})
public class CalIndexApplication {
    public static void main(String[] args) {
        SpringApplication.run(CalIndexApplication.class, args);
    }

}
