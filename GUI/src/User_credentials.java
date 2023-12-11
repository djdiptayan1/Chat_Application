import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class User_credentials {
    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/chat_database"; // Modify the database name if needed
    private static final String DB_USER = "root"; // Replace with your MySQL username
    private static final String DB_PASSWORD = "Djsd@2611"; // Replace with your MySQL password

    public static void main(String[] args) {
        try {
            // Create a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Insert user credentials
            insertUserCredentials(connection, "Diptayan", "12");
            insertUserCredentials(connection, "Palash", "12");
            insertUserCredentials(connection, "Srijit", "12");

            // Close the connection
            connection.close();

            System.out.println("User credentials inserted into the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertUserCredentials(Connection connection, String username, String password) throws SQLException {
        String insertSQL = "INSERT INTO users (username, password) VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }
}
