package com.yasenagat.zkweb;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.yasenagat.zkweb"})
public class ZkWebApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ZkWebApplication.class);
        app.run(args);
    }
}
