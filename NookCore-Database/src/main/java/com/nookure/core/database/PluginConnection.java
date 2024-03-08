package com.nookure.core.database;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nookure.core.annotation.PluginDataFolder;
import com.nookure.core.logger.Logger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static java.util.Objects.requireNonNull;

@Singleton
public class PluginConnection extends AbstractPluginConnection {
  @Inject
  private Logger logger;
  @Inject
  @PluginDataFolder
  private File pluginDataFolder;
  private DSLContext dslContext;
  private Connection connection;
  private HikariDataSource hikariDataSource;

  @Override
  public void connect(@NotNull String host, @NotNull String database, @NotNull String username, @NotNull String password, int port) {
    requireNonNull(host, "host is null");
    requireNonNull(database, "database is null");
    requireNonNull(username, "username is null");
    requireNonNull(password, "password is null");

    HikariConfig hikariConfig = getHikariConfig(host, database, username, password, port);

    try {
      hikariDataSource = new HikariDataSource(hikariConfig);
      connection = hikariDataSource.getConnection();
      dslContext = DSL.using(hikariDataSource, SQLDialect.MYSQL);
    } catch (Exception e) {
      logger.severe("An error occurred while connecting to the database");
      logger.severe("Now trying to connect to SQLite");
      connect();
    }
  }

  @Override
  public void connect() {
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      logger.severe("<red>┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
      logger.severe("<red>┃ The SQLite driver couldn't be found, contact nookure support             ┃");
      logger.severe("<red>┃ to get help with this issue.                                             ┃");
      logger.severe("<red>┃                                                                          ┃");
      logger.severe("<red>┃ This is a fatal error, the plugin will now disable itself.               ┃");
      logger.severe("<red>┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");

      throw new RuntimeException(e);
    }

    try {
      String url = "jdbc:sqlite:" + pluginDataFolder + "/database.db";
      connection = DriverManager.getConnection(url);
      dslContext = DSL.using(hikariDataSource, SQLDialect.MYSQL);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @NotNull
  private static HikariConfig getHikariConfig(@NotNull String host, @NotNull String database, @NotNull String username, @NotNull String password, int port) {
    HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setJdbcUrl(
        "jdbc:mysql://"
            + host
            + ":"
            + port
            + "/"
            + database
            + "?autoReconnect=true&useUnicode=yes");
    hikariConfig.setUsername(username);
    hikariConfig.setPassword(password);
    hikariConfig.setMaximumPoolSize(20);
    hikariConfig.setConnectionTimeout(30000);
    hikariConfig.setLeakDetectionThreshold(0);
    return hikariConfig;
  }

  @Override
  public @NotNull DSLContext getJOOQ() {
    if (dslContext == null) {
      throw new IllegalStateException("The connection is not established");
    }

    return dslContext;
  }

  @Override
  public @NotNull Connection getConnection() {
    if (connection == null) {
      throw new IllegalStateException("The connection is not established");
    }

    return connection;
  }

  @Override
  public void close() {
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    if (hikariDataSource != null) {
      hikariDataSource.close();
    }
  }
}