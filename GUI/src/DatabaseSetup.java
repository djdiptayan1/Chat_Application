import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {
    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_USER = "root"; // Replace with your MySQL username
    private static final String DB_PASSWORD = "Djsd@2611"; // Replace with your MySQL password
    private static final String DB_NAME = "chat_database"; // Modify the database name if needed

    public static void main(String[] args) {
        try {
            // Create a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Create a new database
            createDatabase(connection);

            // Create a table to store user credentials
            createUsersTable(connection);

            // Close the connection
            connection.close();

            System.out.println("Database setup complete.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createDatabase(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();

        // Create the database if it doesn't exist
        String createDBSQL = "CREATE DATABASE IF NOT EXISTS " + DB_NAME;
        statement.executeUpdate(createDBSQL);

        statement.close();
    }

    private static void createUsersTable(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();

        // Use the newly created database
        String useDBSQL = "USE " + DB_NAME;
        statement.executeUpdate(useDBSQL);

        // Create a table to store user credentials
        String createUserTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(255) NOT NULL," +
                "password VARCHAR(255) NOT NULL)";
        statement.executeUpdate(createUserTableSQL);

        statement.close();
    }
}
