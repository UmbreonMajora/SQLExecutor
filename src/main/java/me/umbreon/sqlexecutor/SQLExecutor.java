package me.umbreon.sqlexecutor;

import me.umbreon.sqlexecutor.database.DatabaseConnection;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class SQLExecutor extends JavaPlugin {

    private DatabaseConnection databaseConnection;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.databaseConnection = new DatabaseConnection(this);
        Objects.requireNonNull(getCommand("sql")).setExecutor(this);
    }

    @Override
    public void onDisable() {
        databaseConnection.closeConnection();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            String sql = Arrays.stream(args).skip(1).collect(Collectors.joining(" "));
            sender.sendMessage("SQL: " + sql);
            try (Connection connection = databaseConnection.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql); ResultSet resultSet = preparedStatement.executeQuery()) {
                int columns = resultSet.getMetaData().getColumnCount();
                while (resultSet.next()) {
                    StringBuilder message = new StringBuilder();
                    for (int i = 1; i <= columns; i++) {
                        message.append(resultSet.getString(i));
                    }
                    sender.sendMessage(message.toString());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
