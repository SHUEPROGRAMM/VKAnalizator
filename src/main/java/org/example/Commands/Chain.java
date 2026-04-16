package org.example.Commands;

import org.example.Colors;
import org.example.Console.Input;
import org.example.Networks.Client.Client;
import org.example.Networks.Data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Chain {
    public static class Node {
        public final boolean generate;
        public final Data.DB in, to;

        public Node(boolean generate, Data.DB in, Data.DB to) {
            this.generate = generate;
            this.in = in;
            this.to = to;
        }

        public Node(DataInputStream dataInputStream) throws IOException {
            generate = dataInputStream.readBoolean();
            in = new Data.DB(dataInputStream);
            to = new Data.DB(dataInputStream);
        }

        public void out(DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.writeBoolean(generate);
            in.out(dataOutputStream);
            to.out(dataOutputStream);
        }
    }

    public ArrayList<Node> data;
    public boolean error = false;

    public Chain(Input input, Client.Data data) {
        this.data = new ArrayList<>();

        while (input.strings.size() - input.index > 3) {
            Boolean generate = input.getGenerate();
            if (generate == null) {
                error = true;
                return;
            }

            Data.DB in = new Data.DB(input, data);
            if (in.error || in.id == data.index || in.getType(data) != 0) {
                System.out.println(Colors.ANSI_RED + "Error in type or more" + Colors.ANSI_RESET);
                error = true;
                return;
            }

            if (input.strings.size() - input.index < 3 || !input.strings.get(input.index).equals("to")) {
                System.out.println(Colors.ANSI_RED + "Error not to" + Colors.ANSI_RESET);
                error = true;
                return;
            }

            Data.DB to = new Data.DB(input, data);
            if (to.error || to.id == data.index || to.getType(data) != 0) {
                System.out.println(Colors.ANSI_RED + "Error to type or more" + Colors.ANSI_RESET);
                error = true;
                return;
            }

            this.data.add(new Node(generate, in, to));
            if (input.strings.size() - input.index > 4 && input.strings.get(input.index).equals(",")) ++input.index;
            else break;
        } --input.index;

        if (this.data.isEmpty()) {
            System.out.println("Error chain is empty");
            error = true;
        }
    }

    public Chain(DataInputStream dataInputStream) throws IOException {
        int count = dataInputStream.readInt();
        this.data = new ArrayList<>(count);

        for (int index = 0; index < count; ++index)
            data.add(new Node(dataInputStream));
    }

    public void out(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(data.size());
        for (Node element : data)
            element.out(dataOutputStream);
    }
}