import java.text.SimpleDateFormat;
import java.sql.*;
import java.util.Date;
import java.util.InputMismatchException;

public class MainApplication {

    private static String systemDate;
    private static final int EXIT_CHOICE = 5;
    private static final String[] MAIN_MENU_CHOICES = {
            "System Interface",
            "Customer Interface",
            "Bookstore Interface",
            "Show System Date",
            "Quit the Application......",
    };

    private static void createSystemDate() {
        if (systemDate == null) {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            Date d = new Date();
            systemDate = f.format(d);
        } else {
            System.out.println("Cannot create more instance.");
        }
    }

    public static String getSystemDate() {
        return systemDate;
    }

    public static void showSystemDate() {
        System.out.println("The System Date is now : " + getSystemDate());
    }
    public static void main(String[] args) {
        Connection dbConnection = null;
        try{
            dbConnection = DatabaseManager.getConnection();
        }catch(SQLException e){
            System.err.println("Error connecting to the database: " + e.getMessage());
            System.out.println("Exiting the application......");
            return;
        }

        System.out.println("<This is the Book Ordering System>");
        System.out.println("----------------------------------------");
        int interfaceChoice;
        ChoiceSelector mainMenuSelector = new ChoiceSelector(MAIN_MENU_CHOICES);

        createSystemDate();
        do {
            // showSystemDate(); // Not sure here whether necessary or not
            interfaceChoice = mainMenuSelector.getChoice();
            try {
                switch (interfaceChoice) {
                    case 1:
                        SystemInterface.handleSystemInterface(dbConnection);
                        System.out.println("Returning to the main menu.");
                        break;
                    case 2:
                        CustomerInterface.handleCustomerInterface();
                        break;
                    case 3:
                        BookstoreInterface.handleBookstoreInterface();
                        break;
                    case 4:
                        showSystemDate();
                        break;
                    case EXIT_CHOICE:
                        System.out.println("Exiting the application......");
                        break;
                    default:
                        throw new InputMismatchException(
                                "Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println(e.getMessage());
                interfaceChoice = -1;
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                interfaceChoice = -1;
            }
        } while (interfaceChoice != EXIT_CHOICE);
    }
}
