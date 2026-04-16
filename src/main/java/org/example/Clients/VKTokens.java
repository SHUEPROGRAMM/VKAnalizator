package org.example.Clients;

import org.example.Colors;

import java.util.ArrayList;
import java.util.TreeSet;

public class VKTokens {
    public final ArrayList<VKToken> data = new ArrayList<>();
    public final TreeSet<Integer> ids = new TreeSet<>();

    public synchronized void remove(ArrayList<VKToken> tokens) {
        data.removeAll(tokens);
        for (VKToken element : tokens)
            ids.remove(element.id);
    }

    public synchronized boolean add(int id, String accessToken) {
        if (ids.contains(id)) return false;
        VKToken vkToken = new VKToken(id, accessToken);

        try {
            vkToken.friendsGet(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (VKToken.VkError e) {
            return false;
        }

        ids.add(id);
        data.add(vkToken);
        System.out.println(Colors.ANSI_GREEN + "Added aceessToken: " + Colors.ANSI_RESET + vkToken);
        return true;
    }

    public void clear() {
        data.clear();
        ids.clear();
    }
}
