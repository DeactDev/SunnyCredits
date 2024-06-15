package live.deact.sunnycredits.managers;

import live.deact.sunnycredits.SunnyCredits;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseManager {

    private final SunnyCredits plugin;
    private Connection connection;

    public DatabaseManager(SunnyCredits plugin, String host, int port, String database, String user, String password) {
        this.plugin = plugin;
        connect(host, port, database, user, password);
        setupDatabase();
    }

    private void connect(String host, int port, String database, String user, String password) {
        try {
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupDatabase() {
        try (PreparedStatement statement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS credits (uuid VARCHAR(36) PRIMARY KEY, balance DOUBLE)"
        )) {
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public double getBalance(UUID uuid) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT balance FROM credits WHERE uuid = ?")) {
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addCredits(UUID uuid, double amount) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO credits (uuid, balance) VALUES (?, ?) ON DUPLICATE KEY UPDATE balance = balance + ?"
        )) {
            statement.setString(1, uuid.toString());
            statement.setDouble(2, amount);
            statement.setDouble(3, amount);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeCredits(UUID uuid, double amount) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE credits SET balance = balance - ? WHERE uuid = ?"
        )) {
            statement.setDouble(1, amount);
            statement.setString(2, uuid.toString());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setBalance(UUID uuid, double amount) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO credits (uuid, balance) VALUES (?, ?) ON DUPLICATE KEY UPDATE balance = ?"
        )) {
            statement.setString(1, uuid.toString());
            statement.setDouble(2, amount);
            statement.setDouble(3, amount);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void resetDatabase() {
        try (PreparedStatement statement = connection.prepareStatement(
                "TRUNCATE TABLE credits"
        )) {
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isPlayerRegistered(UUID uuid) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM credits WHERE uuid = ?")) {
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void registerPlayer(UUID uuid) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO credits (uuid, balance) VALUES (?, ?) ON DUPLICATE KEY UPDATE balance = balance"
        )) {
            statement.setString(1, uuid.toString());
            statement.setDouble(2, 0);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}