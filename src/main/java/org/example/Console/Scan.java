package org.example.Console;

import org.example.Clients.ScanClasses;
import org.example.Clients.VKToken;
import org.example.Colors;
import org.example.DB.Base;
import org.example.DB.Groups;
import org.example.DB.Users;
import org.example.General;
import org.example.IOStream;
import org.example.Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Scan {
    public static boolean run() throws InterruptedException {
        if (General.vkTokens.data.isEmpty()) {
            System.out.println("Error vkTokens is empty");
            return false;
        }

        ArrayList<Input.ScanNode> buffer = new ArrayList<>();
        ArrayList<ScanClasses.ScanBase> scan = new ArrayList<>();
        ++General.input.index;

        while (General.input.strings.size() - General.input.index > 3) {
            if (General.input.strings.get(General.input.index).equals("{")) {
                ++General.input.index;

                Input.ScanNode node = General.input.getScan();
                if (node == null) return false;

                synchronized (General.vkTokens) {
                    if (node.tokens != null) {
                        for (int element : node.tokens)
                            if (!General.vkTokens.data.containsKey(element)) {
                                System.out.println(Colors.ANSI_RED + "Error not accessToken id: " + element + Colors.ANSI_RESET);
                                return false;
                            }
                    } else node.tokens = new ArrayList<>(General.vkTokens.data.keySet());
                }

                ArrayList<Integer> temp;
                if (Utils.isInteger(General.input.strings.get(General.input.index))) {
                    temp = General.input.getIntegers();
                    if (temp == null) return false;
                } else {
                    Other.Node base = Other.getBase(false);
                    if (base == null || base.data == null) {
                        System.out.println("Error not base of scan");
                        return false;
                    }

                    switch (base.data) {
                        case Users users -> { if (node.type != 0) return false; }
                        case Groups groups -> { return false; }
                        default -> {
                            System.out.println(Colors.ANSI_RED + "Error not scan of chain" + Colors.ANSI_RESET);
                            return false;
                        }
                    } temp = (ArrayList<Integer>) base.data.data;
                }


                if (General.input.strings.get(General.input.index).equals("}")) {
                    ++General.input.index;
                    scan.add(
                            switch (node.type) {
                                case 0 -> new ScanClasses.ScanFriends(temp, node.tokens, node.rerty, node.rertyTime, node.level);
                                default -> throw new IllegalStateException("Unexpected value: " + node.type);
                            }
                    );
                } else return false;
            } else break;
        }

        if (scan.isEmpty()) {
            System.out.println("Error scan is Empty");
            return false;
        }

        for (ScanClasses.ScanBase element : scan)
            element.start();

        for (ScanClasses.ScanBase element : scan)
            element.join();
        return true;
    }
}
