import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MainApplication {
    private static LocalDate systemDate;
    private static Scanner scanner = new Scanner(System.in);
    private static final int EXIT_CHOICE = 5;

    public static LocalDate getSystemDate() {
        return systemDate;
    }

    public static void setSystemDate(LocalDate newDate) {
        if (!newDate.isAfter(systemDate))
            throw new IllegalArgumentException("New date " + DATE_FORMAT.format(newDate) +
                    " is before old date " + DATE_FORMAT.format(systemDate));
        systemDate = newDate;
    }

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String getSystemDateString() {
        return DATE_FORMAT.format(systemDate);
    }

    public static void showSystemDate() {
        System.out.println("The System Date is now : " + getSystemDateString());
    }

    public static void main(String[] args) {
        // ensure that the connection is closed
        try (Connection conn = DatabaseManager.createConnection()) {
            System.out.println("<This is the Book Ordering System>");
            System.out.println("----------------------------------------");

            int interfaceChoice;
            ChoiceSelector mainMenuSelector = new ChoiceSelector()
                    .addAction(1, "System Interface", SystemInterface.handle(scanner))
                    .addAction(2, "Customer Interface", CustomerInterface.handle(scanner))
                    .addAction(3, "Bookstore Interface", BookstoreInterface.handle(scanner))
                    .addAction(4, "Show System Date", MainApplication::showSystemDate)
                    .addAction(5, "Quit the Application......", () -> {});

            systemDate = LocalDate.now();
            while (true) {
                // showSystemDate(); // Not sure here whether necessary or not
                try {
                    interfaceChoice = mainMenuSelector.run(scanner);
                    if (interfaceChoice == EXIT_CHOICE)
                        break;
                } catch (InputMismatchException e) {
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    System.out.println("An error occurred: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
            System.out.println("Exiting the application......");
        }
        scanner.close();
    }
}
