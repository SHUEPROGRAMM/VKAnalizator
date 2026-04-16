package org.example.Commands;

import org.example.Colors;
import org.example.Console.Input;
import org.example.Networks.Client.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Command {
    public boolean copy = false;
    public Object data;
    public int dbtype;
    public boolean error = false;
    public Input.TwoDate date;

    public Command(Input input, int dbtype, Client.Data data, boolean copy) {
        this.copy = copy;
        this.dbtype = dbtype;

        switch (input.strings.get(input.index)) {
            case "out" -> { this.data = 0; }
            case "count" -> { this.data = 1; }
            case "filter", "remove" -> {
                if (dbtype == 2) {
                    System.out.println(Colors.ANSI_RED + "Error chain no filter" + Colors.ANSI_RESET);
                    error = true;
                    return;
                }

                ++input.index;
                boolean remove = input.strings.get(input.index - 1).equals("remove");
                this.data = new Filter(input, dbtype, remove);
                Filter filter = (Filter) this.data;
                if (filter.error) error = true;
            }
            case "chain" -> {
                if (dbtype != 0) {
                    System.out.println(Colors.ANSI_RED + "Error chain type" + Colors.ANSI_RESET);
                    error = true;
                    return;
                } ++input.index;

                this.dbtype = 2;
                this.data = new Chain(input, data);
                Chain chain = (Chain) this.data;
                if (chain.error) this.error = true;
            }
            case "general" -> {
                if (dbtype == 2) {
                    System.out.println(Colors.ANSI_RED + "Error not general of chain's" + Colors.ANSI_RESET);
                    error = true;
                    return;
                }

                ++input.index;
                this.data = new General(input, dbtype);
                General temp = (General) this.data;
                if (temp.error) {
                    error = true;
                    return;
                } this.dbtype = temp.dbtype;
            }
            default -> {

            }
        } ++input.index;
    }

    public Command(DataInputStream dataInputStream, boolean copy) throws IOException {
        this.copy = copy;
        this.date = new Input.TwoDate(dataInputStream);
        int type = dataInputStream.read();

        switch (type) {
            case 0 -> { this.data = dataInputStream.read(); }
            case 1 -> { this.data = new Chain(dataInputStream); }
            case 2 -> { this.data = new Filter(dataInputStream); }
            case 3 -> { this.data = new General(dataInputStream); }
        }
    }

    public void out(DataOutputStream dataOutputStream) throws IOException {
        this.date.out(dataOutputStream);
        switch (this.data) {
            case Integer integer -> {
                dataOutputStream.write(0);
                dataOutputStream.write(integer);
            }
            case Chain chain -> {
                dataOutputStream.write(1);
                chain.out(dataOutputStream);
            }
            case Filter filter -> {
                dataOutputStream.write(2);
                filter.out(dataOutputStream);
            }
            case General general -> {
                dataOutputStream.write(3);
                general.out(dataOutputStream);
            }
            default -> throw new IllegalStateException("Unexpected value: " + this.data);
        }
    }
}