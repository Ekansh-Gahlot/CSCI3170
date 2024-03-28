import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.System.err;
import static java.lang.System.out;


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
            while (selector.run(scanner) != EXIT_CHOICE) {

            }
        } catch (Exception e) {
            err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private static void updateOrderStatus() {
        try {
            String orderId;
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
                out.println("The order has already been shipped.");
            } else {
                sql = "SELECT COUNT(*) as bookCount FROM ordering WHERE order_id = ? AND quantity > 0";
                ResultSet resultSet2 = DatabaseManager.executeStatement(sql, sqlParameters); // Use DatabaseManager here
                resultSet2.next();
                numberOfBooks = resultSet2.getInt("bookCount");

                if (numberOfBooks == 0) {
                    out.println("There are no books in the order. Please send a reminder to the user.");
                    return;
                }

                out.println("The shipping status of Order " + orderIdentifier + " is " + shippingStatus
                        + " and " + numberOfBooks + " books have been ordered.");

                out.println("Would you like to update the shipping status? (Y/N)");
                String userResponse = scanner.nextLine().trim();

                if (userResponse.equalsIgnoreCase("Y")) {
                    sql = "UPDATE orders SET shipping_status = 'Y' WHERE order_id = ?";
                    DatabaseManager.executeStatement(sql, sqlParameters); // Use DatabaseManager here
                    out.println("The shipping status has been updated.");
                }
            }
        } catch (SQLException e) {
            out.println("[Error]: " + e.getMessage());
        }
    }

    private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
    private static void orderQuery() {
        String[] yearMonth = InputHandler.getValidYearMonth(scanner).split("-");
        String year = yearMonth[0], month = yearMonth[1];

        String sql = "SELECT * FROM orders\n" +
                     "WHERE shipping_status = 'Y' \n" +
                     "AND extract (YEAR from o_date)  = ? \n" + // thank you Oracle
                     "AND extract (MONTH from o_date)  = ?\n" +
                     "ORDER BY order_id ASC";

        try (ResultSet r = DatabaseManager.executeStatement(sql, year, month)) {
            int record = 1;
            while (r.next()) {
                out.println();
                out.println("Record: " + (record++));
                out.println("OrderID: " + r.getString("order_id"));
                out.println("CustomerID: " + r.getString("customer_id"));
                out.println("OrderDate: " + DF.format(r.getDate("o_date")));
                out.println("charge: " + r.getInt("charge"));
            }
        } catch (SQLException e) {
            err.println("[Error]: " + e);
        }
    }

    private static void nMostPopularBookQuery() {
        int n = InputHandler.getValidNBookNum(scanner);

        String sql = "SELECT o.ISBN as \"ISBN\", b.title as \"title\", SUM(o.quantity) as \"copies\" \n" +
                     "FROM ordering o INNER JOIN book b ON o.isbn = b.isbn\n" +
                     "GROUP BY o.isbn, b.title\n" +
                     "ORDER BY SUM(o.quantity) DESC";

        try (ResultSet r = DatabaseManager.executeStatement(sql)) {
            out.println("ISBN            Title                Copies");

            int rank = 0;
            int rankAmt = Integer.MAX_VALUE;

            while (r.next()) {
                int copies = r.getInt("copies");
                if (rank == n && rankAmt != copies) {
                    // rank > n and not the same quantity as the current rank
                    break;
                } else if (rankAmt > copies) {
                    // increment rank
                    rank++;
                    rankAmt = copies;
                } // else rankAmt == copies
                String isbn = r.getString("ISBN");
                String title = r.getString("title");
                out.println(isbn + "    " + title + "    " + copies);
            }
        } catch (SQLException e) {
            err.println("[Error]: " + e);
        }
    }
}
