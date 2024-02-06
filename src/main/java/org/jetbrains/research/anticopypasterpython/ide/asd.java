package org.jetbrains.research.anticopypasterpython.ide;

public class asd {

    public static class Person {
        private static int extracted(int n1, int n2) {
            int gcd = 1;
            for (int i = 1; i <= n1 && i <= n2; i++) {
                if (n1 % i == 0 && n2 % i == 0) {
                    gcd = i;
                }
            }
            return gcd;
        }
    }

    public static class ASD {
        private static int extracted(int n1, int n2) {
            int gcd = 1;
            for (int i = 1; i <= n1 && i <= n2; i++) {
                if (n1 % i == 0 && n2 % i == 0) {
                    gcd = i;
                }
            }
            return gcd;
        }
    }

}
