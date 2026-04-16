package org.example.Console;

import org.example.Colors;
import org.example.General;

import java.io.IOException;

public class Server {
    public static boolean run() throws IOException {
        if (General.input.strings.size() - General.input.index < 2) {
            System.out.println(Colors.ANSI_RED + "Error not server command" + Colors.ANSI_RESET);
            return false;
        }

    q:  switch (General.input.strings.get(++General.input.index)) {
            case "start" -> {
                General.server = new org.example.Networks.Server.Server();
                General.server.start();
            }
            case "stop" -> {
                if (General.server == null) {
                    System.out.println(Colors.ANSI_RED + "Error the server is not running" + Colors.ANSI_RESET);
                    break q;
                }

                General.server.serverSocket.close();
                General.server.interrupt();
                General.server.close();
                General.server = null;
            }
            case "info" -> {
                if (General.server == null) {
                    System.out.println(Colors.ANSI_RED + "Error the server is not running" + Colors.ANSI_RESET);
                    break q;
                }

                synchronized (General.server.nodes) {
                    for (org.example.Networks.Server.Server.Node element : General.server.nodes)
                        System.out.println();
                }
            }
            default -> {
                System.out.println(Colors.ANSI_RED + "Error not server command: " + General.input.strings.get(General.input.index) + Colors.ANSI_RESET);
                return false;
            }
        }

        ++General.input.index;
        return true;
    }
}
