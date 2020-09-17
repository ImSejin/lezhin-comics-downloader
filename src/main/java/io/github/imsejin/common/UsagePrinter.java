package io.github.imsejin.common;

public final class UsagePrinter {

    private UsagePrinter() {
    }

    public static void printAndQuit(String... messages) {
        System.out.println();

        print(messages);

        System.out.println();
        System.exit(1);
    }

    private static void print(String... messages) {
        for (String message : messages) {
            System.err.println("    " + message);
        }
    }

}
