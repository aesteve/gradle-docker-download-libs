package com.github.aesteve.gradle.docker;

import io.vertx.core.Vertx;

public class Main {

    public static void main(String... args) {
        Vertx.vertx()
                .createHttpServer()
                .requestHandler(req ->
                    req.response().end("Hello from a blah jar file")
                )
                .listen(9999);
    }
}
