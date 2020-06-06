package flashcards;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Flashcards flashcards = new Flashcards(scanner, args);
        flashcards.start();

        scanner.close();
    }
}