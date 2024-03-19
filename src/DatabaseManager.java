import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
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
            throw new RuntimeException("Error loading MySQL JDBC driver", e);
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

    static public Connection connectToSQL() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(dbAddress, USER, PASSWORD);
            System.out.println("Connection Success\n\n");
            return con;
        } catch (ClassNotFoundException e) {
            System.out.println("[ERROR] Java MySQL DB Driver not found.");
            System.exit(0);
        } catch (SQLException e) {
            System.out.println(e);
            System.exit(0);
        }
        return null;
    }
    static public ResultSet runSQL(String sql, ArrayList<String> sqlParms) {
        PreparedStatement prestmt;
        ResultSet r;

        try {
            Connection con = connectToSQL();
            prestmt = con.prepareStatement(sql);
            int i = 1;
            for (String s : sqlParms) {
                prestmt.setString(i, s);
                i++;
            }
            r = prestmt.executeQuery();

            prestmt.close();
            con.close();
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
        return r;
    }
}

