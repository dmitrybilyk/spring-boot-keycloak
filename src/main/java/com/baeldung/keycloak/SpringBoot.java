package com.baeldung.keycloak;

import com.zoomint.keycloak.client.EnableKeycloakClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableKeycloakClient
@SpringBootApplication

public class SpringBoot {

    public static void main(String[] args) {
        SpringApplication.run(SpringBoot.class, args);
    }

}
