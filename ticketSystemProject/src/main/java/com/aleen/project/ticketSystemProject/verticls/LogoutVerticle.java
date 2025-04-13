package com.aleen.project.ticketSystemProject.verticls;

import com.aleen.project.ticketSystemProject.commonApis.LogoutHandler;
import com.aleen.project.ticketSystemProject.database.DatabasePool;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;

public class LogoutVerticle extends AbstractVerticle {

  private final Router mainRouter;
  private Pool dbClient;

  public LogoutVerticle(Router mainRouter) {
    this.mainRouter = mainRouter;
  }

  @Override
  public void start(Promise<Void> startPromise) {
    dbClient = DatabasePool.createPool(vertx);

    dbClient.query("SELECT 1").execute()
      .onSuccess(result -> {
        System.out.println("LogoutVerticle: Database connection established.");

        LogoutHandler logoutHandler = new LogoutHandler(dbClient);

        // Attach logout endpoint to the main router
        mainRouter.post("/api/logout").handler(logoutHandler::handle);

        startPromise.complete();
      })
      .onFailure(error -> {
        System.err.println("LogoutVerticle: Failed to connect to database - " + error.getMessage());
        startPromise.fail(error);
      });
  }
}
