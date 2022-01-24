package com.vertx.vertxexample;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

public class ServerService {
    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();

        Router router = Router.router(vertx);

        router.get("/server/:name").handler(ctx -> {
            String name = ctx.request().getParam("name");
            System.out.println("Server Name is " + name);
            HttpServerResponse response = ctx.response();
            response.end("Server Name is " + name);
        });

        vertx.createHttpServer().requestHandler(router).listen(8081);

    }
}
