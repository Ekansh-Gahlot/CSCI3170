import java.sql.ResultSet;
import java.sql.SQLException;
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
        if (input.matches(".+[%_].+")) {
            return "''"; // invalidate strings that has % in the middle
        }
        return input;
    }

    /**
     * A general method to get a valid integer within the given range from the user.
     * 
     * @param prompt  the prompt message to display to the user
     * @param scanner the scanner object to read input from the user
     * @param min     the minimum value of the input
     * @param max     the maximum value of the input
     * @return a valid integer within the given range
     */
    public static int getValidIntegerInRange(String prompt, Scanner scanner, int min, int max) {
        IntegerInputValidator validator = new IntegerInputValidator(prompt);
        return validator.getValidInput(scanner, (Integer input) -> {
            if (input >= min && input <= max) {
                return null;
            }
            return "Invalid input: Please enter a number between " + min + " and " + max + ".";
        });
    }

    public static StringInputValidator.BinaryValidation checkISBN = (String input) -> input
            .matches("\\d{1}-\\d{4}-\\d{4}-\\d{1}");

    public static String getValidISBN(Scanner scanner) {
        StringInputValidator validator = new StringInputValidator("Input the ISBN: ");
        return validator.getValidInput(scanner, checkISBN);
    }

    final static String FINISH_ORDER = "F";
    final static String LIST_ORDER = "L";
    public static StringInputValidator.BinaryValidation checkLF = (String input) -> input.equals(LIST_ORDER)
            || input.equals(FINISH_ORDER);

    public static String getValidOrderInput(Scanner scanner) {
        StringInputValidator bookOrderInputValidator = new StringInputValidator("Please enter the book's ISBN: ");
        StringInputValidator.StringValidation bookOrderInputValidation = (
                String input) -> {
            if (checkISBN.validate(input) || checkLF.validate(input))
                return null;
            return "Invalid input: received non-ISBN input, \"" + FINISH_ORDER + "\" nor \"" + LIST_ORDER + "\".";
        };
        return bookOrderInputValidator.getValidInput(scanner,
                bookOrderInputValidation);
    }

    /**
     * Returns a valid order quantity by checking if the input is a valid integer
     * that is within the range of the number of copies of the book.
     * Returns -1 if error occurred
     * 
     * @param scanner
     * @param ISBN
     * @return
     */
    public static int getValidOrderBookQuantity(Scanner scanner, String ISBN) {
        // check existing book
        ResultSet bookResult = TableHandler.bookTableHandler.selectRecordByKey(new String[] { ISBN });
        final int[] numberOfCopies = { -1 };
        try {
            if (!bookResult.next()) {
                System.out.println("No book found with the given ISBN. Please try again.");
                return -1;
            }
            numberOfCopies[0] = bookResult.getInt("no_of_copies");
        } catch (SQLException e) {
            System.out.println("An error occurred while checking remaining book quantity: " + e.getMessage());
            return -1;
        }

        // get book order quantity
        return getValidIntegerInRange("Please enter the quantity of the order: ", scanner, 1, numberOfCopies[0]);
    }

    // Customer
    public static String getValidBookTitle(Scanner scanner) {
        StringInputValidator validator = new StringInputValidator("Please enter the Book Title: ");
        return validator.getValidInput(scanner, (String input) -> input.length() > 0 && input.length() <= 100);
    }

    // Customer
    public static String getValidAuthorName(Scanner scanner) {
        StringInputValidator validator = new StringInputValidator("Please enter the Author Name: ");
        return validator.getValidInput(scanner, (String input) -> input.length() > 0 && input.length() <= 50);
    }

    // Customer
    public static String getValidCustomerID(Scanner scanner, String prompt) {
        StringInputValidator customerIDValidator = new StringInputValidator(prompt);
        StringInputValidator.StringValidation customerIDValidation = (String input) -> {
            if (input.length() == 0 || input.length() > 10) {
                return "Customer ID must be between 1 and 10 characters long.";
            }
            try {
                ResultSet customerResult = TableHandler.customerTableHandler.selectRecordByKey(new String[] { input });
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
        return customerID;
    }

    public static String getValidCustomerID(Scanner scanner) {
        return getValidCustomerID(scanner, "Please enter your customerID??");
    }

    public static int MAX_YEAR = 9999;

    public static int getValidYear(Scanner scanner, String prompt) {
        IntegerInputValidator yearValidator = new IntegerInputValidator(prompt);
        return yearValidator.getValidInput(scanner, (Integer input) -> {
            if (input >= 0 && input <= 9999) {
                return null;
            }
            return "Invalid input: Please enter a number between 0 and " + MAX_YEAR + ".";
        });
    }

    // Bookstore
    public static int getValidYear(Scanner scanner) {
        return getValidYear(scanner, "Please enter the target year (YYYY): ");
    }

    // Bookstore
    public static String getValidYearMonth(Scanner scanner) {
        StringInputValidator validator = new StringInputValidator(
                "Please input the Month for Order Query (e.g., 2005-09): ");
        return validator.getValidInput(scanner, (String input) -> input.matches("\\d{4}-\\d{2}"));
    }

    public static int getValidNBookNum(Scanner scanner) {
        return getValidIntegerInRange("Please enter the N popular books number: ", scanner, 1, Integer.MAX_VALUE);
        // int quantity = 0;
        // while (true) {
        //     System.out.print("Please enter the N popular books number: ");
        //     quantity = scanner.nextInt();
        //     try {
        //         if (quantity < 1) {
        //             System.out.println("[Error] Invalid Input: cannot be < 1");
        //         } else {
        //             break;
        //         }
        //     } catch (NumberFormatException e) {
        //         System.out.println("[Error] Invalid Input");
        //     }
        // }
        // return quantity;
    }

    public static String getValidOrderID(Scanner scanner, String prompt) {
        StringInputValidator validator = new StringInputValidator(prompt);
        return validator.getValidInput(scanner, (String input) -> {
            try {
                ResultSet orderResult = TableHandler.orderTableHandler.selectRecordByKey(new String[] { input });
                if (!orderResult.next()) {
                    return "No order found with the given ID. Please try again.";
                }
            } catch (SQLException e) {
                return "An error occurred while checking order: " + e.getMessage();
            }
            return null;
        });
    }

    public static String getValidOrderID(Scanner scanner) {
        return getValidOrderID(scanner, "Please enter the Order ID: ");
    }

    public static int getValidOrderAlteringBookNumber(Scanner scanner, int max_number) {
        return getValidIntegerInRange("Which book you want to alter (input book no.):", scanner, 1, max_number);
    }

    public final static String ADD_CHOICE = "add";
    public final static String REMOVE_CHOICE = "remove";

    public static String getValidOrderAlteringChoice(Scanner scanner) {
        StringInputValidator validator = new StringInputValidator("input add or remove\n");
        return validator.getValidInput(scanner, (String input) -> {
            if (input.equals(ADD_CHOICE) || input.equals(REMOVE_CHOICE)) {
                return null;
            }
            return "Invalid input: Please enter either " + ADD_CHOICE + "/" + REMOVE_CHOICE + ".";
        });
    }

    // BookStore
    public static String getValidUpdateConfirmation(Scanner scanner) {
        StringInputValidator validator = new StringInputValidator(
                "Are you sure you want to update the shipping status? (Yes=Y)  ");
        return validator.getValidInput(scanner, (String input) -> {
            // if (input.equals("Y") || input.equals("N")) {
            //     return null;
            // }
            // return "Invalid input: Please enter either Y or N.";
            return null; // accept any input
        });
    }
}
