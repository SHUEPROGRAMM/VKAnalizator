package org.example.Clients;

import java.util.ArrayList;
import java.util.TreeMap;

public class VKTokens {
    public final TreeMap<Integer, VKToken> data = new TreeMap<>();

    public synchronized void remove(ArrayList<Integer> data) {
        for (int element : data)
            this.data.remove(element);
    }

    public synchronized boolean add(int id, String accessToken) {
        if (data.containsKey(id)) return false;
        VKToken vkToken = new VKToken(id, accessToken);
        try { vkToken.friendsGet(1); } catch (InterruptedException | VKToken.VkError e) { return false; }
        data.put(id, vkToken);
        return true;
    }

    public void clear() { data.clear(); }
}
