import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class DatabaseManager {

    private static final String dbAddress = "jdbc:oracle:thin://@db18.cse.cuhk.edu.hk:1521/oradb.cse.cuhk.edu.hk";
    private static String USER = "";
    private static String PASSWORD = "";

    private static Connection connection;

    static {
        // Read credentials from file
        try {
            File credentialsFile = new File("credentials");
            Scanner credentialsReader = new Scanner(credentialsFile);
            USER = credentialsReader.nextLine();
            PASSWORD = credentialsReader.nextLine();
            credentialsReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Did you forget to create the credentials file under ./src, or does it contain at least 2 lines?");
            throw new RuntimeException("Error reading credentials file");
        }
        
        // Get class from oracle driver
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error loading Oracle JDBC driver", e);
        }
    }

    public static Connection createConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(dbAddress, USER, PASSWORD);
        }
        return connection;
    }

    public static Connection getConnection() {
        return connection;
    }

    public static ResultSet executeStatement(String statement, List<String> parameters){
        PreparedStatement preStatement;
        try{
            // Connection con = connectToSQL();
            Connection connection = getConnection();
            preStatement = connection.prepareStatement(statement);
            int i = 1;
            for (String s : parameters) {
                preStatement.setString(i++, s);
            }
            return preStatement.executeQuery();
        }
        catch (SQLException e) {
            // System.out.println("An error occurred: " + e.getMessage());
            // System.out.println("Statement was: " + statement);
            return null;
        }
    }

    public static ResultSet executeStatement(String statement, String... parameters) {
        return executeStatement(statement, Arrays.asList(parameters));
    }
}

