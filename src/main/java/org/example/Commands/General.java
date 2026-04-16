package org.example.Commands;

import org.example.Console.Input;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class General {
    public int type;
    public boolean generate;
    public int dbtype;
    public boolean error = false;

    public General(Input input, int type) {
        dbtype = type;
        Boolean generate = input.getGenerate();

        if (generate == null) {
            error = true;
            return;
        } this.generate = generate;

        if (type == 0) {
            this.type = switch (input.strings.get(++input.index)) {
                case "friends" -> 0;
                case "subscribers" -> 1;
                case "groups" -> 2;
                default -> -1;
            };

            if (this.type == -1) error = true;
            if (this.type == 2) this.dbtype = 1;
        } else {

        }
    }

    public General(DataInputStream dataInputStream) throws IOException {
        generate = dataInputStream.readBoolean();
        type = dataInputStream.read();
    }

    public void out(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeBoolean(generate);
        dataOutputStream.write(type);
    }
}
