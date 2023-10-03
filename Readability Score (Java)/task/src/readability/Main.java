package readability;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Main <filename>");
            return;
        }

        String fileName = args[0];

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            StringBuilder textBuilder = new StringBuilder();
            String line;

            // Read the contents of the file
            while ((line = reader.readLine()) != null) {
                textBuilder.append(line);
                textBuilder.append("\n"); // Add a newline between lines
            }

            String text = textBuilder.toString();

            String[] words = text.split("\\s+");
            int wordResult = words.length;

            // Count sentences using regular expressions
            String[] sentences = text.split("[.!?]\\s+");
            int sentencesNum = sentences.length;

            // Calculate the number of characters (excluding spaces, newline, and tab)
            int charResult = text.replaceAll("[\\n\\t\\s]", "").length();

            // Calculate the number of syllables and polysyllables
            int syllables = countSyllables(words);
            int polysyllables = countPolysyllables(words);

            // Calculate ARI, FK, SMOG, and Coleman-Liau scores
            double ari = calculateARI(charResult, wordResult, sentencesNum);
            double fk = calculateFK(wordResult, syllables, sentencesNum);
            double smog = calculateSMOG(polysyllables, sentencesNum);
            double cl = calculateColemanLiau(charResult, wordResult, sentencesNum);

            // Calculate the approximate ages
            int ariAge = getAgeForScore((int) Math.round(ari));
            int fkAge = getAgeForScore((int) Math.round(fk));
            int smogAge = getAgeForScore((int) Math.round(smog));
            int clAge = getAgeForScore((int) Math.round(cl));

            // Output the results
            System.out.println("Words: " + wordResult);
            System.out.println("Sentences: " + sentencesNum);
            System.out.println("Characters: " + charResult);
            System.out.println("Syllables: " + syllables);
            System.out.println("Polysyllables: " + polysyllables);

            String choice = "all";
            System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): " + choice);


            if ("ARI".equalsIgnoreCase(choice) || "all".equalsIgnoreCase(choice)) {
                System.out.printf("Automated Readability Index: %.2f (about %d-year-olds).\n", ari, ariAge);
            }
            if ("FK".equalsIgnoreCase(choice) || "all".equalsIgnoreCase(choice)) {
                System.out.printf("Flesch–Kincaid readability tests: %.2f (about %d-year-olds).\n", fk, fkAge);
            }
            if ("SMOG".equalsIgnoreCase(choice) || "all".equalsIgnoreCase(choice)) {
                System.out.printf("Simple Measure of Gobbledygook: %.2f (about %d-year-olds).\n", smog, smogAge);
            }
            if ("CL".equalsIgnoreCase(choice) || "all".equalsIgnoreCase(choice)) {
                System.out.printf("Coleman–Liau index: %.2f (about %d-year-olds).\n", cl, clAge);
            }

            double averageAge = (ariAge + fkAge + smogAge + clAge) / 4.0;
            System.out.printf("\nThis text should be understood in average by %.2f-year-olds.\n", averageAge);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getAgeForScore(int score) {
        if (score <= 1) {
            return 6;
        } else if (score <= 2) {
            return 7;
        } else if (score <= 3) {
            return 8;
        } else if (score <= 4) {
            return 9;
        } else if (score <= 5) {
            return 10;
        } else if (score <= 6) {
            return 11;
        } else if (score <= 7) {
            return 12;
        } else if (score <= 8) {
            return 13;
        } else if (score <= 9) {
            return 14;
        } else if (score <= 10) {
            return 15;
        } else if (score <= 11) {
            return 16;
        } else if (score <= 12) {
            return 17;
        } else if (score <= 13) {
            return 18;
        } else {
            return 22;
        }
    }

    private static int countSyllables(String[] words) {
        int syllables = 0;
        for (String word : words) {
            syllables += countSyllablesInWord(word);
        }
        return syllables;
    }

    private static int countSyllablesInWord(String word) {
        word = word.toLowerCase().replaceAll("[.,!?]", "");
        if (word.length() <= 3) return 1;

        int syllables = 0;
        boolean prevVowel = false;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            boolean isVowel = "aeiouy".contains(String.valueOf(c));
            if (isVowel && !prevVowel) {
                syllables++;
            }
            prevVowel = isVowel;
        }
        if (word.endsWith("e")) {
            syllables--;
        }
        if (syllables == 0) {
            syllables = 1;
        }
        return syllables;
    }

    private static int countPolysyllables(String[] words) {
        int polysyllables = 0;
        for (String word : words) {
            int syllables = countSyllablesInWord(word);
            if (syllables > 2) {
                polysyllables++;
            }
        }
        return polysyllables;
    }

    private static double calculateARI(int charCount, int wordCount, int sentenceCount) {
        return 4.71 * ((double) charCount / wordCount) + 0.5 * ((double) wordCount / sentenceCount) - 21.43;
    }

    private static double calculateFK(int wordCount, int syllableCount, int sentenceCount) {
        return 0.39 * ((double) wordCount / sentenceCount) + 11.8 * ((double) syllableCount / wordCount) - 15.59;
    }

    private static double calculateSMOG(int polysyllableCount, int sentenceCount) {
        return 1.043 * Math.sqrt((double) polysyllableCount * (30.0 / sentenceCount)) + 3.1291;
    }

    private static double calculateColemanLiau(int charCount, int wordCount, int sentenceCount) {
        double L = ((double) charCount / wordCount) * 100;
        double S = ((double) sentenceCount / wordCount) * 100;
        return 0.0588 * L - 0.296 * S - 15.8;
    }
}