package org.example.Console;

import org.example.General;
import java.util.ArrayList;

public class Base {
    public static boolean isAcceessTokenIndices(ArrayList<Integer> data) {
        return data.getFirst() > -1 || data.getLast() < General.vkTokens.data.size();
    }
}
