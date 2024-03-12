import java.util.Scanner;

public class MainApplication {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int interfaceChoice;

        do {
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

        } while (interfaceChoice != 0);

        // Close resources
        scanner.close();
    }
}
