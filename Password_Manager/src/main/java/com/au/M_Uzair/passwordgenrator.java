package com.au.M_Uzair;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/* 
Password Generator 
+---------------+----------------------------+------------------------------------------------------+
|function       | input                      | output                                               |
+---------------+----------------------------+------------------------------------------------------+
|randomPswd     | int(number of characters)  | String (randomly generated password)                 |
|pswdStrength   | String(password)           | String (Weak, Medium, Strong)                        |
|copytoclipboard| String(password)        v  | String (confirmation message)                        |
+---------------+----------------------------+------------------------------------------------------+

*/

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

        boolean w = true, x = true, y = true, z = true;

        for (int i = 0; i < pswd.length(); i++) {
            char c = pswd.charAt(i);
            if (Character.isUpperCase(c) && w) {
                strength++;
                w = false;
            } else if (Character.isLowerCase(c) && x) {
                strength++;
                x = false;
            } else if (Character.isDigit(c) && y) {
                strength++;
                y = false;
            } else if (z) {
                strength++;
                z = false;
            }
        }

        if (len < 5) {
            return "Weak";
        } else if (len < 8 && strength < 3) {
            return "Weak";
        } else if (strength < 4 && len < 12) {
            return "Medium";
        } else if (strength == 4 && len >= 10) {
            return "Strong";
        }
        return "Medium";
    }

    public static String copytoclipboard(String pswd) {

        if ("".equals(pswd)) {
            return "No password to copy!";
        }

        StringSelection stringSelection = new StringSelection(pswd);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // Use the stringSelection as the owner to help manage the transfer
        clipboard.setContents(stringSelection, stringSelection);

        // // Add a small pause if the application exits immediately
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return "Password copied to clipboard!";
    }

    public static void main(String[] args) {
        String password = randomPswd(10);
        System.out.println(password);
        System.out.println(pswdStrength(password));

        System.out.println(copytoclipboard(password));
    }
}
