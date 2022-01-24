package com.vertx.vertxexample;

import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakeException;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.CorsHandler;

import javax.net.ssl.SSLPeerUnverifiedException;

public class GatewayService extends AbstractVerticle {

    private static String verifiedCN = "localhost";

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        Router router = Router.router(vertx);

        router.route().handler(CorsHandler.create("*").allowedHeader("*"));

        router.get("/server/:name").handler(ctx -> {
            String name = ctx.request().getParam("name");

            try {
                String CN = getCN(ctx.request().sslSession().getPeerPrincipal().getName());
                if (!CN.equalsIgnoreCase(verifiedCN)) {
                    throw new WebSocketClientHandshakeException("Unable to verify the client CN name.. provided-" + CN + ", required-" + verifiedCN);
                }
            } catch (SSLPeerUnverifiedException e) {
                e.printStackTrace();
            }

            System.out.println("Server Name is " + name);
            WebClient webClient = WebClient.create(vertx);
            webClient.get(8081, "localhost", "/server/" + name).send().onSuccess(res -> {
                System.out.println("Success Response received from server service - " + res.body());
                ctx.response().end(res.body());
                startPromise.complete();
            }).onFailure(fai -> {
                System.out.println("Failure Response received from server service - " + fai.getMessage());
                ctx.response().end(fai.getMessage());
                startPromise.fail(fai.getMessage());
            });
        });
        System.out.println("Secure Transport Protocol [ SSL/TLS ] has been enabled !!! ");
        HttpServerOptions httpServerOptions = new HttpServerOptions().setSsl(true).setKeyStoreOptions(new JksOptions().setPath("tls/nt-gateway.jks").setAlias("nt-gateway").setPassword("changeit")).setTrustStoreOptions(new JksOptions().setPath("tls/nt-gateway.jks").setAlias("nt-gateway").setPassword("changeit")).setClientAuth(ClientAuth.REQUIRED);

        vertx.createHttpServer(httpServerOptions).requestHandler(router).listen(8443, "localhost");
    }

    private String getCN(String name) {

        return name.substring(name.indexOf("CN=") + 3, name.indexOf(",OU"));

    }
}
