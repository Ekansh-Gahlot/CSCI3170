import java.sql.SQLException;

public class BookstoreInterface {

    public static void handle() {
        ChoiceSelector selector = new ChoiceSelector()
                .addAction(1, "Order Update", BookstoreInterface::orderUpdate)
                .addAction(2, "Order Query", BookstoreInterface::orderQuery)
                .addAction(3, "N most popular book query", BookstoreInterface::nMostPopularBookQuery)
                .addAction(4, "Back to main menu", () -> {});

        try {
            // Bookstore interface logic here
            // For example, orderUpdate()
            orderUpdate();

            while (selector.run() != 4) {

            }
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }


    private static void orderUpdate() {
        // Logic to update the shipping status of an order
        // Implement database-related operations here
    }

    private static void orderQuery() {
    }

    private static void nMostPopularBookQuery() {
    }
}
