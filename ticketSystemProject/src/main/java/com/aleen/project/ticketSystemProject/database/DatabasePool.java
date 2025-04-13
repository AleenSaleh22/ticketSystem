package com.aleen.project.ticketSystemProject.database;

import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;

public class DatabasePool {

  public static Pool createPool(Vertx vertx) {
    MySQLConnectOptions connectOptions = new MySQLConnectOptions()
      .setHost("localhost")
      .setPort(3306)
      .setDatabase("TicketSystem")
      .setUser("root")
      .setPassword("Thebest1inworld@");

    PoolOptions poolOptions = new PoolOptions().setMaxSize(10);

    return MySQLPool.pool(vertx, connectOptions, poolOptions);
  }

}
