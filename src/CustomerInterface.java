import java.sql.SQLException;
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
                    .addAction(EXIT_CHOICE, "Back to main menu", () -> {});

            while (selector.run(scanner) != EXIT_CHOICE) {}
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private static void orderQuery() {

    }

    private static void orderAltering() {

    }

    private static void orderCreation() {
    }

    private static void bookSearch() {
        //  actual logic for book search in the customer interface
        // Implement database-related operations here
    }
}
