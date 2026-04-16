package org.example.Console;

import org.example.Colors;
import org.example.General;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class Client {
    public static boolean run() throws InterruptedException {
        if (General.input.strings.size() - General.input.index < 1) {
            System.out.println("Error not connect address");
            return false;
        } org.example.Networks.Client.Client client;

        try {
            ++General.input.index;
            client = new org.example.Networks.Client.Client(General.input.strings.get(General.input.index));
            ++General.input.index;
        } catch (IOException e) {
            System.out.println("Error no connect to: " + General.input.strings.get(General.input.index));
            return false;
        }

        client.start();
        client.join();
        return true;
    }
}
