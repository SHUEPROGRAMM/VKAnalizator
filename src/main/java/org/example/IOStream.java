package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class IOStream {
    public static int[] readIntArray(DataInputStream dataInputStream) throws IOException {
        int count = dataInputStream.readInt();
        if (count == 0) return null;
        int[] buffer = new int[count];

        for (int index = 0; index < buffer.length; ++index) {
            buffer[index] = dataInputStream.readInt();
        } return buffer;
    }

    public static void writeIntArray(int[] array, DataOutputStream dataOutputStream) throws IOException {
        if (array == null) {
            dataOutputStream.writeInt(0);
            return;
        }

        dataOutputStream.writeInt(array.length);
        for (int element : array)
            dataOutputStream.writeInt(element);
    }
    
    public static ArrayList<Integer> readIntArrayList(DataInputStream dataInputStream) throws IOException {
        int count = dataInputStream.readInt();
        if (count == 0) return null;
        ArrayList<Integer> buffer = new ArrayList<>(count);
        
        for (int a = 0; a < count; ++a)
            buffer.add(dataInputStream.readInt());
        return buffer;
    }
    
    public static void writeIntArrayList(ArrayList<Integer> data, DataOutputStream dataOutputStream) throws IOException {
        if (data == null) {
            dataOutputStream.writeInt(0);
            return;
        } dataOutputStream.writeInt(data.size());
        
        for (int element : data)
            dataOutputStream.writeInt(element);
    }
}
