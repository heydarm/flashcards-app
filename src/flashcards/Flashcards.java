package flashcards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class Flashcards {
    private final Map<String, String> flashcards;
    private final Map<String, Integer> errorStats;
    private final ArrayList<String> log;
    private final Scanner scanner;
    private String importFile;
    private String exportFile;

    public Flashcards(Scanner scanner) {
        this.scanner = scanner;
        this.importFile = null;
        this.exportFile = null;

        flashcards = new LinkedHashMap<>();
        errorStats = new HashMap<>();
        log = new ArrayList<>();
    }

    public Flashcards(Scanner scanner, String[] args) {
        this(scanner);
        handleArgs(args);
    }

    public void start() {
        if (importFile != null) {
            importCard();
        }

        while (true) {
            print("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):\n");
            String action = log(scanner.nextLine());

            switch (action) {
                case "add":
                    add();
                    break;
                case "remove":
                    remove();
                    break;
                case "import":
                    importCard();
                    break;
                case "export":
                    exportCard();
                    break;
                case "ask":
                    ask();
                    break;
                case "exit":
                    print("Bye bye!\n");

                    if (exportFile != null) {
                        exportCard();
                    }
                    return;
                case "log":
                    saveLog();
                    break;
                case "hardest card":
                    hardestCard();
                    break;
                case "reset stats":
                    resetStats();
                    break;
                default:
                    print("Sorry, there is no such action. Please try again.\n");
            }
        }
    }

    // ================================================================= //
    // ==================== Primary functions start ==================== //
    // ================================================================= //
    private void add() {
        print("The card:\n");
        String card = log(scanner.nextLine());

        if (flashcards.containsKey(card)) {
            print(String.format("The card \"%s\" already exists.\n\n", card));
            return;
        }

        print("The definition of the card:\n");
        String definition = log(scanner.nextLine());

        if (flashcards.containsValue(definition)) {
            print(String.format("The definition \"%s\" already exists.\n\n", definition));
            return;
        }

        flashcards.put(card, definition);
        print(String.format("The pair (\"%s\":\"%s\") has been added.\n\n", card, definition));
    }

    private void remove() {
        print("The card:\n");
        String card = log(scanner.nextLine());

        if (flashcards.containsKey(card)) {
            flashcards.remove(card);
            errorStats.remove(card);
            print("The card has been removed.\n\n");
        } else {
            print(String.format("Can't remove \"%s\": there is no such card.\n\n", card));
        }
    }

    private void importCard() {
        int countCards = 0;
        File file;

        if (importFile == null) {
            print("File name:\n");
            file = new File(log(scanner.nextLine()));
        } else {
            file = new File(importFile);
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                // [0] - card, [1] - definition, [2] - number of errors
                String[] flashcard = scanner.nextLine().split(":");
                flashcards.put(flashcard[0], flashcard[1]);

                if (Integer.parseInt(flashcard[2]) > 0) {
                    errorStats.put(flashcard[0], Integer.parseInt(flashcard[2]));
                }

                countCards++;
            }

            print(String.format("%s cards have been loaded\n", countCards));
        } catch (FileNotFoundException e) {
            print("File not found\n\n");
        } catch (Exception e) {
            print(String.format("Error: %s\n\n", e.getMessage()));
        }
    }

    private void exportCard() {
        File file;

        if (exportFile == null) {
            print("File name:\n");
            file = new File(log(scanner.nextLine()));
        } else {
            file = new File(exportFile);
        }

        try (PrintWriter printWriter = new PrintWriter(file)) {
            for (var entry : flashcards.entrySet()) {
                printWriter.printf("%s:%s:%s\n",
                        entry.getKey(),
                        entry.getValue(),
                        errorStats.getOrDefault(entry.getKey(), 0));
            }
        } catch (Exception e) {
            print(String.format("Error: %s\n\n", e.getMessage()));
        }

        print(String.format("%s cards have been saved.\n\n", flashcards.size()));
    }

    private void ask() {
        if (flashcards.size() < 1) {
            print("There is nothing to ask.\n\n");
            return;
        }

        print("How many times to ask?\n");
        int times = Integer.parseInt(log(scanner.nextLine()));

        for (int i = 0; i < times; i++) {
            Map<String, String> randomCard = takeRandomCard();

            String card = randomCard.keySet().iterator().next();
            String correctDef = flashcards.get(card);

            print(String.format("Print the definition of \"%s\":\n", card));
            String input = log(scanner.nextLine());

            if (correctDef.equals(input)) {
                print("Correct answer.\n");
            } else if (flashcards.containsValue(input)) {
                print(String.format("Wrong answer. The correct one is \"%s\", " +
                                "you've just written the definition of \"%s\".\n",
                        correctDef, getCardByDef(flashcards, input)));

                errorStats.put(card, errorStats.getOrDefault(card, 0) + 1);
            } else {
                print(String.format("Wrong answer. The correct one is \"%s\".\n", correctDef));
                errorStats.put(card, errorStats.getOrDefault(card, 0) + 1);
            }
        }

        System.out.println();
    }

    private void hardestCard() {
        if (errorStats.size() == 0) {
            print("There are no cards with errors.\n");
        } else {
            ArrayList<String> maxMistakesCard = new ArrayList<>();
            int maxMistakes = 0;
            StringBuilder hardestCards = new StringBuilder();

            for (Integer mistakes : errorStats.values()) {
                if (mistakes > maxMistakes) {
                    maxMistakes = mistakes;
                }
            }

            for (var entry : errorStats.entrySet()) {
                if (entry.getValue() == maxMistakes) {
                    maxMistakesCard.add(entry.getKey());
                }
            }

            for (int i = 0; i < maxMistakesCard.size(); i++) {
                hardestCards.append(String.format("\"%s\"", maxMistakesCard.get(i)));

                if (i != maxMistakesCard.size() - 1) {
                    hardestCards.append(", ");
                }
            }

            String plurality = " is";
            if (maxMistakesCard.size() > 1) {
                plurality = "s are";
            }

            print(String.format("The hardest card%s %s. " +
                    "You have %s errors answering them.\n\n", plurality, hardestCards, maxMistakes));
        }
    }

    private void resetStats() {
        errorStats.clear();
        print("Card statistics has been reset.\n\n");
    }

    private void saveLog() {
        print("File name:\n");
        File file = new File(log(scanner.nextLine()));

        try (PrintWriter printWriter = new PrintWriter(file)) {
            log.forEach(line -> printWriter.println(line.strip()));
            print("The log has been saved.\n\n");
        } catch (Exception e) {
            print(String.format("Error: %s\n\n", e.getMessage()));
        }
    }
    // ================================================================= //
    // ==================== Primary functions end ====================== //
    // ================================================================= //


    private Map<String, String> takeRandomCard() {
        ArrayList<String> cards = new ArrayList<>(flashcards.keySet());
        Map<String, String> map = new HashMap<>();

        String key = cards.get(new Random().nextInt(cards.size()));
        map.put(key, flashcards.get(key));

        return map;
    }

    private String getCardByDef(Map<String, String> map, String val) {
        for (var entry : map.entrySet()) {
            if (entry.getValue().equals(val)) {
                return entry.getKey();
            }
        }

        return null;
    }

    private String log(String str) {
        log.add(str);
        return str;
    }

    private void print(String str) {
        System.out.print(str);
        log.add(str);
    }

    private void handleArgs(String[] args) {
        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equals("-import")) {
                this.importFile = args[i + 1];
            } else if (args[i].equals("-export")) {
                this.exportFile = args[i + 1];
            }
        }
    }
}
