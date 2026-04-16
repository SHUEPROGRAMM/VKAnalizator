package org.example.DB;

import java.util.ArrayList;

public class Chains extends Base <org.example.Algorithm.Chains.Base> {
    public Chains(ArrayList<org.example.Algorithm.Chains.Base> data) {
        super(data);
    }

    @Override
    public void outConsole() throws InterruptedException {
        System.out.println("items count: " + data.size());
        for (org.example.Algorithm.Chains.Base element : data)
            if (element.data != null) element.outConsole();
    }

    @Override
    public void outConsole(long date) throws InterruptedException {
        for (org.example.Algorithm.Chains.Base element : data)
            if (element.data != null) element.outConsole(date);
    }
}
