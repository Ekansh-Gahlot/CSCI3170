import java.util.Scanner;

public class StringInputValidator{
    private String promptString;

    public StringInputValidator(String promptString_) {
        promptString = promptString_;
    }

    // remained for backwards compatibility
    @FunctionalInterface
    public static interface BinaryValidation {
        public boolean validate(String input); // decide whether the input is valid
    }

    @FunctionalInterface
    public static interface StringValidation {
        public String validate(String input); // null for valid input, otherwise error message should be returned
    }

    // remained for backwards compatibility
    public String getValidInput(Scanner scanner, BinaryValidation validator){
        System.out.print(promptString);
        String input = scanner.nextLine().replace("\n", "");
        while (!validator.validate(input)){
            System.out.println("Invalid input. Please try again.");
            input = scanner.nextLine().replace("\n", "");
        }
        return input;
    }

    public String getValidInput(Scanner scanner, StringValidation validator){
        System.out.print(promptString);
        String input = scanner.nextLine().replace("\n", "");
        String validationResult;
        while ((validationResult = validator.validate(input))!=null){
            System.out.println(validationResult);
            input = scanner.nextLine().replace("\n", "");
        }
        return input;
    }
}