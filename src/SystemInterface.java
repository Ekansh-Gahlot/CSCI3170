import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.sql.*;

public class SystemInterface {
    private static final int EXIT_CHOICE = 5;

    public static void handle() {
        System.out.println("<This is the system interface.>");
        System.out.println("----------------------------------------");
        ChoiceSelector systemInterfaceMenuSelector = new ChoiceSelector()
                .addAction(1, "Create Table", SystemInterface::createTableSchemas)
                .addAction(2, "Delete Table", SystemInterface::deleteTableSchemas)
                .addAction(3, "Insert Data", SystemInterface::insertDataToDatabase)
                .addAction(4, "Set System Date", SystemInterface::systemDateSetting)
                .addAction(5, "Back to Main Menu", () -> {});
        int systemChoice;

        do {
            systemChoice = systemInterfaceMenuSelector.run();
        } while (systemChoice != EXIT_CHOICE);
    }

    private static void createTableSchemas() {
        Connection dbConnection = DatabaseManager.getConnection();
        try {
            String bookSql = "Create table book"
                    + "(ISBN CHAR(13) PRIMARY KEY,"
                    + "title VARCHAR(100) NOT NULL,"
                    + "unit_price INTEGER,"
                    + "no_of_copies INTEGER,"
                    + "CHECK (unit_price >=0),"
                    + "CHECK (no_of_copies >=0))";

            String customerSql = "Create table customer"
                    + "(customer_id VARCHAR(10) PRIMARY KEY,"
                    + "name VARCHAR(50) NOT NULL,"
                    + "shipping_address VARCHAR(200) NOT NULL,"
                    + "credit_card_no CHAR(19))";

            String ordersSql = "Create table orders"
                    + "(order_id CHAR(8) PRIMARY KEY,"
                    + "o_date DATE,"
                    + "shipping_status CHAR(1),"
                    + "charge INTEGER,"
                    + "customer_id VARCHAR(10) NOT NULL REFERENCES customer(customer_id),"
                    + "CHECK (charge >=0),"
                    + "CHECK (shipping_status='Y' OR shipping_status='N'))";

            String orderingSql = "Create table ordering"
                    + "(order_id CHAR(8) NOT NULL REFERENCES orders(order_id),"
                    + "ISBN CHAR(13) REFERENCES book(ISBN),"
                    + "quantity INTEGER,"
                    + "PRIMARY KEY (order_id , ISBN),"
                    + "CHECK (quantity>=0))";

            String bookauthorSql = "Create table book_author"
                    + "(ISBN CHAR(13) NOT NULL REFERENCES book(ISBN),"
                    + "author_name VARCHAR(50) NOT NULL,"
                    + "PRIMARY KEY (ISBN , author_name))";

            Statement stmt = dbConnection.createStatement();
            stmt.executeUpdate(bookSql);
            stmt.executeUpdate(customerSql);
            stmt.executeUpdate(ordersSql);
            stmt.executeUpdate(orderingSql);
            stmt.executeUpdate(bookauthorSql);

            System.out.println("Done! All Tables are created!");
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private static void deleteTableSchemas() {
        Connection dbConnection = DatabaseManager.getConnection();
        try {

            String delBook = "DROP TABLE book";
            String delCustomer = "DROP TABLE customer";
            String delOrders = "DROP TABLE orders";
            String delOrdering = "DROP TABLE ordering";
            String delBookAuthor = "DROP TABLE book_author";

            Statement stmt = dbConnection.createStatement();
            stmt.executeUpdate(delOrdering);
            stmt.executeUpdate(delBookAuthor);
            stmt.executeUpdate(delOrders);
            stmt.executeUpdate(delBook);
            stmt.executeUpdate(delCustomer);

            System.out.println("Done! All Tables are deleted.");
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private static void insertDataToDatabase() {
        Scanner scanner = new Scanner(System.in);
        Connection dbConnection = DatabaseManager.getConnection();

        System.out.println("Inserting data to the database...");
        try {
            System.out.println("Please enter the folder path\n");
            String path = scanner.nextLine().replace("\n", "");
            Path base = Paths.get(path);

            Path bookpath = base.resolve("book.txt");
            Path customerpath = base.resolve("customer.txt");
            Path orderspath = base.resolve("orders.txt");
            Path orderingpath = base.resolve("ordering.txt");
            Path bookauthorpath = base.resolve("book_author.txt");

            String insertBookSql = "INSERT IGNORE INTO book(ISBN, title, unit_price, no_of_copies) VALUES (?, ?, ?, ?)";
            String insertCustomerSql = "INSERT IGNORE INTO customer(customer_id, name, shipping_address, credit_card_no) VALUES (?, ?, ?, ?)";
            String insertOrdersSql = "INSERT IGNORE INTO orders(order_id, o_date, shipping_status, charge, customer_id) VALUES (?, ?, ?, ?, ?)";
            String insertOrderingSql = "INSERT IGNORE INTO ordering(order_id, ISBN, quantity) VALUES (?, ?, ?)";
            String insertBookAuthorSql = "INSERT IGNORE INTO book_author(ISBN, author_name) VALUES (?, ?)";

            PreparedStatement bookStmt = dbConnection.prepareStatement(insertBookSql);
            PreparedStatement customerStmt = dbConnection.prepareStatement(insertCustomerSql);
            PreparedStatement ordersStmt = dbConnection.prepareStatement(insertOrdersSql);
            PreparedStatement orderingStmt = dbConnection.prepareStatement(insertOrderingSql);
            PreparedStatement bookAuthorStmt = dbConnection.prepareStatement(insertBookAuthorSql);

            readAndExecute(bookpath, bookStmt);
            readAndExecute(customerpath, customerStmt);
            readAndExecute(orderspath, ordersStmt);
            readAndExecute(orderingpath, orderingStmt);
            readAndExecute(bookauthorpath, bookAuthorStmt);

            System.out.println("Data loaded successfully.");
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private static void readAndExecute(Path path, PreparedStatement stmt) throws IOException, SQLException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\\|");
                for (int i = 0; i < data.length; i++) {
                    stmt.setString(i + 1, data[i].trim());
                }
                stmt.addBatch();
            }
        }
        stmt.executeBatch();
    }

    private static void systemDateSetting() {
        Connection dbConnection = DatabaseManager.getConnection();

        LocalDate orderDate = null;
        String pattern = "yyyyMMdd";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        try {
            Statement stmt = dbConnection.createStatement();

            String query = "SELECT MAX(o_date) AS order_date FROM orders";
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                orderDate = rs.getDate("order_date").toLocalDate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error: " + e.getMessage());
            return;
        }

        Scanner s = new Scanner(System.in);
        while (true) {
            LocalDate inputDate;

            System.out.print("Please Input the date (YYYYMMDD): ");
            try {
                inputDate = formatter.parse(s.nextLine(), LocalDate::from);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid Format! Please Try again!");
                continue;
            }

            if (inputDate.isBefore(MainApplication.getSystemDate())) {
                System.out.println("Invalid input: Input Date earlier than the Original Date");
            } else if (inputDate.isBefore(orderDate)) {
                System.out.println("Invalid input: Input Date earlier than the Latest Order Date");
            } else {
                MainApplication.setSystemDate(inputDate);
                System.out.println("System date set to: " + formatter.format(inputDate));
                break;
            }
        }
    }
}
