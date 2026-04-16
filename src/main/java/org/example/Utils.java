package org.example;

import java.util.ArrayList;

public class Utils {
    public static boolean binSearch(ArrayList<Integer> data, int element) {
        int begin = 0, end = data.size();
        while (begin < end) {
            int index = begin + ((end - begin) >> 1);
            int mid = data.get(index);

            if (element < mid) end = index;
            else if (element > mid) begin = index + 1;
            else return true;
        } return false;
    }

    public static int binSearchIndexOf(ArrayList<Integer> data, int element) {
        int begin = 0, end = data.size();
        while (begin < end) {
            int index = begin + ((end - begin) >> 1);
            int mid = data.get(index);

            if (element < mid) end = index;
            else if (element > mid) begin = index + 1;
            else return index;
        } return -1;
    }

    public static boolean isInteger(String string) {
        for (char element : string.toCharArray())
            if (!Character.isDigit(element)) return false;
        return true;
    }

    public static boolean isBDate(String str) {
        int num = 0, count = 0;
        for (char element : str.toCharArray()) {
            if (!Character.isDigit(element)) {
                if (num == 0) return false;
                num = 0; ++count;
            } else num = (num * 10) + (int)(element - '0');
        } return num != 0 && count >= 1 && count <= 2;
    }

    public static int addBDate(String str) {
        int[] dates = new int[3];
        int index = 0, result = 0;

        for (char element : str.toCharArray()) {
            if (Character.isDigit(element)) dates[index] = (dates[index] * 10) + (int)(element - '0');
            else ++index;
        } --dates[1];

        if (str.length() > 6) {
            if (dates[2] > 2000) result += 120000;
            dates[2] %= 100;
            result += 1200 * dates[2];
            result += dates[1] * 100;
            result += dates[0];
        } else result = -(dates[1] * 100) - dates[0];
        return result;
    }

    public static String toBBate(int data) {
        if (data < 0) {
            data = - data;
            return String.format("%d:%d", data % 100, (data / 100) + 1);
        } else {
            int day = data % 100;
            int age;
            if (data > 120000) {
                age = 2000;
                data -= 120000;
            } else age = 1900;
            age += data / 1200;
            int month = ((data / 100) % 12) + 1;
            return String.format("%d:%d:%d", day, month, age);
        }
    }

    public static String arrayToString(int[] data) {
        StringBuffer buffer = new StringBuffer();
        for (int index = 0; index < data.length - 1; ++index)
            buffer.append(data[index]).append(',');
        buffer.append(data[data.length - 1]);
        return buffer.toString();
    }

    public static class DecomposeTheQuantity {
        public final int count, fix, thread;
        public int begin = 0, end = 0, index = 0;

        public DecomposeTheQuantity(int size, int thread) {
            this.thread = Math.min(size, thread);
            this.count = size / this.thread;
            this.fix = size % this.thread;
        }

        public void next0() {
            this.end += count;
            if (index < fix) ++this.end;
            index++;
        }

        public void next1() {
            this.begin = end;
        }

        public void reset() {
            this.begin = 0;
            this.end = 0;
            this.index = 0;
        }
    }

    public static boolean isCorrectBDateRange(int one, int two) {
        return (one < 0 && two < 0 && one >= two) || (one > 0 && two > 0 && two > one);
    }

    public static <T> ArrayList<T> removeIndices(ArrayList<T> data, ArrayList<Integer> indices) {
        if (indices.isEmpty()) return data;
        ArrayList<T> buffer = new ArrayList<>(data.subList(0, indices.getFirst()));

        for (int index = 1; index < indices.size() - 1; ++index) {
            if (indices.get(index + 1) - indices.get(index) != 1)
                buffer.addAll(data.subList(indices.get(index) + 1, indices.get(index + 1)));
        }

        buffer.addAll(data.subList(indices.get(indices.size() - 1) + 1, data.size()));
        return buffer;
    }

    public static boolean readByte(short value, int poz) {
        return ((value >> (poz)) & 1) == 1;
    }

    public static short writeByte(short value, int poz) {
        return (short) (value ^ (short) (1 << (poz)));
    }

    public int countThreadServer() {
        int count = General.threadCount / General.server.nodes.size();
        return (count == 0) ? 1 : count;
    }

    public static void outConsole(int[] array) {
        for (int a = 0; a < array.length - 1; ++a)
            System.out.print(array[a] + ", ");
        System.out.println(array[array.length - 1]);
    }
}
