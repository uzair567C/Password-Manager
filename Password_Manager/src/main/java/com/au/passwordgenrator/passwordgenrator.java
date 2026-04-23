package com.au.passwordgenrator;

public class passwordgenrator {

    static char[] Lowercase = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
            'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
    static char[] Uppercase = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
            'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
    static char[] symbols = { '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '{', '}', '[', ']', '|', '?', '/',
            '~', '`', '-' };
    static int[] digits = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    public static String randomPswd(int num) {

        String pswd = "";

        if (num <= 0) {
            return "Incorrect Input!";
        }

        for (int i = 0; i < num; i++) {
            int randNum = (int) (Math.random() * 100);
            switch (randNum % 4) {
                case 0 -> pswd += Lowercase[(int) ((Math.random() * 100) % Lowercase.length)];
                case 1 -> pswd += Uppercase[(int) ((Math.random() * 100) % Uppercase.length)];
                case 2 -> pswd += symbols[(int) ((Math.random() * 100) % symbols.length)];
                default -> pswd += digits[(int) ((Math.random() * 100) % digits.length)];
            }
        }

        return pswd;
    }

    public static String pswdStrength(String pswd) {

        int len = pswd.length();
        int strength = 0;

        for (int i = 0; i < pswd.length(); i++) {
            char c = pswd.charAt(i);
            if (Character.isUpperCase(c))
                strength++;
            else if (Character.isLowerCase(c))
                strength++;
            else if (Character.isDigit(c))
                strength++;
            else
                strength++; // Assuming remaining are symbols
        }

        if (len < 4) {
            return "Weak";
        } else if (len < 8 && strength < 4) {

        } else {

        }

        return "";
    }

    public static void main(String[] args) {
        String password = randomPswd(12);
        System.out.println(password);
        pswdStrength(password);
    }
}
