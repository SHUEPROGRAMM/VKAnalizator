package org.example.Console;

import org.example.Clients.VKToken;
import org.example.Colors;
import org.example.General;

import java.util.ArrayList;

public class Token {
    public static boolean run() {
        if (General.input.strings.size() - General.input.index < 2) {
            System.out.println("Error not token command");
            return false;
        }

        switch (General.input.strings.get(++General.input.index)) {
            case "add" -> {
                ++General.input.index;
                ArrayList<Input.AccessToken> accessTokens = General.input.getAccessTokens();
                if (accessTokens == null) return false;

                for (Input.AccessToken element : accessTokens)
                    if (General.vkTokens.add(element.id(), element.accessToken()))
                        System.out.println(Colors.ANSI_GREEN + "added: " + element.id() + ":" + element.accessToken() + Colors.ANSI_RESET);
            }
            case "info" -> {
                System.out.println("Token's count: " + General.vkTokens.data.size());
                for (int index = 0; index < General.vkTokens.data.size(); ++index)
                    System.out.println(Integer.toString(index) + "\t:" + General.vkTokens.data.get(index));
            }
            case "remove" -> {
                ArrayList<Integer> vkTokens = General.input.getIntegers();
                if (vkTokens == null) return false;
                General.vkTokens.remove(vkTokens);
            }
            case "clear" -> { General.vkTokens.clear(); }
            default -> {
                System.out.println("Error not token command: " + General.input.strings.get(General.input.index));
                return false;
            }
        }

        ++General.input.index;
        return true;
    }
}
