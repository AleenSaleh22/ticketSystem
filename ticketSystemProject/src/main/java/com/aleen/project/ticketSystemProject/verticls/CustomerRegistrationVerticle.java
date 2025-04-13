package com.aleen.project.ticketSystemProject.verticls;

import com.aleen.project.ticketSystemProject.customerApis.CustomerSignupHandler;
import com.aleen.project.ticketSystemProject.database.DatabasePool;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;

public class CustomerRegistrationVerticle extends AbstractVerticle {

  private final Router mainRouter;
  private Pool dbClient;

  public CustomerRegistrationVerticle(Router mainRouter) {
    this.mainRouter = mainRouter;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    dbClient = DatabasePool.createPool(vertx);

    dbClient.query("SELECT 1").execute()
      .onSuccess(result -> {
        System.out.println("CustomerRegistrationVerticle: Database connection established.");

        // Create an instance of CustomerRegestrationHandler
        CustomerSignupHandler SignUpHandler = new CustomerSignupHandler(dbClient);

        // Use the instance to reference the method
        mainRouter.post("/api/registration").handler(SignUpHandler::handleCustomerSignup);
        startPromise.complete();
      })
      .onFailure(error -> {
        System.err.println("CustomerRegistrationVerticle: Failed to connect to database - " + error.getMessage());
        startPromise.fail(error);
      });
  }
}
