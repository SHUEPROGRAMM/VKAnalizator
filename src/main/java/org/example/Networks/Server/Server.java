package org.example.Networks.Server;

import org.example.DB.Base;
import org.example.General;

import java.io.IOException;
import java.net.ServerSocket;

import java.io.*;
import java.net.*;
import java.util.*;


public class Server extends Thread {
    public class Node extends Thread {
        public final Socket socket;
        public final DataInputStream dataInputStream;
        public final DataOutputStream dataOutputStream;
        public final IOBase ioBase = new IOBase();

        public Node(Socket socket) throws IOException {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            this.socket = socket;
        }

        public void close() throws IOException {
            dataOutputStream.close();
            dataInputStream.close();
            socket.close();
            if (!done) synchronized (nodes) { nodes.remove(this); }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (interrupted()) {
                        close();
                    } if (!ioBase.run(this)) {
                        close();
                        break;
                    }
                } catch (IOException e) {
                    try {
                        this.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public String toString() { return socket.getLocalAddress() + ":" + socket.getPort(); }
    }

    public final ServerSocket serverSocket;
    public final LinkedList<Node> nodes = new LinkedList<>();
    public boolean done = false;

    public Server() throws IOException {
        this.serverSocket = new ServerSocket(General.port);
    }

    public void addClient() throws IOException {
        Socket socket = serverSocket.accept();
        synchronized (nodes) {
            Node node = new Node(socket);
            nodes.add(node);
            nodes.getLast().start();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (interrupted()) return;
                addClient();
            } catch (IOException e) {
                return;
            }
        }
    }

    public void close() throws IOException {
        done = true;
        for (Node element : nodes)
            element.close();
        nodes.clear();
    }
}
