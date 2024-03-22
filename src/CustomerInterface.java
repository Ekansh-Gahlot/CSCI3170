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
                    ResultSet bookInfo = DatabaseManager.executeStatement("SELECT * FROM BOOK WHERE ISBN = ?",
                            new ArrayList<String>() {
                                {
                                    add(bookResult.getString("ISBN"));
                                }
                            });
                    if (bookInfo.next()) {
                        // print book infor
                        System.out.println("Record " + count);
                        System.out.println("ISBN: " + bookInfo.getString("ISBN"));
                        System.out.println("Book Title:" + bookInfo.getString("title"));
                        System.out.println("Unit Price:" + bookInfo.getString("unit_price"));
                        System.out.println("No of Available:" + bookInfo.getString("no_of_copies"));

                        // print authors
                        ResultSet authors = DatabaseManager.executeStatement(
                                "SELECT author_name FROM book_author WHERE ISBN = ? ORDER BY author_name ASC",
                                new ArrayList<String>() {
                                    {
                                        add(bookInfo.getString("ISBN"));
                                    }
                                });
                        System.out.println("Authors:");
                        int authorCount = 1;
                        while (authors.next()) {
                            System.out.println(authorCount++ + ". " + authors.getString("author_name"));
                        }
                        System.out.println();
                        count++;
                    }
                }
            } catch (SQLException e) {
                System.out.println("An error occurred while printing book results: " + e.getMessage());
            }
        }
    }

    private static final int BOOK_QUERY_EXIT_CHOICE = 4;

    private static void bookSearch() {
        String sortingOrder = " ORDER BY book.title ASC, book.ISBN ASC";

        Runnable SearchByISBN = () -> {
            ResultSet bookResults[] = new ResultSet[1];
            String ISBN = InputHandler.getValidISBN(scanner);
            String searchSQL = "SELECT ISBN FROM BOOK WHERE ISBN = ?";
            bookResults[0] = DatabaseManager.executeStatement(searchSQL, new ArrayList<String>() {
                {
                    add(ISBN);
                }
            });
            printBookResults(bookResults);
        };

        Runnable SearchByBookTitle = () -> {
            ResultSet bookResults[] = new ResultSet[2];
            String exactBookTitle = InputHandler.getValidBookTitle(scanner);
            String exactSearchSQL = "SELECT ISBN FROM book WHERE title = ?";
            bookResults[0] = DatabaseManager.executeStatement(exactSearchSQL, new ArrayList<String>() {
                {
                    add(exactBookTitle);
                }
            });
            String partialBookTitle = InputHandler.getValidPartialString(exactBookTitle);
            String partialSearchSQL = "SELECT ISBN FROM book WHERE title LIKE ? AND title <> ?" + sortingOrder;
            bookResults[1] = DatabaseManager.executeStatement(partialSearchSQL, new ArrayList<String>() {
                {
                    add(partialBookTitle);
                    add(exactBookTitle);
                }
            });
            printBookResults(bookResults);
        };

        Runnable SearchByAuthorName = () -> {
            ResultSet bookResults[] = new ResultSet[2];
            String exactAuthorName = InputHandler.getValidAuthorName(scanner);
            String exactSearchSQL = "SELECT ISBN FROM book WHERE ISBN IN (SELECT book2.ISBN FROM book_author, book book2 WHERE book2.ISBN = book_author.ISBN AND book_author.author_name = ?)"
                    + sortingOrder; // Yes, I know this is a bit of a mess, but the sub-query is required to get
                                    // distinct ISBNs
            bookResults[0] = DatabaseManager.executeStatement(exactSearchSQL, new ArrayList<String>() {
                {
                    add(exactAuthorName);
                }
            });
            String partialAuthorName = InputHandler.getValidPartialString(exactAuthorName);
            String partialSearchSQL = "SELECT ISBN FROM book WHERE ISBN IN (SELECT book2.ISBN FROM book_author, book book2 WHERE book2.ISBN = book_author.ISBN AND book_author.author_name LIKE ? AND book_author.author_name <> ?)"
                    + sortingOrder; // same mess as above
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
        InputValidator customerIDValidator = new InputValidator("Please enter your customerID??",
                "");
        InputValidator.StringValidation customerIDValidation = (String input) -> {
            try {
                String customerQuery = "SELECT * FROM customer WHERE customer_id = ?";
                ResultSet customerResult = DatabaseManager.executeStatement(customerQuery, new ArrayList<String>() {
                    {
                        add(input);
                    }
                });
                if (!customerResult.next()) {
                    return "No customer found with the given ID. Please try again.";
                }
            } catch (SQLException e) {
                return "An error occurred while checking customer: " + e.getMessage();
            }
            return null;
        };
        String customerID = customerIDValidator.getValidInput(scanner,
                customerIDValidation);

        // creates a new order
        final int[] thisOrderID = { 0 };
        try {
            String latestOrderQuery = "SELECT MAX(order_id) FROM orders";
            ResultSet latestOrderResult = DatabaseManager.executeStatement(latestOrderQuery, new ArrayList<String>());
            if (latestOrderResult.next()) {
                thisOrderID[0] = Integer.parseInt(latestOrderResult.getString(1)) + 1;
            }
            String thisOrderInsert = "INSERT INTO orders VALUES (?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ?)";
            ResultSet orderResult = DatabaseManager.executeStatement(thisOrderInsert, new ArrayList<String>() {
                {
                    add(String.format("%08d", thisOrderID[0]));
                    add(MainApplication.getSystemDateString());
                    add("N");
                    add("0");
                    add(customerID);
                }
            });
            
            if (orderResult == null){
                System.out.println("An error occurred while creating the order. Please try again.");
                return;
            }
        } catch (Exception e) {
            System.out.println("An error occurred while getting the latest order ID: " + e.getMessage());
            return;
        }

        // continue with ordering books
        System.out.println(">> What books do you want to order??");
        System.out.println(">> Input ISBN and then the quantity");
        System.out.println(">> You can press \"L\" to see ordered list, or \"F\" to finish ordering");

        String bookOrderInput;
        InputValidator bookOrderInputValidator = new InputValidator("Please enter the book's ISBN: ",
                "Invalid input: received non-ISBN input, \"L\" nor \"F\".");
        InputValidator.BinaryValidation bookOrderInputValidation = (
                String input) -> input.matches("\\d{1}-\\d{4}-\\d{4}-\\d{1}")
                        || input.equals("L") || input.equals("F");
        InputValidator bookOrderQuantityValidator = new InputValidator("Please enter the quantity of the order: ",
                "Invalid input: Invalid quantity. Please try again.");

        ArrayList<Order> orders = new ArrayList<>();
        final String finishOrderInput = "F";
        int totalBookPrice = 0, totalBookQuantity = 0;
        while (!(bookOrderInput = bookOrderInputValidator.getValidInput(scanner,
                bookOrderInputValidation)).equals(finishOrderInput)) {
            switch (bookOrderInput) {
                case "L":
                    // show ordered list
                    System.out.println("ISBN:            Number: ");
                    for (Order order : orders) {
                        System.out.println(order.ISBN + "      " + order.quantity);
                    }
                    break;
                default:
                    // order book
                    String ISBN = bookOrderInput;
                    try {
                        // check existing book
                        String bookQuery = "SELECT * FROM book WHERE ISBN = ?";
                        ResultSet bookResult = DatabaseManager.executeStatement(bookQuery, new ArrayList<String>() {
                            {
                                add(ISBN);
                            }
                        });
                        if (!bookResult.next()) {
                            System.out.println("No book found with the given ISBN. Please try again.");
                            break;
                        }
                        int numberOfCopies = bookResult.getInt("no_of_copies");

                        // get book order quantity
                        InputValidator.BinaryValidation bookOrderQuantityValidation = (String input) -> {
                            try {
                                int quantity = Integer.parseInt(input);
                                return quantity > 0 && quantity <= numberOfCopies;
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        };
                        int quantity = Integer.parseInt(bookOrderQuantityValidator.getValidInput(scanner,
                                bookOrderQuantityValidation));

                        // create ordering
                        String orderingInsert = "INSERT INTO ordering VALUES (?, ?, ?)";
                        ResultSet insertResult = DatabaseManager.executeStatement(orderingInsert, new ArrayList<String>() {
                            {
                                add(String.format("%08d", thisOrderID[0]));
                                add(ISBN);
                                add(String.valueOf(quantity));
                            }
                        });
                        if(insertResult != null){
                            orders.add(new Order(ISBN, quantity));
    
                            // add up the charge
                            int unitPrice = bookResult.getInt("unit_price");
                            totalBookPrice += unitPrice * quantity;
                            totalBookQuantity += quantity;
                        }
                    } catch (Exception e) {
                        System.out.println("An error occurred creating order: " + e.getMessage());
                    }
                    break;
            }
        }
        // clean up by calculating the charge for this order
        if (totalBookQuantity > 0){
            int shippingPrice = totalBookQuantity * UNIT_SHIPPING_CHARGE + HANDLING_CHARGE;
            int totalPrice = totalBookPrice + shippingPrice;
            String updateOrder = "UPDATE orders SET charge = ? WHERE order_id = ?";
            DatabaseManager.executeStatement(updateOrder, new ArrayList<String>() {
                {
                    add(String.valueOf(totalPrice));
                    add(String.format("%08d", thisOrderID[0]));
                }
            });
        }
    }

    private static void orderAltering() {

    }

    private static void orderQuery() {

    }
}
