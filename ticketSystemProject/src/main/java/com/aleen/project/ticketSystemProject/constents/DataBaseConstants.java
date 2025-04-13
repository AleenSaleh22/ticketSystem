package com.aleen.project.ticketSystemProject.constents;

public class DataBaseConstants {
  public static final String INSERT_NEW_CUSTOMER_QUERY = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)";
  public static final String CHECK_EMAIL_EXISTS = "SELECT 1 FROM users WHERE email = ?";
}
