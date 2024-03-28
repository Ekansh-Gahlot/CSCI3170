import java.util.Scanner;

public class IntegerInputValidator {
    private String notIntegerErrorMessage = "Invalid input. Please enter an integer.";
    private String promptString;

    public IntegerInputValidator(String promptString_) {
        promptString = promptString_;
    }

    @FunctionalInterface
    public static interface IntegerValidation {
        public String validate(Integer input); // null for valid input, otherwise error message should be returned
    }

    public IntegerInputValidator(String promptString_, String notIntegerErrorMessage_) {
        promptString = promptString_;
        notIntegerErrorMessage = notIntegerErrorMessage_;
    }

    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public int getValidInput(Scanner scanner, IntegerValidation validator) {
        System.out.print(promptString);
        String input;
        while (true) {
            input = scanner.nextLine().replace("\n", "");
            if (isInteger(input)) {
                int inputInt = Integer.parseInt(input);
                String validationResult = validator.validate(inputInt);
                if (validationResult == null) {
                    return inputInt;
                } else {
                    System.out.println(validationResult);
                }
            } else {
                System.out.println(notIntegerErrorMessage);
            }
        }
    }
}
