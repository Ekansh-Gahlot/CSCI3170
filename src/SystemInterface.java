import java.util.Scanner;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemInterface {
    private static final int EXIT_CHOICE = 5;
    private static final String[] SYSTEM_INTERFACE_CHOICES = {
            "Create Table",
            "Delete Table",
            "Insert Data",
            "Set System Date",
            "Back to Main Menu",
    };

    public static void handleSystemInterface(Connection dbConnection) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("<This is the system interface.>");
        System.out.println("----------------------------------------");
        ChoiceSelector systemInterfaceMenuSelector = new ChoiceSelector(SYSTEM_INTERFACE_CHOICES);
        int systemChoice;

        do {
            systemChoice = systemInterfaceMenuSelector.getChoice();

            switch (systemChoice) {
                case 1:
                    createTableSchemas(dbConnection);
                    break;
                case 2:
                    deleteTableSchemas(dbConnection);
                    break;
                case 3:
                    insertDataToDatabase(dbConnection, scanner);
                    break;
                case 4:
                    systemDateSetting(dbConnection);
                    break;
                case EXIT_CHOICE:
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

        } while (systemChoice != EXIT_CHOICE);
    }

    private static void createTableSchemas(Connection dbConnection) {
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

    private static void deleteTableSchemas(Connection dbConnection) {
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

    private static void insertDataToDatabase(Connection dbConnection, Scanner scanner) {
        System.out.println("Inserting data to the database...");
        try {
            
            System.out.println("Please enter the folder path\n");
            String path = scanner.nextLine().replace("\n", "");
            
            String bookpath = path + "/book.csv";
            String customerpath = path + "/customer.csv";
            String orderspath = path + "/orders.csv";
            String orderingpath = path + "/ordering.csv";
            String bookauthorpath = path + "/book_author.csv";
            
            // TODO
            String insertBookSql = "LOAD DATA INFILE '" + bookpath + "' INTO TABLE book" 
                    + " FIELDS TERMINATED BY ','" 
                    + " LINES TERMINATED BY '\\n'" 
                    + " (ISBN, title, unit_price, no_of_copies)";

            String insertCustomerSql = "LOAD DATA INFILE '" + customerpath + "' INTO TABLE customer" 
                    + " FIELDS TERMINATED BY ','" 
                    + " LINES TERMINATED BY '\\n'" 
                    + " (customer_id, name, shipping_address, credit_card_no)";

            String insertOrdersSql = "LOAD DATA INFILE '" + orderspath + "' INTO TABLE orders" 
                    + " FIELDS TERMINATED BY ','" 
                    + " LINES TERMINATED BY '\\n'" 
                    + " (order_id, o_date, shipping_status, charge, customer_id)";

            String insertOrderingSql = "LOAD DATA INFILE '" + orderingpath + "' INTO TABLE ordering" 
                    + " FIELDS TERMINATED BY ','" 
                    + " LINES TERMINATED BY '\\n'" 
                    + " (order_id, ISBN, quantity)";

            String insertBookAuthorSql = "LOAD DATA INFILE '" + bookauthorpath + "' INTO TABLE book_author" 
                    + " FIELDS TERMINATED BY ','" 
                    + " LINES TERMINATED BY '\\n'" 
                    + " (ISBN, author_name)";

            Statement stmt = dbConnection.createStatement();
            stmt.executeUpdate(insertBookSql);
            stmt.executeUpdate(insertCustomerSql);
            stmt.executeUpdate(insertOrdersSql);
            stmt.executeUpdate(insertOrderingSql);
            stmt.executeUpdate(insertBookAuthorSql);

            System.out.println("Data loaded successfully.");
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private static void systemDateSetting(Connection dbConnection) {
        Date orderDate = new Date();
        Date inputDate = new Date();
        String pattern = "yyyyMMdd";
        SimpleDateFormat ft = new SimpleDateFormat(pattern);
        boolean flag = false;

        try {
            Statement stmt = dbConnection.createStatement();

            String query = "SELECT MAX(o_date) AS order_date FROM orders";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                orderDate = rs.getDate("order_date");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error: " + e.getMessage());
        }

        Scanner s = new Scanner(System.in);
        do {
            flag = false;
            System.out.print("Please Input the date (YYYYMMDD) : ");
            try {
                inputDate = ft.parse(s.nextLine());
            } catch (ParseException e) {
                System.out.println("Invalid Format! Please Try again!");
                flag = true;
            }

            if (inputDate.before(new Date())) {
                System.out.println("Invalid input: Input Date earlier than the Original Date");
                flag = true;
            } else if (orderDate.after(inputDate)) {
                System.out.println("Invalid input: Input Date earlier than the Latest Order Date");
                flag = true;
            }
        } while (flag);

        s.close();

        System.out.println("System date set to: " + ft.format(inputDate));
    }
}
