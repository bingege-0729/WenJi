package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.example.Mapper")
public class WenjiApplication {

    public static void main(String[] args) {
        SpringApplication.run(WenjiApplication.class, args);
    }

}
