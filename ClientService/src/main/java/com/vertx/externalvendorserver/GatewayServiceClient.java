package com.vertx.externalvendorserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class GatewayServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    public String callGatewayService(String name) throws URISyntaxException {
        return this.restTemplate.getForObject(new URI("https://localhost:8443/server/" + name), String.class);
    }
}
