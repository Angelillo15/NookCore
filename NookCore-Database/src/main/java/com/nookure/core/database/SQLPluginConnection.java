package com.nookure.core.database;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nookure.core.annotation.PluginDataFolder;
import com.nookure.core.database.annotation.EbeanDatabaseName;
import com.nookure.core.database.annotation.EntityClassMappings;
import com.nookure.core.database.config.DataProvider;
import com.nookure.core.database.config.DatabaseConfig;
import com.nookure.core.logger.Logger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.ContainerConfig;
import io.ebean.datasource.DataSourceConfig;
import io.ebean.platform.mysql.MySqlPlatform;
import io.ebean.platform.sqlite.SQLitePlatform;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;

@Singleton
public class SQLPluginConnection extends AbstractSQLPluginConnection {
  @Inject
  private Logger logger;
  @Inject
  private AtomicReference<Database> databaseReference;
  @Inject
  @EbeanDatabaseName
  private String ebeanDatabaseName;
  @Inject
  @EntityClassMappings
  private List<Class<?>> entityClassMappings;
  @Inject
  @PluginDataFolder
  private Path pluginDataFolder;
  private ClassLoader classLoader;
  private Connection connection;
  private HikariDataSource hikariDataSource;
  private Database database;

  @Override
  public void connect(@NotNull DatabaseConfig config, ClassLoader classLoader) {
    requireNonNull(config, "config is null");

    this.classLoader = classLoader;

    logger.debug("Connecting to the database");
    logger.debug("Debug database data: " + config);

    if (connection != null) {
      return;
    }

    if (config.getType() == DataProvider.MYSQL)
      loadMySQL(config);
    else
      loadSqlite(config);

    databaseReference.set(database);
    logger.info("<green>Successfully connected to the database!");
  }

  private void loadMySQL(@NotNull DatabaseConfig config) {
    requireNonNull(config, "config is null");

    HikariConfig hikariConfig = getHikariConfig(config);

    try {
      hikariDataSource = new HikariDataSource(hikariConfig);
      connection = hikariDataSource.getConnection();

      loadEbean(config);
    } catch (Exception e) {
      logger.severe("An error occurred while connecting to the database");
      logger.severe("Now trying to connect to SQLite");
      logger.severe(e);
      loadSqlite(config);
    }
  }

  private void loadEbean(DatabaseConfig config) {
    io.ebean.config.DatabaseConfig ebeanConfig = getDatabaseConfig(config);
    Thread.currentThread().setContextClassLoader(classLoader);
    database = DatabaseFactory.create(ebeanConfig);
  }

  @NotNull
  private io.ebean.config.DatabaseConfig getDatabaseConfig(DatabaseConfig config) {
    DataSourceConfig dataSourceConfig = new DataSourceConfig();
    dataSourceConfig.setUrl(hikariDataSource.getJdbcUrl());

    if (config.getType() == DataProvider.MYSQL) {
      dataSourceConfig.setUsername(hikariDataSource.getUsername());
      dataSourceConfig.setPassword(hikariDataSource.getPassword());
    }

    dataSourceConfig.setName(ebeanDatabaseName);

    io.ebean.config.DatabaseConfig ebeanConfig = new io.ebean.config.DatabaseConfig();

    ebeanConfig.loadFromProperties();

    ContainerConfig containerConfig = new ContainerConfig();
    containerConfig.setActive(false);

    ebeanConfig.setDataSourceConfig(dataSourceConfig);

    ebeanConfig.setName(ebeanDatabaseName);
    ebeanConfig.setRunMigration(true);
    ebeanConfig.setClasses(entityClassMappings);
    ebeanConfig.setDataSource(hikariDataSource);
    ebeanConfig.setDefaultServer(true);

    if (config.getType() == DataProvider.MYSQL) {
      ebeanConfig.setDatabasePlatform(new MySqlPlatform());
    } else {
      ebeanConfig.setDatabasePlatform(new SQLitePlatform());
    }

    return ebeanConfig;
  }

  @NotNull
  private HikariConfig getHikariConfig(@NotNull DatabaseConfig config) {
    requireNonNull(config, "config is null");

    HikariConfig hikariConfig = new HikariConfig();
    if (config.getType() == DataProvider.MYSQL) {
      hikariConfig.setJdbcUrl(
          "jdbc:mysql://"
              + config.getHost()
              + ":"
              + config.getPort()
              + "/"
              + config.getDatabase()
              + "?autoReconnect=true&useUnicode=yes");
      hikariConfig.setUsername(config.getUsername());
      hikariConfig.setPassword(config.getPassword());
    } else {
      hikariConfig.setJdbcUrl("jdbc:sqlite:" + pluginDataFolder + "/database.db");
    }

    hikariConfig.setMaximumPoolSize(20);
    hikariConfig.setConnectionTimeout(30000);
    hikariConfig.setAutoCommit(false);
    hikariConfig.setLeakDetectionThreshold(0);
    return hikariConfig;
  }

  private void loadSqlite(DatabaseConfig config) {
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
      hikariDataSource = new HikariDataSource(getHikariConfig(config));

      loadEbean(config);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void reload(@NotNull DatabaseConfig config, @NotNull ClassLoader classLoader) {
    close();
    connect(config, classLoader);
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

    if (database != null) {
      database.shutdown(true, false);
    }
  }

  @Override
  public @NotNull Connection getConnection() {
    if (connection == null) {
      throw new IllegalStateException("The connection is not established");
    }

    return connection;
  }

  @Override
  public Database getEbeanDatabase() {
    if (connection == null) {
      throw new IllegalStateException("The connection is not established");
    }

    return database;
  }
}
