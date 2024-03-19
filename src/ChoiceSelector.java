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
        Scanner scanner = new Scanner(System.in);
        while (true) {
            for (int i = 0; i < choices.length; i++) {
                System.out.println((i + 1) + ". " + choices[i] + ".");
            }
            System.out.print(promptChoiceString);
            while (!scanner.hasNext())
                ;
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (choice >= 1 && choice <= choices.length) {
                    break;
                }
            } else {
                scanner.next();
            }

            System.out.println(invalidChoiceString);
        }
        return choice;
    }
}
