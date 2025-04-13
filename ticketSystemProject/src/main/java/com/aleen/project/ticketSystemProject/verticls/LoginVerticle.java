package com.aleen.project.ticketSystemProject.verticls;

import com.aleen.project.ticketSystemProject.commonApis.LoginHandler;
import com.aleen.project.ticketSystemProject.database.DatabasePool;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;

public class LoginVerticle extends AbstractVerticle {

  private final Router mainRouter;
  private Pool dbClient;

  public LoginVerticle(Router mainRouter) {
    this.mainRouter = mainRouter;
  }

  @Override
  public void start(Promise<Void> startPromise) {
    dbClient = DatabasePool.createPool(vertx);

    dbClient.query("SELECT 1").execute()
      .onSuccess(result -> {
        System.out.println("LoginVerticle: Database connection established.");

        LoginHandler loginHandler = new LoginHandler(dbClient);

        // Attach directly to the main router
        mainRouter.post("/api/login").handler(loginHandler::handleLogin);

        startPromise.complete();
      })
      .onFailure(error -> {
        System.err.println("LoginVerticle: Failed to connect to database - " + error.getMessage());
        startPromise.fail(error);
      });
  }
}
