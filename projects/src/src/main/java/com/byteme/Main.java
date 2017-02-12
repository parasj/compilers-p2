package com.byteme;

import com.byteme.scanner.Scanner;
import com.byteme.scanner.ScannerToken;

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
                            .map(ScannerToken::toString)
                            .collect(Collectors.toList());

            System.out.println(String.join(" ", tokens));
        } else {
            printUsage();
            System.exit(1);
        }
    }

    private static List<ScannerToken> scanTokens(File f) {
        Scanner scanner = new Scanner(f);
        return scanner.tokenize();
    }

    private static void printUsage() {
        System.out.println("Usage: java -jar compiler.jar SOURCE_FILE [—-tokens]\n");
    }
}
