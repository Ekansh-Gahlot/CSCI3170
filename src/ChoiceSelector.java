import java.util.Scanner;

public class ChoiceSelector {
    private String[] choices;
    private static String promptChoiceString = "Please enter your choice: ";
    private static String invalidChoiceString = "Invalid choice. Please try again.";

    public ChoiceSelector(String[] choices_) {
        choices = choices_;
    }

    public int getChoice() {
        int choice = -1;
        Boolean validChoice = false;
        Scanner scanner = new Scanner(System.in);
        while (!validChoice) {
            for (int i = 0; i < choices.length; i++) {
                System.out.println((i + 1) + ". " + choices[i] + ".");
            }
            System.out.print(promptChoiceString);
            try {
                while (!scanner.hasNext()) // TODO: DK why getting stuck here on every consecutive success inputs
                    ;
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    if (choice < 1 || choice > choices.length) {
                        throw new Exception("Choice out of range");
                    }
                    validChoice = true;
                    break;
                } else {
                    scanner.next();
                }

            } catch (Exception e) {
                System.out.println(invalidChoiceString);
            }
        }
        scanner.close();
        return choice;
    }
}
