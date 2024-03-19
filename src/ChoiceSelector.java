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

    private static final String PROMPT_CHOICE = "Please enter your choice: ";
    private static final String INVALID_CHOICE = "Invalid choice. Please try again.";

    public int run() {
        Scanner scanner = new Scanner(System.in);
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
                choice.action.run();
                return choiceIdx;
            } catch (Exception e) {
                System.out.println(INVALID_CHOICE);
            }
        }
    }
}
