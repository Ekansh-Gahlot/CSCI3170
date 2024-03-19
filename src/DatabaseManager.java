import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:oracle:thin://@db18.cse.cuhk.edu.hk:1521/oradb.cse.cuhk.edu.hk";
    private static String USER = "";
    private static String PASSWORD = "";

    private static Connection connection;

    static {
        try {
            File credentialsFile = new File("credentials");
            Scanner credentialsReader = new Scanner(credentialsFile);
            USER = credentialsReader.nextLine();
            PASSWORD = credentialsReader.nextLine();
            credentialsReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error reading credentials file");
        }
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Error loading MySQL JDBC driver");
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        }
        return connection;
    }

    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
