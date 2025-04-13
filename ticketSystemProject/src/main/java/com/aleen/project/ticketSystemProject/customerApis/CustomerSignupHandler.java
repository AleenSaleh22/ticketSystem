package com.aleen.project.ticketSystemProject.customerApis;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Tuple;

import static com.aleen.project.ticketSystemProject.constents.DataBaseConstants.CHECK_EMAIL_EXISTS;
import static com.aleen.project.ticketSystemProject.constents.DataBaseConstants.INSERT_NEW_CUSTOMER_QUERY;

public class CustomerSignupHandler {
  // Step 1: declare a final database client
  private final Pool dbClient;

  // Step 2: constructor to inject the dbClient from the verticle
  public CustomerSignupHandler(Pool dbClient) {
    this.dbClient = dbClient;
  }

  // Step 3: handle the full customer registration process
  public void handleCustomerSignup(RoutingContext context) {
    JsonObject requestBody = context.body().asJsonObject();

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

    if (username == null || username.contains(" ") ||
      password == null || password.length() < 8 ||
      email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
      context.response()
        .setStatusCode(400)
        .end(new JsonObject().put("error", "Bad request").encode());
      return;
    }

    // Now using instance methods properly
    checkEmailExists(email)
      .onSuccess(exists -> {
        if (exists) {
          context.response()
            .setStatusCode(409)
            .end("Email exists");
        } else {
          insertNewCustomer(username, email, password)
            .onSuccess(v -> {
              context.response()
                .setStatusCode(201)
                .end(new JsonObject().put("message", "Registration successful, please login").encode());
            })
            .onFailure(err -> {
              err.printStackTrace();
              context.response()
                .setStatusCode(500)
                .end("Failed to create account");
            });
        }
      })
      .onFailure(err -> {
        err.printStackTrace();
        context.response()
          .setStatusCode(500)
          .end("Server error");
      });
  }

  // Check if email already exists in DB
  private Future<Boolean> checkEmailExists(String email) {
    // Trim the email to remove leading or trailing spaces
    String trimmedEmail = email.trim();

    // Use the trimmed email in the query
    return dbClient
      .preparedQuery(CHECK_EMAIL_EXISTS)  // Use '?' for MySQL
      .execute(Tuple.of(trimmedEmail))  // Pass the trimmed email to the query
      .map(rowSet -> rowSet.size() > 0);  // Check if the email exists
  }


  // Insert new customer into DB
  private Future<Void> insertNewCustomer(String username, String email, String password) {
    String defaultRole = "Customer";
    return dbClient
      .preparedQuery(INSERT_NEW_CUSTOMER_QUERY)
      .execute(Tuple.of(username, email, password, defaultRole))
      .mapEmpty();
  }

  // A methode to update the signup information

}
