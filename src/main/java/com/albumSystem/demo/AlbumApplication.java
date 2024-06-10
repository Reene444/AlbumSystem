package com.albumSystem.demo;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SecurityScheme(name = "album-system-api",scheme = "bearer",type= SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class AlbumApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlbumApplication.class, args);
    }

}
