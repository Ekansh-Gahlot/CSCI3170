import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    private static class Ordering {
        public String ISBN;
        public int quantity;

        public Ordering(String ISBN, int quantity) {
            this.ISBN = ISBN;
            this.quantity = quantity;
        }
    }

    private static Boolean updateOrderCharge(String orderID) {
        ResultSet orderingResult = TableHandler.orderingTableHandler.selectRecords("order_id = ?",
                new String[] { orderID });
        int totalBookPrice = 0, totalBookQuantity = 0;
        try {
            while (orderingResult.next()) {
                ResultSet selectedBook = TableHandler.bookTableHandler
                        .selectRecordByKey(new String[] { orderingResult.getString("ISBN") });
                if (selectedBook.next()) {
                    int unitPrice = selectedBook.getInt("unit_price");
                    totalBookPrice += unitPrice * orderingResult.getInt("quantity");
                    totalBookQuantity += orderingResult.getInt("quantity");
                }
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while updating the order charge: " + e.getMessage());
            return false;
        }
        int shippingPrice = totalBookQuantity * UNIT_SHIPPING_CHARGE + HANDLING_CHARGE;
        int totalPrice = totalBookQuantity > 0 ? totalBookPrice + shippingPrice : 0;
        return TableHandler.orderTableHandler.updateRecordByKey(new String[] { "charge" },
                new String[] { String.valueOf(totalPrice) },
                new String[] { orderID });
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

        ArrayList<Ordering> orders = new ArrayList<>();
        while (!(bookOrderInput = InputHandler.getValidOrderInput(scanner)).equals(InputHandler.FINISH_ORDER)) {
            switch (bookOrderInput) {
                case InputHandler.LIST_ORDER:
                    // show ordered list
                    System.out.println("ISBN:            Number: ");
                    for (Ordering order : orders) {
                        System.out.println(order.ISBN + "      " + order.quantity);
                    }
                    break;
                default:
                    // order book
                    String ISBN = bookOrderInput;
                    int quantity = InputHandler.getValidOrderBookQuantity(scanner, ISBN);
                    if (quantity == -1) { // error occurred, skip this order
                        System.out.println("An error occurred while ordering the book. Did you enter a valid ISBN? Please try again.");
                        continue;
                    }
                    Boolean insertResult = TableHandler.orderingTableHandler.insertRecord(
                            String.format("%08d", thisOrderID[0]),
                            ISBN,
                            String.valueOf(quantity));

                    if (insertResult) {
                        ResultSet selectedBook = TableHandler.bookTableHandler.selectRecordByKey(new String[] { ISBN });
                        try {
                            if (selectedBook.next()) {
                                orders.add(new Ordering(ISBN, quantity));
                            }
                        } catch (SQLException e) {
                            System.out.println("An error occurred while getting book info: " + e.getMessage());
                        }
                    }else{
                        System.out.println("An error occurred while ordering the book. Did you enter the same ISBN again? Please try again.");
                    }
                    break;
            }
        }
        // clean up by calculating the charge for this order
        Boolean updateChargeResult = updateOrderCharge(String.format("%08d", thisOrderID[0]));
        if (!updateChargeResult) {
            System.out.println("An error occurred while updating the order charge.");
        }
    }

    private static class Order {
        public String order_id;
        public Date o_date;
        public String shipping_status;
        public int charge;
        public String customer_id;

        public Order(String orderID, Date o_date, String shippingStatus, int charge, String customerID) {
            this.order_id = orderID;
            this.o_date = o_date;
            this.shipping_status = shippingStatus;
            this.charge = charge;
            this.customer_id = customerID;
        }
    }

    private static Order processOrder(ResultSet orderResult) throws SQLException {
        orderResult.next();
        return new Order(orderResult.getString("order_id"), orderResult.getDate("o_date"),
                orderResult.getString("shipping_status"), orderResult.getInt("charge"),
                orderResult.getString("customer_id"));
    }

    private static void printOrder(Order order) {
        System.out.print("order_id:" + order.order_id);
        System.out.print("  shipping:" + order.shipping_status);
        System.out.print("  charge=" + order.charge);
        System.out.println("  customerId=" + order.customer_id);
    }

    private static List<Ordering> processOrderings(ResultSet orderingResult) throws SQLException {
        List<Ordering> orderings = new ArrayList<>();
        while (orderingResult.next()) {
            orderings.add(new Ordering(orderingResult.getString("ISBN"), orderingResult.getInt("quantity")));
        }
        return orderings;
    }

    private static void printOrdering(List<Ordering> orderings) {
        int count = 1;
        for (Ordering ordering : orderings) {
            System.out.print("book no: " + count++);
            System.out.print("  ISBN = " + ordering.ISBN);
            System.out.println("  quantity = " + ordering.quantity);
        }
    }

    private static Boolean updateOrderingQuantity(String orderID, String ISBN, int quantity) {
        return TableHandler.orderingTableHandler.updateRecordByKey(new String[] { "quantity" },
                new String[] { String.valueOf(quantity) },
                new String[] { orderID, ISBN });
    }

    private static void orderAltering() {
        String orderID = InputHandler.getValidOrderID(scanner, "Please enter the OrderID that you want to change: ");
        ResultSet orderResult = TableHandler.orderTableHandler.selectRecordByKey(new String[] { orderID });
        ResultSet orderingResult = TableHandler.orderingTableHandler.selectRecords("order_id = ?",
                new String[] { orderID });

        Order order;
        List<Ordering> orderings;
        try {
            order = processOrder(orderResult);
            orderings = processOrderings(orderingResult);
        } catch (SQLException e) {
            System.out.println("An error occurred while checking the order ID: " + e.getMessage());
            return;
        }
        printOrder(order);
        printOrdering(orderings);

        if(orderings.size() == 0){
            System.out.println("There is no book in the order to alter");
            return;
        }

        int bookNumber = InputHandler.getValidOrderAlteringBookNumber(scanner, orderings.size());
        Ordering orderingToAlter = orderings.get(bookNumber - 1);
        String alteringChoice = InputHandler.getValidOrderAlteringChoice(scanner);
        Boolean orderShipped = order.shipping_status.equals("Y");
        int updatedQuantity = -1;
        switch (alteringChoice) {
            case InputHandler.ADD_CHOICE:
                int remainingCopies;
                try {
                    ResultSet bookResult = TableHandler.bookTableHandler
                            .selectRecordByKey(new String[] { orderings.get(bookNumber - 1).ISBN });
                    bookResult.next();
                    remainingCopies = bookResult.getInt("no_of_copies");
                } catch (SQLException e) {
                    System.out.println(
                            "An error occurred while checking the remaining copies of book: " + e.getMessage());
                    return;
                }

                int quantityToAdd = InputHandler.getValidIntegerInRange("Input the number ", scanner, 1,
                        remainingCopies);
                System.out.println("Update is ok!");
                if (orderShipped) {
                    System.out.println("The books in the order are shipped");
                } else {
                    updatedQuantity = orderingToAlter.quantity + quantityToAdd;
                }
                break;
            default:
                int currentCopies = orderings.get(bookNumber - 1).quantity;
                int quantityToRemove = InputHandler.getValidIntegerInRange("Input the number ", scanner, 1,
                        currentCopies);
                System.out.println("Update is ok!");
                if (orderShipped) {
                    System.out.println("The books in the order are shipped");
                } else {
                    updatedQuantity = orderingToAlter.quantity - quantityToRemove;
                }
                break;
        }
        if (updatedQuantity != -1) {
            Boolean orderingUpdateResult = updateOrderingQuantity(orderID, orderingToAlter.ISBN,
            updatedQuantity);
            if (orderingUpdateResult) {
                System.out.println("Update is done!!");
                Boolean updateChargeResult = updateOrderCharge(orderID);
                if (updateChargeResult) {
                    System.out.println("Updated charge");
                }
            } else {
                System.out.println("An error occurred while updating the order quantity.");
            }
        }
    }

    private static void printOrders(ResultSet orderResult){
        int count = 1;
        try {
            while (orderResult.next()) {
                System.out.println();
                System.out.println("Record " + count++);
                System.out.println("OrderID: " + orderResult.getString("order_id"));
                System.out.println("Order Date: " + orderResult.getDate("o_date"));
                System.out.println("Charge: " + orderResult.getInt("charge"));
                System.out.println("Shipping Status: " + orderResult.getString("shipping_status"));
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while printing the orders: " + e.getMessage());
        }
    }

    private static void orderQuery() {
        String customerID = InputHandler.getValidCustomerID(scanner, "Please Input Customer ID: ");
        int yearToQuery = InputHandler.getValidYear(scanner, "Please Input Year: ");
        ResultSet orderResults = TableHandler.orderTableHandler.selectRecords("customer_id = ? AND EXTRACT(YEAR from o_date) = ?", new String[] { customerID, String.valueOf(yearToQuery) }, "order_id ASC");
        printOrders(orderResults);
    }
}
