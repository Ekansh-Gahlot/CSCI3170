import java.sql.SQLException;

public class CustomerInterface {

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
                    .addAction(5, "Back to main menu", () -> {});

            while (selector.run() != 5) {}
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
