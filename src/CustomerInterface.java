import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class CustomerInterface {
    private static Scanner scanner;
    private static final int EXIT_CHOICE = 5;

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
                    ResultSet bookInfo = DatabaseManager.executeStatement("SELECT * FROM BOOK WHERE ISBN = ?", new ArrayList<String>() {
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
                        ResultSet authors = DatabaseManager.executeStatement("SELECT author_name FROM book_author WHERE ISBN = ? ORDER BY author_name ASC", new ArrayList<String>() {
                            {
                                add(bookInfo.getString("ISBN"));
                            }
                        });
                        System.out.println("Authors:");
                        int authorCount = 1;
                        while(authors.next()){
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
            String ISBN = InputValidator.getValidISBN(scanner);
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
            String exactBookTitle = InputValidator.getValidBookTitle(scanner);
            String exactSearchSQL = "SELECT ISBN FROM book WHERE title = ?";
            bookResults[0] = DatabaseManager.executeStatement(exactSearchSQL, new ArrayList<String>() {
                {
                    add(exactBookTitle);
                }
            });
            String partialBookTitle = InputValidator.getValidPartialString(exactBookTitle);
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
            String exactAuthorName = InputValidator.getValidAuthorName(scanner);
            String exactSearchSQL = "SELECT ISBN FROM book WHERE ISBN IN (SELECT book2.ISBN FROM book_author, book book2 WHERE book2.ISBN = book_author.ISBN AND book_author.author_name = ?)" + sortingOrder; // Yes, I know this is a bit of a mess, but the sub-query is required to get distinct ISBNs
            bookResults[0] = DatabaseManager.executeStatement(exactSearchSQL, new ArrayList<String>() {
                {
                    add(exactAuthorName);
                }
            });
            String partialAuthorName = InputValidator.getValidPartialString(exactAuthorName);
            String partialSearchSQL = "SELECT ISBN FROM book WHERE ISBN IN (SELECT book2.ISBN FROM book_author, book book2 WHERE book2.ISBN = book_author.ISBN AND book_author.author_name LIKE ? AND book_author.author_name <> ?)" + sortingOrder; // same mess as above
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

    private static void orderCreation() {
    }

    private static void orderAltering() {

    }

    private static void orderQuery() {

    }
}
