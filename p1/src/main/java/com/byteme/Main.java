package com.byteme;

import com.byteme.scanner.Scanner;
import com.byteme.scanner.Lexeme;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            printUsage();
            System.exit(1);
        }

        String fnameIn = args[0];
        String phase = args[1];

        if (phase.equals("--tokens")) {
            List<String> tokens =
                    scanTokens(new File(fnameIn)).stream()
                            .map(Lexeme::toString)
                            .collect(Collectors.toList());

            // Remove empty string from comments
            for (int i = 0; i < tokens.size(); i++) {
                if (tokens.get(i).equals("")) {
                    tokens.remove(i);
                    i--;
                }
            }

            System.out.print(String.join(" ", tokens));
        } else {
            printUsage();
            System.exit(1);
        }
    }

    private static List<Lexeme> scanTokens(File f) {
        Scanner scanner = new Scanner(f);
        return scanner.tokenize();
    }

    private static void printUsage() {
        System.out.println("Usage: java -jar compiler.jar SOURCE_FILE [—-tokens]\n");
    }
}
