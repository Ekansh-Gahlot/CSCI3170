import java.sql.*;
import java.util.*;


public class BookstoreInterface {
    private static Scanner scanner;
    private static final int EXIT_CHOICE = 4;

    public static Runnable handle(Scanner scanner_) {
        scanner = scanner_;
        return BookstoreInterface::handle;
    }

    public static void handle() {
        ChoiceSelector selector = new ChoiceSelector()
                .addAction(1, "Order Update", BookstoreInterface::updateOrderStatus)
                .addAction(2, "Order Query", BookstoreInterface::orderQuery)
                .addAction(3, "N most popular book query", BookstoreInterface::nMostPopularBookQuery)
                .addAction(EXIT_CHOICE, "Back to main menu", () -> {});

        try {
            // Bookstore interface logic here
            updateOrderStatus();

            while (selector.run(scanner) != EXIT_CHOICE) {

            }
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private static void updateOrderStatus() {
        try {
            int orderId;
            int numberOfBooks;
            String shippingStatus;
            String orderIdentifier;

            orderId = InputHandler.getValidOrderID(scanner);
            String sql = "SELECT shipping_status, order_id FROM orders WHERE order_id = ?";
            ArrayList<String> sqlParameters = new ArrayList<>();
            sqlParameters.add(String.valueOf(orderId));

            ResultSet resultSet = DatabaseManager.executeStatement(sql, sqlParameters);
            resultSet.next();
            shippingStatus = resultSet.getString("shipping_status");
            orderIdentifier = resultSet.getString("order_id");

            if (shippingStatus.equals("Y")) {
                System.out.println("The order has already been shipped.");
            } else {
                sql = "SELECT COUNT(*) as bookCount FROM ordering WHERE order_id = ? AND quantity > 0";
                ResultSet resultSet2 = DatabaseManager.executeStatement(sql, sqlParameters); // Use DatabaseManager here
                resultSet2.next();
                numberOfBooks = resultSet2.getInt("bookCount");

                if (numberOfBooks == 0) {
                    System.out.println("There are no books in the order. Please send a reminder to the user.");
                    return;
                }

                System.out.println("The shipping status of Order " + orderIdentifier + " is " + shippingStatus
                        + " and " + numberOfBooks + " books have been ordered.");

                System.out.println("Would you like to update the shipping status? (Y/N)");
                String userResponse = scanner.nextLine().trim();

                if (userResponse.equalsIgnoreCase("Y")) {
                    sql = "UPDATE orders SET shipping_status = 'Y' WHERE order_id = ?";
                    DatabaseManager.executeStatement(sql, sqlParameters); // Use DatabaseManager here
                    System.out.println("The shipping status has been updated.");
                }
            }
        } catch (SQLException e) {
            System.out.println("[Error]: " + e.getMessage());
        }
    }

    private static void orderQuery() {
    }

    private static void nMostPopularBookQuery() {
    }
}
