package org.example;

import org.example.Console.*;

import java.io.*;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        General.init();
    q:  while (true) {
            if (!General.input.next(Colors.ANSI_GREEN + "> " + Colors.ANSI_RESET)) continue;
            while (General.input.strings.size() - General.input.index > 0) {
                switch (General.input.strings.get(General.input.index)) {
                    case "dataBase" -> { if (!DataBase.run()) continue q; }
                    case "token" -> { if (!Token.run()) continue q; }
                    case "scan" -> { if (!Scan.run()) continue q; }
                    case "help" -> {
                        System.out.println(General.help);
                        ++General.input.index;
                    }
                    case "client" -> { if (!Client.run()) continue q; }
                    case "server" -> { if (!Server.run()) continue q; }
                    case "about" -> {
                        ++General.input.index;
                    }
                    case "exit" -> {
                        General.scanner.close();
                        if (General.server != null) General.server.close();
                        System.exit(0);
                    }
                    default -> { if (!Other.run()) continue q; }
                }

                if (General.input.strings.size() - General.input.index > 0) {
                    if (General.input.strings.get(General.input.index).equals("&")) {
                        if (General.input.strings.size()- General.input.index > 1) ++General.input.index;
                        else {
                            System.out.println("Error not command");
                            continue q;
                        }
                    } else {
                        System.out.println("Error expected &");
                        continue q;
                    }
                } else break;
            }
        }
    }
}