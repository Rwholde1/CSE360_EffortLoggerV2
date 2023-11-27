package application;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/Library";
    private static final String USERNAME = "hunterfunk";
    private static final String PASSWORD = "Nugget10f!";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }

}
