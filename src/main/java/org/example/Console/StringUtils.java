package org.example.Console;

import java.util.ArrayList;

public class StringUtils {
    public static ArrayList<String> getString(String string) {
        ArrayList<String> buffer = new ArrayList<>();
        for (int a = 0; a < string.length(); ++a) {
            if (string.charAt(a) != ' ') {
                boolean done = false;
            q:  for (int b = a; b < string.length(); ++b) {
                    switch (string.charAt(b)) {
                        case ' ' -> {
                            buffer.add(string.substring(a, b));
                            a = b; done = true; break q;
                        }
                        case ',', '>', '&', '{', '}', '=', '<' -> {
                            if (a != b) buffer.add(string.substring(a, b));
                            buffer.add(string.substring(b, b + 1));
                            a = b; done = true; break q;
                        }
                        case '\'' -> {
                            for (int c = b + 1; c < string.length(); ++c) {
                                if (string.charAt(c) == '\'') {
                                    buffer.add(string.substring(b + 1, c));
                                    a = c; done = true; break q;
                                }
                            }

                            System.out.println("Error");
                            return null;
                        }
                        case '"' -> {
                            StringBuilder stringBuilder = new StringBuilder();
                        q2:  for (int c = b + 1; c < string.length(); ++c) {
                                switch (string.charAt(c)) {
                                    case '\\' -> {
                                        if (string.length() - c < 3) {
                                            System.out.println("Error");
                                            return null;
                                        } ++c;

                                        if (string.charAt(c) == '\\' || string.charAt(c) == '"') stringBuilder.append(string.charAt(c));
                                        else if (string.charAt(c) == 'n') stringBuilder.append('\n');
                                        else {
                                            System.out.println("Error");
                                            return null;
                                        }
                                    }
                                    case '"' -> { a = c; done = true; break q2; }
                                    default -> { stringBuilder.append(string.charAt(c)); }
                                }
                            }

                            if (!done) {
                                System.out.println("Error");
                                return null;
                            }

                            buffer.add(stringBuilder.toString());
                            break q;
                        }
                    }
                } if (!done){
                    buffer.add(string.substring(a));
                    break;
                }
            }
        }

        if (buffer.isEmpty()) {
            System.out.println("Error");
            return null;
        } return buffer;
    }
}
