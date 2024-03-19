import java.util.InputMismatchException;
import java.util.Scanner;

public class InputValidator {
    Scanner scanner;

    public InputValidator() {
        //
    }

    //Customer
    public String validateISBN() {
        String input = "";
        while (true) {
            scanner = new Scanner(System.in);
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
    public String validateBookTitle() {
        String input = "";
        while (true) {
            System.out.println("Please enter the Book Title: ");
            scanner = new Scanner(System.in);
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
    public String validateAuthorName() {
        String input = "";
        while (true) {
            System.out.println("Please enter the Author Name: ");
            scanner = new Scanner(System.in);
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
    public String validateCustomerID() {
        String input = "";
        while (true) {
            System.out.println("Please enter the Customer ID: ");
            scanner = new Scanner(System.in);
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
    public String validateYear() {
        String input = "";
        while (true) {
            System.out.println("Please enter the target year (YYYY): ");
            scanner = new Scanner(System.in);
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
    public String validateMonth() {
        String input = "";
        while (true) {
            System.out.println("Please input the Month for Order Query (e.g., 2005-09): ");
            scanner = new Scanner(System.in);
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

    public int validateQuantity() {
        int quantity = 0;
        while (true) {
            System.out.println("Please enter the Quantity you want: ");
            scanner = new Scanner(System.in);
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

    public int validateQuantityAdd() {
        int input = 0;
        while (true) {
            System.out.println("How many numbers do you want to add? : ");
            scanner = new Scanner(System.in);
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
    public int validateQuantityDelete() {
        int input = 0;
        while (true) {
            System.out.println("How many numbers do you want to delete? : ");
            scanner = new Scanner(System.in);
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
    public String validateExit() {
        String input = "";
        Scanner reader = new Scanner(System.in);
        System.out.println("Please press (L/F): ");
        while (true) {
            input = reader.nextLine().replace("\n", "");
            try {
                if (input.equals("L") || input.equals("F")) {
                    reader.close();
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
    static public int validateOrderID() {
        int oid;
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("Please input the order ID: ");
            try {
                if (in.hasNext()) {
                    oid = in.nextInt();
                    if (oid <= 0)
                        System.out.println("[Error] Invalid Order ID. Please try again.");
                    else
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("[Error] Invalid Input");
            }
        }
        in.close();
        return oid;
    }

    //Customer
    public String validateAction() {
        String input = "";
        while (true) {
            System.out.println("What kind of action do you want to make? (A/D): ");
            scanner = new Scanner(System.in);
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
    static public String validateUserResponse() {
        String r;
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("Are you sure you want to update the shipping status? (Yes=Y): ");
            try {
                if (in.hasNext()) {
                    r = in.nextLine().replace("\n", "");
                    if (r.equals("Y") || r.equals("N"))
                        break;
                    else
                        System.out.println("[Error] Only Y or N can be entered");
                }
            } catch (InputMismatchException e) {
                System.out.println("[Error] Invalid Input");
            }
        }
        in.close();
        return r;
    }

    //Part of Customer Interface
    public int validateChoice(int number) {
        int input = -1;
        while (true) {
            System.out.println("Which book do you want to alter (input book no.): ");
            scanner = new Scanner(System.in);
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
