import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MainApplication {
    private static String systemDate;

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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int interfaceChoice;

        do {
            try {
                createSystemDate();
                String sdate = getSystemDate();

                System.out.println("The System Date is now : " + sdate);
                System.out.println("<This is the Book Ordering System>");
                System.out.println("----------------------------------------");
                System.out.println("Select Interface:");
                System.out.println("1. System Interface");
                System.out.println("2. Customer Interface");
                System.out.println("3. Bookstore Interface");
                System.out.println("0. Exit");
                System.out.print("Enter your choice: ");
                interfaceChoice = scanner.nextInt();

                switch (interfaceChoice) {
                    case 1:
                        SystemInterface.handleSystemInterface();
                        break;
                    case 2:
                        CustomerInterface.handleCustomerInterface();
                        break;
                    case 3:
                        BookstoreInterface.handleBookstoreInterface();
                        break;
                    case 0:
                        System.out.println("Exiting the application.");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.next(); 
                interfaceChoice = -1; 
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                interfaceChoice = -1; 
            }

        } while (interfaceChoice != 0);

        scanner.close();
    }
}
