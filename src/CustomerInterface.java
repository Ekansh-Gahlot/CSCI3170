import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class CustomerInterface {
    private static Scanner scanner;
    private static final int EXIT_CHOICE = 5;

    private static final int UNIT_SHIPPING_CHARGE = 10;
    private static final int HANDLING_CHARGE = 10;

    public static Runnable handle(Scanner scanner_) {
        scanner = scanner_;
        return CustomerInterface::handle;
    }

    public static void handle() {
        try {
            // customer interface logic here
            // For example, bookSearch()
            System.out.println("<This is the system interface.>");
            System.out.println("----------------------------------------");
            ChoiceSelector selector = new ChoiceSelector()
                    .addAction(1, "Book search", CustomerInterface::bookSearch)
                    .addAction(2, "Order Creation", CustomerInterface::orderCreation)
                    .addAction(3, "Order Altering", CustomerInterface::orderAltering)
                    .addAction(4, "Order Query", CustomerInterface::orderQuery)
                    .addAction(EXIT_CHOICE, "Back to main menu", () -> {
                    });

            while (selector.run(scanner) != EXIT_CHOICE) {
            }
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private static void printBookResults(ResultSet[] bookResults) {
        System.out.println();
        int count = 1;
        for (ResultSet bookResult : bookResults) {
            try {
                while (bookResult.next()) {
                    ResultSet bookInfo = TableHandler.bookTableHandler
                            .selectRecordByKey(new String[] { bookResult.getString("ISBN") });
                    if (bookInfo.next()) {
                        // print book info
                        System.out.println("Record " + count++);
                        System.out.println("ISBN: " + bookInfo.getString("ISBN"));
                        System.out.println("Book Title:" + bookInfo.getString("title"));
                        System.out.println("Unit Price:" + bookInfo.getString("unit_price"));
                        System.out.println("No of Available:" + bookInfo.getString("no_of_copies"));

                        // print authors
                        ResultSet authors = TableHandler.bookAuthorTableHandler.selectRecords("ISBN = ?",
                                new String[] { bookInfo.getString("ISBN") }, "author_name ASC");
                        System.out.println("Authors:");
                        int authorCount = 1;
                        while (authors.next()) {
                            System.out.println(authorCount++ + ". " + authors.getString("author_name"));
                        }
                        System.out.println();
                    }
                }
            } catch (SQLException e) {
                System.out.println("An error occurred while printing book results: " + e.getMessage());
            }
        }
    }

    private static final int BOOK_QUERY_EXIT_CHOICE = 4;

    private static void bookSearch() {
        String sortingOrder = "book.title ASC, book.ISBN ASC";
        String sortingOrderSQL = " ORDER BY " + sortingOrder;

        Runnable SearchByISBN = () -> {
            ResultSet bookResults[] = new ResultSet[1];
            String ISBN = InputHandler.getValidISBN(scanner);
            bookResults[0] = TableHandler.bookTableHandler.selectRecordByKey(new String[] { ISBN });
            printBookResults(bookResults);
        };

        Runnable SearchByBookTitle = () -> {
            ResultSet bookResults[] = new ResultSet[2];
            String exactBookTitle = InputHandler.getValidBookTitle(scanner);
            bookResults[0] = TableHandler.bookTableHandler.selectRecords("title = ?", new String[] { exactBookTitle },
                    sortingOrder);

            String partialBookTitle = InputHandler.getValidPartialString(exactBookTitle);
            bookResults[1] = TableHandler.bookTableHandler.selectRecords("title LIKE ? AND title <> ?",
                    new String[] { partialBookTitle, exactBookTitle }, sortingOrder);

            printBookResults(bookResults);
        };

        Runnable SearchByAuthorName = () -> {
            ResultSet bookResults[] = new ResultSet[2];
            String exactAuthorName = InputHandler.getValidAuthorName(scanner);
            String exactSearchSQL = "SELECT ISBN FROM book WHERE ISBN IN (SELECT book2.ISBN FROM book_author, book book2 WHERE book2.ISBN = book_author.ISBN AND book_author.author_name = ?)"
                    + sortingOrderSQL; // Yes, I know this is a bit of a mess, but the sub-query is required to get
                                       // distinct ISBNs
            bookResults[0] = DatabaseManager.executeStatement(exactSearchSQL, new ArrayList<String>() {
                {
                    add(exactAuthorName);
                }
            });
            String partialAuthorName = InputHandler.getValidPartialString(exactAuthorName);
            String partialSearchSQL = "SELECT ISBN FROM book WHERE ISBN IN (SELECT book2.ISBN FROM book_author, book book2 WHERE book2.ISBN = book_author.ISBN AND book_author.author_name LIKE ? AND book_author.author_name <> ?)"
                    + sortingOrderSQL; // same mess as above
            bookResults[1] = DatabaseManager.executeStatement(partialSearchSQL, new ArrayList<String>() {
                {
                    add(partialAuthorName);
                    add(exactAuthorName);
                }
            });
            printBookResults(bookResults);
        };

        ChoiceSelector querySelector = new ChoiceSelector("Your choice?...", null)
                .addAction(1, "ISBN", SearchByISBN)
                .addAction(2, "Book Title", SearchByBookTitle)
                .addAction(3, "Author Name", SearchByAuthorName)
                .addAction(BOOK_QUERY_EXIT_CHOICE, "Exit", () -> {
                });
        querySelector.run(scanner);
    }

    private static class Order {
        public String ISBN;
        public int quantity;

        public Order(String ISBN, int quantity) {
            this.ISBN = ISBN;
            this.quantity = quantity;
        }
    }

    private static void orderCreation() {
        String customerID = InputHandler.getValidCustomerID(scanner);

        // creates a new order
        final int[] thisOrderID = { 0 };
        ResultSet latestOrderResult = TableHandler.orderTableHandler.selectRecords(null,
                new String[] { "max(order_id)" }, new String[] {}, null);
        try {
            if (latestOrderResult.next()) {
                thisOrderID[0] = Integer.parseInt(latestOrderResult.getString(1)) + 1;
            }
        } catch (Exception e) {
            System.out.println("An error occurred while getting the latest order ID: " + e.getMessage());
            return;
        }
        Boolean orderResult = TableHandler.orderTableHandler.insertRecord("(?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ?)",
                new String[] {
                        String.format("%08d", thisOrderID[0]),
                        MainApplication.getSystemDateString(),
                        "N",
                        "0",
                        customerID
                });

        if (!orderResult) {
            System.out.println("An error occurred while creating the order. Please try again.");
            return;
        }

        // continue with ordering books
        System.out.println(">> What books do you want to order??");
        System.out.println(">> Input ISBN and then the quantity");
        System.out.println(">> You can press \"L\" to see ordered list, or \"F\" to finish ordering");

        String bookOrderInput;

        ArrayList<Order> orders = new ArrayList<>();

        int totalBookPrice = 0, totalBookQuantity = 0;
        while (!(bookOrderInput = InputHandler.getValidOrderInput(scanner)).equals(InputHandler.FINISH_ORDER)) {
            switch (bookOrderInput) {
                case InputHandler.LIST_ORDER:
                    // show ordered list
                    System.out.println("ISBN:            Number: ");
                    for (Order order : orders) {
                        System.out.println(order.ISBN + "      " + order.quantity);
                    }
                    break;
                default:
                    // order book
                    String ISBN = bookOrderInput;
                    int quantity = InputHandler.getValidOrderBookQuantity(scanner, ISBN);
                    if (quantity == -1) { // error occurred, skip this order
                        return;
                    }
                    Boolean insertResult = false;
                    try {
                        insertResult = TableHandler.orderingTableHandler.insertRecord(
                                String.format("%08d", thisOrderID[0]),
                                ISBN,
                                String.valueOf(quantity));
                    } catch (Exception e) {
                        System.out.println("An error occurred creating order: " + e.getMessage());
                    }

                    if(insertResult){
                        ResultSet selectedBook = TableHandler.bookTableHandler.selectRecordByKey(new String[] { ISBN });
                        try{
                            if (selectedBook.next()) {
                                orders.add(new Order(ISBN, quantity));
                                
                                // add up the charge
                                int unitPrice = selectedBook.getInt("unit_price");
                                totalBookPrice += unitPrice * quantity;
                                totalBookQuantity += quantity;
                            }
                        }catch(SQLException e){
                            System.out.println("An error occurred while getting book info: " + e.getMessage());
                        }
                    }
                    break;
            }
        }
        // clean up by calculating the charge for this order
        if (totalBookQuantity > 0) {
            int shippingPrice = totalBookQuantity * UNIT_SHIPPING_CHARGE + HANDLING_CHARGE;
            int totalPrice = totalBookPrice + shippingPrice;
            Boolean updateResult = TableHandler.orderTableHandler.updateRecordByKey(new String[] { "charge" },
                    new String[] { String.valueOf(totalPrice) },
                    new String[] { String.format("%08d", thisOrderID[0]) });
            if (!updateResult) {
                System.out.println("An error occurred while updating the order charge.");
            }
        }
    }

    private static void orderAltering() {

    }

    private static void orderQuery() {

    }
}
