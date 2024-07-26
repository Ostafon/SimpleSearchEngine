package search;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2 || !args[0].equals("--data")) {
            System.out.println("Please provide the --data argument followed by the file name.");
            return;
        }

        String filePath = args[1];
        List<String> lines = readFileToArrayList(filePath);

        if (lines == null) {
            System.out.println("Error reading the file.");
            return;
        }

        Map<String, List<Integer>> invertedIndex = buildInvertedIndex(lines);
        menu(lines, invertedIndex);
    }

    public static List<String> readFileToArrayList(String filePath) {
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
            return null;
        }
        return lines;
    }

    public static Map<String, List<Integer>> buildInvertedIndex(List<String> lines) {
        Map<String, List<Integer>> invertedIndex = new HashMap<>();
        for (int i = 0; i < lines.size(); i++) {
            String[] words = lines.get(i).toLowerCase().split("\\s+");
            for (String word : words) {
                invertedIndex.computeIfAbsent(word, k -> new ArrayList<>()).add(i);
            }
        }
        return invertedIndex;
    }

    private static void searchPerson(Scanner scanner, List<String> lines, Map<String, List<Integer>> invertedIndex) {
        System.out.println("Enter a search strategy (ALL, ANY, NONE):");
        String strategy = scanner.nextLine().trim().toUpperCase();

        System.out.println("Enter a name or email to search all suitable people.");
        String query = scanner.nextLine().trim().toLowerCase();

        Set<Integer> resultIndices = new HashSet<>();
        String[] queryWords = query.split("\\s+");

        switch (strategy) {
            case "ALL":
                resultIndices = searchAll(queryWords, invertedIndex, lines.size());
                break;
            case "ANY":
                resultIndices = searchAny(queryWords, invertedIndex);
                break;
            case "NONE":
                resultIndices = searchNone(queryWords, invertedIndex, lines.size());
                break;
            default:
                System.out.println("Invalid strategy. Please enter ALL, ANY, or NONE.");
                return;
        }

        if (resultIndices.isEmpty()) {
            System.out.println("No matching people found.");
        } else {
            for (Integer index : resultIndices) {
                System.out.println(lines.get(index));
            }
        }
    }

    private static Set<Integer> searchAll(String[] queryWords, Map<String, List<Integer>> invertedIndex, int totalLines) {
        Set<Integer> resultIndices = new HashSet<>();
        if (queryWords.length == 0) return resultIndices;


        resultIndices.addAll(invertedIndex.getOrDefault(queryWords[0], Collections.emptyList()));

        for (int i = 1; i < queryWords.length; i++) {
            Set<Integer> currentIndices = new HashSet<>(invertedIndex.getOrDefault(queryWords[i], Collections.emptyList()));
            resultIndices.retainAll(currentIndices);
        }

        return resultIndices;
    }

    private static Set<Integer> searchAny(String[] queryWords, Map<String, List<Integer>> invertedIndex) {
        Set<Integer> resultIndices = new HashSet<>();

        for (String word : queryWords) {
            resultIndices.addAll(invertedIndex.getOrDefault(word, Collections.emptyList()));
        }

        return resultIndices;
    }

    private static Set<Integer> searchNone(String[] queryWords, Map<String, List<Integer>> invertedIndex, int totalLines) {
        Set<Integer> resultIndices = new HashSet<>();
        for (int i = 0; i < totalLines; i++) {
            resultIndices.add(i);
        }

        for (String word : queryWords) {
            resultIndices.removeAll(invertedIndex.getOrDefault(word, Collections.emptyList()));
        }

        return resultIndices;
    }

    private static void printAllPeople(List<String> lines) {
        System.out.println("=== List of people ===");
        for (String line : lines) {
            System.out.println(line);
        }
    }

    public static void menu(List<String> lines, Map<String, List<Integer>> invertedIndex) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("=== Menu ===");
            System.out.println("1. Find a person");
            System.out.println("2. Print all people");
            System.out.println("0. Exit");

            String choiceStr = scanner.nextLine().trim();
            int choice = Integer.parseInt(choiceStr);

            switch (choice) {
                case 1:
                    searchPerson(scanner, lines, invertedIndex);
                    break;
                case 2:
                    printAllPeople(lines);
                    break;
                case 0:
                    System.out.println("Bye!");
                    return;
                default:
                    System.out.println("Incorrect option! Try again.");
            }
        }
    }
}
