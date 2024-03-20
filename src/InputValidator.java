import java.util.InputMismatchException;
import java.util.Scanner;

public class InputValidator {
    //Customer
    public static String getValidISBN(Scanner scanner) {
        String input = "";
        while (true) {
            System.out.println("Please enter the ISBN (e.g., 1-2345-6789-0): ");
            input = scanner.nextLine().replace("\n", "");
            try {
                if (!(input.matches("\\d{1}-\\d{4}-\\d{4}-\\d{1}"))) {
                    System.out.println("[Error] Input does not follow the format of ISBN. Please try again.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid Input");
            }
        }
        return input;
    }

    //Customer
    public static String getValidBookTitle(Scanner scanner) {
        String input = "";
        while (true) {
            System.out.println("Please enter the Book Title: ");
            input = scanner.nextLine().replace("\n", "");
            try {
                if (input.length() == 0 || input.length() > 100) {
                    System.out.println("[Error] Invalid Input: Title out of range! Please try again.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid Input");
            }
        }
        return input;
    }

    //Customer
    public static String getValidAuthorName(Scanner scanner) {
        String input = "";
        while (true) {
            System.out.println("Please enter the Author Name: ");
            input = scanner.nextLine().replace("\n", "");
            try {
                if (input.length() == 0 || input.length() > 50) {
                    System.out.println("[Error] Invalid Input: Author Name out of range. Please try again.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid Input");
            }
        }
        return input;
    }

    //Customer
    public static String getValidCustomerID(Scanner scanner) {
        String input = "";
        while (true) {
            System.out.println("Please enter the Customer ID: ");
            input = scanner.nextLine().replace("\n", "");
            try {
                if (input.length() == 0 || input.length() > 10) {
                    System.out.println("[Error] Invalid Input: ID out of range. Please try again.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid Input");
            }
        }
        return input;
    }

    //Bookstore
    public static String getValidYear(Scanner scanner) {
        String input = "";
        while (true) {
            System.out.println("Please enter the target year (YYYY): ");
            input = scanner.nextLine().replace("\n", "");
            try {
                if (input.length() != 4) {
                    System.out.println("[Error] Invalid Input: Formatting Error. Please try again.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid Input");
            }
        }
        return input;
    }

    //Bookstore
    public static String getValidMonth(Scanner scanner) {
        String input = "";
        while (true) {
            System.out.println("Please input the Month for Order Query (e.g., 2005-09): ");
            input = scanner.nextLine().replace("\n", "");
            try {
                if (!(input.matches("\\d{4}-\\d{2}"))) {
                    System.out.println("[Error] Input does not follow the format of month. Please try again.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid Input");
            }
        }
        return input;
    }

    public static int getValidQuantity(Scanner scanner) {
        int quantity = 0;
        while (true) {
            System.out.println("Please enter the Quantity you want: ");
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

    //Customer

    public static int getValidAddQuantity(Scanner scanner) {
        int input = 0;
        while (true) {
            System.out.println("How many numbers do you want to add? : ");
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

    //Customer
    public static int getValidDeleteQuantity(Scanner scanner) {
        int input = 0;
        while (true) {
            System.out.println("How many numbers do you want to delete? : ");
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

    //Customer
    public static String getValidExit(Scanner scanner) {
        String input = "";
        System.out.println("Please press (L/F): ");
        while (true) {
            input = scanner.nextLine().replace("\n", "");
            try {
                if (input.equals("L") || input.equals("F")) {
                    return input;
                } else {
                    System.out.println("[Error] You should only enter 'L' for Looking the ordered list / 'F' for Finishing ordering. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid Input");
            }
        }
    }

    //BookStore
    public static int getValidOrderID(Scanner scanner) {
        int oid;
        while (true) {
            System.out.println("Please input the order ID: ");
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

    //Customer
    public static String getValidAction(Scanner scanner) {
        String input = "";
        while (true) {
            System.out.println("What kind of action do you want to make? (A/D): ");
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

    //BookStore
    public static String getValidUserResponse(Scanner scanner) {
        String r;
        while (true) {
            System.out.println("Are you sure you want to update the shipping status? (Yes=Y): ");
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

    //Part of Customer Interface
    public static int getValidChoice(Scanner scanner, int number) {
        int input = -1;
        while (true) {
            System.out.println("Which book do you want to alter (input book no.): ");
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
