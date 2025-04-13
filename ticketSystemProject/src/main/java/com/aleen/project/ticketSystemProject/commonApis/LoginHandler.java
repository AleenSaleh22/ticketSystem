package com.aleen.project.ticketSystemProject.commonApis;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Tuple;

public class LoginHandler {
  private final Pool dbClient;

  public LoginHandler(Pool dbClient) {
    this.dbClient = dbClient;
  }

  public void handleLogin(RoutingContext context) {
    JsonObject requestBody = context.body().asJsonObject();

    if (requestBody == null || !requestBody.containsKey("username") || !requestBody.containsKey("password")) {
      context.response()
        .setStatusCode(400)
        .end(new JsonObject().put("error", "Missing credentials").encode());
      return;
    }

    String username = requestBody.getString("username");
    String password = requestBody.getString("password");

    if (username == null || password == null) {
      context.response()
        .setStatusCode(400)
        .end(new JsonObject().put("error", "Missing credentials").encode());
      return;
    }

    String query = "SELECT id, password FROM users WHERE username = ?";
    dbClient.preparedQuery(query)
      .execute(Tuple.of(username))
      .onSuccess(rows -> {
        if (rows.size() == 0) {
          context.response()
            .setStatusCode(401)
            .end(new JsonObject().put("error", "Invalid username or password").encode());
        } else {
          var row = rows.iterator().next();
          String storedPassword = row.getString("password");
          int userId = row.getInteger("id");

          if (storedPassword.equals(password)) {
            String token = generateToken(username);

            // Store token in login_tokens table (not logout_tokens)
            String insertSessionQuery = "INSERT INTO login_tokens (token, user_id) VALUES (?, ?)";
            dbClient.preparedQuery(insertSessionQuery)
              .execute(Tuple.of(token, userId), res -> {
                if (res.succeeded()) {
                  context.response()
                    .setStatusCode(200)
                    .end(new JsonObject().put("token", token).encode());
                } else {
                  context.response()
                    .setStatusCode(500)
                    .end(new JsonObject().put("error", "Failed to create session").encode());
                }
              });
          } else {
            context.response()
              .setStatusCode(401)
              .end(new JsonObject().put("error", "Invalid username or password").encode());
          }
        }
      })
      .onFailure(err -> {
        context.response()
          .setStatusCode(500)
          .end(new JsonObject().put("error", "Database error: " + err.getMessage()).encode());
      });
  }

  private String generateToken(String username) {
    return username + "_token_" + System.currentTimeMillis();
  }
}
