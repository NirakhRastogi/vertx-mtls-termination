package com.vertx.externalvendorserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

@SpringBootApplication
@RestController
public class ClientServiceApplication {

    @Autowired
    private GatewayServiceClient gatewayServiceClient;

    public static void main(String[] args) {
        SpringApplication.run(ClientServiceApplication.class, args);
    }

    @GetMapping("/client/{name}")
    public String customApi(@PathVariable("name") String name) throws URISyntaxException {
        System.out.println("Calling gateway service.....");
        return this.gatewayServiceClient.callGatewayService(name);
    }

}
