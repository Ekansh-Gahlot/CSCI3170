import java.util.*;

public class ChoiceSelector {
    // I love Java 8
    private static class Action {
        Action(int index, String name, Runnable action) {
            this.index = index;
            this.name = name;
            this.action = action;
        }

        int index;
        String name;
        Runnable action;
    }

    private final Map<Integer, Action> choices = new HashMap<>();

    public ChoiceSelector addAction(int index, String choice, Runnable action) {
        choices.put(index, new Action(index, choice, action));
        return this;
    }

    private String PROMPT_CHOICE = "Please enter your choice: ";
    private String INVALID_CHOICE = "Invalid choice. Please try again.";

    public ChoiceSelector() {
    }

    public ChoiceSelector(String promptChoice, String invalidChoice) {
        if (promptChoice != null) {
            PROMPT_CHOICE = promptChoice;
        }
        if (invalidChoice != null) {
            INVALID_CHOICE = invalidChoice;
        }
    }

    public int run(Scanner scanner) {
        while (true) {
            for (Action action : choices.values()) {
                System.out.println(action.index + ". " + action.name);
            }
            System.out.print(PROMPT_CHOICE);
            try {
                int choiceIdx = scanner.nextInt();
                Action choice = choices.get(choiceIdx);
                if (choice == null) {
                    throw new IllegalArgumentException("Choice out of range");
                }
                scanner.nextLine(); // DK why need this line again, otherwise reading ISBN will directly encounter invalid input
                choice.action.run();
                return choiceIdx;
            } catch (Exception e) {
                System.out.println(INVALID_CHOICE);
                scanner.next(); // clear the invalid input
            }
        }
    }
}
