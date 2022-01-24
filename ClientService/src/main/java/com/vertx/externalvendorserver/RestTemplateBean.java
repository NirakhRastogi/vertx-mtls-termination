package com.vertx.externalvendorserver;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.security.KeyStore;

@Component
public class RestTemplateBean {

  @Bean
  public RestTemplate getRestTemplate() {
    RestTemplate restTemplate = new RestTemplate();

    KeyStore keyStore;
    HttpComponentsClientHttpRequestFactory requestFactory = null;

    try {
      keyStore = KeyStore.getInstance("jks");
      ClassPathResource classPathResource = new ClassPathResource("nt-gateway.jks");
      InputStream inputStream = classPathResource.getInputStream();
      keyStore.load(inputStream, "changeit".toCharArray());

      SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(new SSLContextBuilder()
        .loadTrustMaterial(null, new TrustSelfSignedStrategy())
        .loadKeyMaterial(keyStore, "changeit".toCharArray()).build(),
        NoopHostnameVerifier.INSTANCE);

      HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory)
        .setMaxConnTotal(Integer.valueOf(5))
        .setMaxConnPerRoute(Integer.valueOf(5))
        .build();

      requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
      requestFactory.setReadTimeout(Integer.valueOf(10000));
      requestFactory.setConnectTimeout(Integer.valueOf(10000));

      restTemplate.setRequestFactory(requestFactory);
    } catch (Exception exception) {
      System.out.println("Exception Occured while creating restTemplate " + exception);
      exception.printStackTrace();
    }
    return restTemplate;
  }

}
