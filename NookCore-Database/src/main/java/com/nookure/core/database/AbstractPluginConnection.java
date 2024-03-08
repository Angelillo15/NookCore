package com.nookure.core.database;

import com.nookure.core.database.config.DataProvider;
import com.nookure.core.database.config.DatabaseConfig;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import java.sql.Connection;

/**
 * This class represents a connection to the database.
 * Use {@link #connect(DatabaseConfig)} to connect to the database.
 */
public abstract class AbstractPluginConnection implements AutoCloseable {
  /**
   * Connects to the database.
   * <p>
   * This method should be called before any other method.
   * If the connection is already established, this method should do nothing.
   * If the connection is not established, this method should establish it.
   * If the connection is not established, and the connection fails, the plugin should
   * try using SQLite.
   * If the connection is not established, and the connection fails, and SQLite fails,
   * the plugin should disable itself.
   * </p>
   *
   * @param config the database config
   * @see DatabaseConfig
   */
  public void connect(@NotNull DatabaseConfig config) {
    if (config.getType() == DataProvider.MYSQL) {
      connect(config.getHost(), config.getDatabase(), config.getUsername(), config.getPassword(), config.getPort());
      return;
    }

    connect();
  }

  /**
   * Connects to the database as MySQL.
   *
   * @param host     the host of the database
   * @param database the name of the database
   * @param username the username of the database
   * @param password the password of the database
   * @param port     the port of the database
   */
  public abstract void connect(@NotNull String host, @NotNull String database, @NotNull String username, @NotNull String password, int port);

  /**
   * Connects to the database as SQLite.
   */
  public abstract void connect();

  /**
   * Returns the jOOQ instance.
   * This should be called after {@link #connect(DatabaseConfig)}.
   * <p>
   * If the connection is not established, this method should throw an exception.
   * If the connection is established, this method should return the Storm instance.
   * </p>
   *
   * @return the jOOQ instance
   */
  public abstract @NotNull DSLContext getJOOQ();

  /**
   * Returns the connection to the database.
   * this should be called after {@link #connect(DatabaseConfig)}.
   * <p>
   * If the connection is not established, this method should throw an exception.
   * If the connection is established, this method should return the connection.
   * </p>
   *
   * @return the connection to the database
   */
  public abstract @NotNull Connection getConnection();
}