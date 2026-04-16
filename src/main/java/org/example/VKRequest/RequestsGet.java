package org.example.VKRequest;

import com.google.gson.JsonArray;

import java.util.ArrayList;

public class RequestsGet {
    public static class Node implements Comparable<Node> {
        public final long date;
        public final JsonArray data;

        public Node (long date, JsonArray data) {
            this.date = date;
            this.data = data;
        }

        @Override
        public int compareTo(Node o) {
            return Long.compare(this.date, o.date);
        }
    }

    public final ArrayList<Node> data;

    public RequestsGet(ArrayList<Node> data) { this.data = data; }
}
