package com.example.demo;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SecurityScheme(name = "rest-test1-api",scheme = "bearer",type= SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class AlbumApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlbumApplication.class, args);
    }

}
