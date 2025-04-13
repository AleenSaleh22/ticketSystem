package com.aleen.project.ticketSystemProject;

import com.aleen.project.ticketSystemProject.verticls.CustomerRegistrationVerticle;
import com.aleen.project.ticketSystemProject.verticls.LoginVerticle;
import com.aleen.project.ticketSystemProject.verticls.LogoutVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class MainVerticle extends AbstractVerticle {

  private Router mainRouter;

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle(), res -> {
      if (res.succeeded()) {
        System.out.println("MainVerticle deployed successfully.");
      } else {
        System.err.println("Failed to deploy MainVerticle: " + res.cause().getMessage());
      }
    });
  }

  @Override
  public void start(Promise<Void> startPromise) {
    mainRouter = Router.router(vertx);
    mainRouter.route().handler(BodyHandler.create());

    // Deploy LoginVerticle and pass the mainRouter
    vertx.deployVerticle(new LoginVerticle(mainRouter));
    vertx.deployVerticle(new LogoutVerticle(mainRouter));
    vertx.deployVerticle(new CustomerRegistrationVerticle(mainRouter));

    vertx.createHttpServer()
      .requestHandler(mainRouter)
      .listen(8080)
      .onSuccess(server -> {
        System.out.println("HTTP server started on port 8080");
        startPromise.complete();
      })
      .onFailure(error -> {
        System.err.println("Failed to start HTTP server: " + error.getMessage());
        startPromise.fail(error);
      });
  }

}
