package me.umbreon.sqlexecutor.database;

import me.umbreon.sqlexecutor.SQLExecutor;

import org.bukkit.Bukkit;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;

public class DatabaseConnection {

    private BasicDataSource dataSource;
    private final SQLExecutor SQLExecutor;

    public DatabaseConnection(SQLExecutor SQLExecutor) {
        this.SQLExecutor = SQLExecutor;
        startup();
    }

    public void startup() {

        String host = SQLExecutor.getConfig().getString("database.host");
        String port = SQLExecutor.getConfig().getString("database.port");
        String database = SQLExecutor.getConfig().getString("database.database");
        String username = SQLExecutor.getConfig().getString("database.username");
        String password = SQLExecutor.getConfig().getString("database.password");

        int poolSize = 20;

        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setMaxTotal(poolSize);
        dataSource.setMaxIdle((poolSize > 4) ? ((Double) Math.ceil(poolSize / 4)).intValue() : 1);
        dataSource.setMinIdle(5);
        dataSource.setUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?verifyServerCertificate=false&useSSL=true");
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        dataSource.setSoftMinEvictableIdleTimeMillis(500);
        dataSource.setTimeBetweenEvictionRunsMillis(100);
        dataSource.setNumTestsPerEvictionRun(2);
        dataSource.setTestWhileIdle(true);
        dataSource.setMinEvictableIdleTimeMillis(60 * 1000);
        dataSource.setDefaultQueryTimeout(10);
        dataSource.setRemoveAbandonedTimeout(800);
        dataSource.setRemoveAbandonedOnMaintenance(true);

        try(Connection connection = dataSource.getConnection()) {

            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS onlinetimetracker_table (\n"
                    + "uuid VARCHAR(50) PRIMARY KEY,\n"
                    + "playtime VARCHAR(50) NOT NULL,\n"
                    + "name VARCHAR(50) NOT NULL\n"
                    + ")"
            );

        } catch (SQLException e) {
            Bukkit.getLogger().info(e.toString());
        }
    }

    public void closeConnection() {
        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            return null;
        }
    }

}