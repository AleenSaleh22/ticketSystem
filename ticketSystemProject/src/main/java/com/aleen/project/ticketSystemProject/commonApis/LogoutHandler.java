package com.aleen.project.ticketSystemProject.commonApis;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Tuple;
import io.vertx.core.json.JsonObject;

public class LogoutHandler implements Handler<RoutingContext> {
  private final Pool dbClient;

  public LogoutHandler(Pool dbClient) {
    this.dbClient = dbClient;
  }

  @Override
  public void handle(RoutingContext context) {
    String authHeader = context.request().getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      context.response()
        .setStatusCode(401)
        .end(new JsonObject().put("error", "Missing or invalid Authorization header").encode());
      return;
    }

    String token = authHeader.substring("Bearer ".length()).trim();

    String query = "DELETE FROM login_tokens WHERE token = ?";
    dbClient.preparedQuery(query)
      .execute(Tuple.of(token))
      .onSuccess(result -> {
        if (result.rowCount() > 0) {
          context.response()
            .setStatusCode(200)
            .end(new JsonObject().put("message", "Logged out successfully").encode());
        } else {
          context.response()
            .setStatusCode(401)
            .end(new JsonObject().put("error", "No active session found or invalid token").encode());
        }
      })
      .onFailure(err -> {
        context.response()
          .setStatusCode(500)
          .end(new JsonObject().put("error", "Database error: " + err.getMessage()).encode());
      });
  }
}
