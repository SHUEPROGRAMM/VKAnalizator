package org.example.VKRequest;

import java.util.ArrayList;

public class RequestsGetID extends RequestsGet {
    public final long date;
    public final int id;
    public final boolean full;

    public RequestsGetID(long date, ArrayList<Node> data, int id, boolean full) {
        super(data);
        this.date = date;
        this.id = id;
        this.full = full;
    }
}
