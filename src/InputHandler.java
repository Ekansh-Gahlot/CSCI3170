import java.util.InputMismatchException;
import java.util.Scanner;

public class InputHandler {

    /**
     * Returns a valid partial string by checking if the input string contains a '%'
     * character in the middle.
     * If the input string contains a '%' character in the middle, a string
     * containing an empty string is returned.
     * Otherwise, the input string is returned as is.
     *
     * @param input the input string to validate
     * @return a valid partial string or a string containing an empty string
     */
    public static String getValidPartialString(String input) {
        if (input.matches(".+%.+")) {
            return "''"; // invalidate strings that has % in the middle
        }
        return input;
    }

    // Customer
    public static String getValidISBN(Scanner scanner) {
        InputValidator validator = new InputValidator("Input the ISBN: ",
                "[Error] Input does not follow the format of ISBN. Please try again.");
        return validator.getValidInput(scanner, (String input) -> input.matches("\\d{1}-\\d{4}-\\d{4}-\\d{1}"));
    }

    // Customer
    public static String getValidBookTitle(Scanner scanner) {
        InputValidator validator = new InputValidator("Please enter the Book Title: ",
                "[Error] Invalid Input: Title out of range! Please try again.");
        return validator.getValidInput(scanner, (String input) -> input.length() > 0 && input.length() <= 100);
    }

    // Customer
    public static String getValidAuthorName(Scanner scanner) {
        InputValidator validator = new InputValidator("Please enter the Author Name: ",
                "[Error] Invalid Input: Author Name out of range. Please try again.");
        return validator.getValidInput(scanner, (String input) -> input.length() > 0 && input.length() <= 50);
    }

    // Customer
    public static String getValidCustomerID(Scanner scanner) {
        InputValidator validator = new InputValidator("Please enter the Customer ID: ",
                "[Error] Invalid Input: ID out of range. Please try again.");
        return validator.getValidInput(scanner, (String input) -> input.length() > 0 && input.length() <= 10);
    }

    // Bookstore
    public static String getValidYear(Scanner scanner) {
        InputValidator validator = new InputValidator("Please enter the target year (YYYY): ",
                "[Error] Invalid Input: Formatting Error. Please try again.");
        return validator.getValidInput(scanner, (String input) -> input.length() == 4);
    }

    // Bookstore
    public static String getValidYearMonth(Scanner scanner) {
        InputValidator validator = new InputValidator("Please input the Month for Order Query (e.g., 2005-09): ", "[Error] Input does not follow the format of month. Please try again.");
        return validator.getValidInput(scanner, (String input) -> input.matches("\\d{4}-\\d{2}"));
    }

    public static int getValidQuantity(Scanner scanner) {
        int quantity = 0;
        while (true) {
            System.out.print("Please enter the Quantity you want: ");
            quantity = scanner.nextInt();
            try {
                if (quantity < 1) {
                    System.out.println("[Error] Invalid Input: Zero Quantity is entered. Please try again.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid Input");
            }
        }
        return quantity;
    }

    // Customer
    public static int getValidAddQuantity(Scanner scanner) {
        int input = 0;
        while (true) {
            System.out.print("How many numbers do you want to add? : ");
            input = scanner.nextInt();
            try {
                if (input < 1) {
                    System.out.println("[Error] Invalid Input: Zero Quantity is entered. Please try again.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid Input");
            }
        }
        return input;
    }

    // Customer
    public static int getValidDeleteQuantity(Scanner scanner) {
        int input = 0;
        while (true) {
            System.out.print("How many numbers do you want to delete? : ");
            input = scanner.nextInt();
            try {
                if (input < 1) {
                    System.out.println("[Error] Invalid Input: Zero Quantity is entered. Please try again.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid Input");
            }
        }
        return input;
    }

    // Customer
    public static String getValidExit(Scanner scanner) {
        String input = "";
        System.out.print("Please press (L/F): ");
        while (true) {
            input = scanner.nextLine().replace("\n", "");
            try {
                if (input.equals("L") || input.equals("F")) {
                    return input;
                } else {
                    System.out.println(
                            "[Error] You should only enter 'L' for Looking the ordered list / 'F' for Finishing ordering. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid Input");
            }
        }
    }

    // BookStore
    public static int getValidOrderID(Scanner scanner) {
        int oid;
        while (true) {
            System.out.print("Please input the order ID: ");
            try {
                if (scanner.hasNext()) {
                    oid = scanner.nextInt();
                    if (oid <= 0)
                        System.out.println("[Error] Invalid Order ID. Please try again.");
                    else
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("[Error] Invalid Input");
            }
        }
        return oid;
    }

    // Customer
    public static String getValidAction(Scanner scanner) {
        String input = "";
        while (true) {
            System.out.print("What kind of action do you want to make? (A/D): ");
            input = scanner.nextLine().replace("\n", "");
            try {
                if (!input.equals("A") & !input.equals("D")) {
                    System.out.println("[Error] Only A or D can be entered (A = Add / D = Delete)");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid Input");
            }
        }
        return input;
    }

    // BookStore
    public static String getValidUserResponse(Scanner scanner) {
        String r;
        while (true) {
            System.out.print("Are you sure you want to update the shipping status? (Yes=Y): ");
            try {
                if (scanner.hasNext()) {
                    r = scanner.nextLine().replace("\n", "");
                    if (r.equals("Y") || r.equals("N"))
                        break;
                    else
                        System.out.println("[Error] Only Y or N can be entered");
                }
            } catch (InputMismatchException e) {
                System.out.println("[Error] Invalid Input");
            }
        }
        return r;
    }

    // Part of Customer Interface
    public static int getValidChoice(Scanner scanner, int number) {
        int input = -1;
        while (true) {
            System.out.print("Which book do you want to alter (input book no.): ");
            input = scanner.nextInt();
            try {
                if (input < 1 || input > number) {
                    System.out.println("[Error] Out of Range. Please select a number range from 1 to " + number);
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid Input");
            }
        }
        return input;
    }
}
