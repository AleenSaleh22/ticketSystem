package com.aleen.project.ticketSystemProject.customerApis;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;

public class CustomerUpdateInfoHandler {
  private final Pool dbClient;

  public CustomerUpdateInfoHandler(Pool dbClient) {
    this.dbClient = dbClient;
  }

  public void handleUpdateInfo(RoutingContext context) {
    JsonObject requestBody = context.body().asJsonObject();

    //check if all fields exists for the user to optionally update one or more fields
    if (requestBody == null ||
      !requestBody.containsKey("password") ||
      !requestBody.containsKey("username") ||
      !requestBody.containsKey("email")) {
      context.response()
        .setStatusCode(400)
        .end(new JsonObject().put("error", "Missing credentials").encode());
      return;
    }

    String username = requestBody.getString("username");
    String email = requestBody.getString("email");
    String password = requestBody.getString("password");

    if (username.isEmpty() || username.contains(" ")
      || password.isEmpty() || password.length() < 8
      || email.isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")
    ) {
      //this is for error handling to see if the user input is invalided
      context.response()
        .setStatusCode(400)
        .end(new JsonObject().put("error", "Bad request").encode());
      return;
    }
  }
}
