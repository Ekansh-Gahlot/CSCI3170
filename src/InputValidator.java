import java.util.Scanner;

public class InputValidator{
    private String promptString;
    private String invalidString;

    public InputValidator(String promptString_, String invalidString_) {
        promptString = promptString_;
        invalidString = invalidString_;
    }

    public static interface Validation {
        public Boolean validate(String input); // decide whether the input is valid
    }

    public String getValidInput(Scanner scanner, Validation validator){
        System.out.print(promptString);
        String input = scanner.nextLine().replace("\n", "");
        while (!validator.validate(input)){
            System.out.println(invalidString);
            input = scanner.nextLine().replace("\n", "");
        }
        return input;
    }
}