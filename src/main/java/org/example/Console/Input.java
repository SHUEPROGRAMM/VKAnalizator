package org.example.Console;

import org.example.Colors;
import org.example.General;
import org.example.IOStream;
import org.example.Utils;

import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

public class Input {
    public record Names(String first, String last) {}
    public record AccessToken(int id, String accessToken) {}
    public record GenerateAndLevel(boolean generate, int level) {}
    public record Probability(int percent, boolean generate) {}

    public static class TwoDate {
        public final long one, two;

        public TwoDate(long one) {
            this.one = one;
            this.two = 0;
        }

        public TwoDate(long one, long two) {
            this.one = one;
            this.two = two;
        }

        public TwoDate(DataInputStream dataInputStream) throws IOException {
            one = dataInputStream.readLong();
            two = dataInputStream.readLong();
        }

        public void out(DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.writeLong(one);
            dataOutputStream.writeLong(two);
        }

        public boolean range() { return one != 0 && two != 0; }
        public boolean isDate() { return one != 0; }
    }

    private int _getPercent() {
        if (strings.size() - index < 3 || !Utils.isInteger(strings.get(++index))) {
            System.out.println(Colors.ANSI_RED + "Error not percent or not percent value" + Colors.ANSI_RESET);
            return -1;
        } return Integer.parseInt(strings.get(index));
    }

    public Probability getProbability() {
        if (strings.size() - index > 1 && strings.get(index).charAt(0) == '-') {
            if (strings.get(index).length() == 1) {
                System.out.println(errorLengthArgument);
                return null;
            }

            boolean generate = false;
            int percent = 100;

            while (strings.size() - index > 1 && strings.get(index).charAt(0) == '-') {
                if (strings.get(index).charAt(1) == '-') {
                    String str = strings.get(index).substring(2);
                    switch (str) {
                        case "generate" -> { generate = true; }
                        case "percent" -> {
                            percent = _getPercent();
                            if (percent == -1) return null;
                        }
                        default -> {
                            System.out.println(Colors.ANSI_RED + "Error not argument: " + str + Colors.ANSI_RESET);
                            return null;
                        }
                    }
                } else {
                    for (char element : strings.get(index).substring(1).toCharArray()) {
                        switch (element) {
                            case 'g' -> { generate = true; }
                            case 'p' -> {
                                percent = _getPercent();
                                if (percent == -1) return null;
                            }
                            default -> {
                                System.out.println(Colors.ANSI_RED + "Error not key: " + element + Colors.ANSI_RESET);
                                return null;
                            }
                        }
                    }
                } ++index;
            }
            return new Probability(percent, generate);
        } return new Probability(100, false);
    }

    public static class ScanNode {
        public final int type;
        public ArrayList<Integer> tokens;
        public final int rerty;
        public final long rertyTime;
        public final int level;

        public ScanNode(int type, ArrayList<Integer> tokens, int rerty, long rertyTime, int level) {
            this.type = type;
            this.tokens = tokens;
            this.rerty = rerty;
            this.rertyTime = rertyTime;
            this.level = level;
        }

        public ScanNode(DataInputStream dataInputStream) throws IOException {
            this.type = dataInputStream.read();
            this.tokens = IOStream.readIntArrayList(dataInputStream);
            this.rerty = dataInputStream.readInt();
            this.rertyTime = dataInputStream.readLong();
            this.level = dataInputStream.readInt();
        }

        public void out(DataOutputStream dataOutputStream) throws IOException {
            dataOutputStream.write(this.type);
            IOStream.writeIntArrayList(this.tokens, dataOutputStream);
            dataOutputStream.write(this.rerty);
            dataOutputStream.writeLong(this.rertyTime);
            dataOutputStream.writeInt(this.level);
        }
    }

    public static final String errorLengthArgument = Colors.ANSI_RED + "Error length argument" + Colors.ANSI_RESET;

    public ArrayList<String> strings;
    public int index = 0;

    public boolean next(String string) {
        System.out.print(string);
        String str = General.scanner.nextLine();
        if (str.isEmpty()) return false;
        this.strings = StringUtils.getString(str);
        if (this.strings == null) return false;
        this.index = 0;
        return true;
    }

    public ArrayList<Integer> getIntegers() {
        TreeSet<Integer> buffer = new TreeSet<>();
        while (strings.size() - index > 0) {
            try { buffer.add(Integer.parseInt(strings.get(index))); }
            catch (NumberFormatException e) { break; }
            ++index;

            if (strings.size() - index > 1 && strings.get(index).equals(",")) ++index;
            else break;
        }

        if (buffer.isEmpty()) {
            System.out.println(Colors.ANSI_RED + "Error not integers" + Colors.ANSI_RESET);
            return null;
        } return new ArrayList<>(buffer);
    }

    public ArrayList<String> getStrings() {
        HashSet<String> buffer = new HashSet<>();
        while (strings.size() - index > 0) {
            buffer.add(strings.get(index)); ++index;
            if (strings.size() - index > 1 && strings.get(index).equals(",")) ++index;
            else break;
        }

        if (buffer.isEmpty()) {
            System.out.println(Colors.ANSI_RED + "Error not string" + Colors.ANSI_RESET);
            return null;
        } return new ArrayList<>(buffer);
    }

    public ArrayList<DataInputStream> getDataInputStreams() {
        ArrayList<String> temp = getStrings();
        if (temp == null) return null;
        ArrayList<DataInputStream> buffer = new ArrayList<>();

        for (String element : temp) {
            try {
                buffer.add(new DataInputStream(new FileInputStream(element)));
            } catch (FileNotFoundException e) {
                System.out.println(Colors.ANSI_RED + "Error not found file: " + element + Colors.ANSI_RESET);
                return null;
            }
        } return buffer;
    }

    public ArrayList<Names> getNames() {
        ArrayList<Names> buffer = new ArrayList<>();
        while (strings.size() - index > 1) {
            if (strings.get(index).equals(",")) break;
            buffer.add(new Names(strings.get(index), strings.get(index + 1)));
            index += 2;

            if (strings.size() - index > 2 && strings.get(index).equals(",")) ++index;
            else break;
        }

        if (buffer.isEmpty()) {
            System.out.println(Colors.ANSI_RED + "Error not names" + Colors.ANSI_RESET);
            return null;
        } return buffer;
    }

    public ArrayList<AccessToken> getAccessTokens() {
        ArrayList<AccessToken> buffer = new ArrayList<>();
        while (strings.size() - index > 1) {
            try {
                int id = Integer.parseInt(strings.get(index));
                buffer.add(new AccessToken(id, strings.get(index + 1)));
            } catch (NumberFormatException e) {
                System.out.println(Colors.ANSI_RED + "Error get id in accessToken" + Colors.ANSI_RESET);
                return null;
            }

            if (strings.size() - index > 2 && strings.get(index).equals(",")) ++index;
            else break;
        }

        if (buffer.isEmpty()) {
            System.out.println(Colors.ANSI_RED + "Error not names" + Colors.ANSI_RESET);
            return null;
        } return buffer;
    }

    private int getPercentArgument() {
        ++index;
        try {
            return Integer.parseInt(strings.get(index));
        } catch (NumberFormatException e) {
            System.out.println(Colors.ANSI_RED + "Error get percent" + Colors.ANSI_RESET);
            return -1;
        }
    }

    public int getPercent() {
        if (strings.get(index).charAt(0) == '-') {
            int buffer;
            if (strings.get(index).length() == 1) {
                System.out.println(errorLengthArgument);
                return -1;
            }

            if (strings.get(index).charAt(1) == '-') {
                if (strings.get(index).substring(2).equals("percent")) {
                    buffer = getPercentArgument();
                    if (buffer == -1) return -1;
                } else {
                    System.out.println(Colors.ANSI_RED + "Error not argumnet: " + strings.get(index) + Colors.ANSI_RESET);
                    return -1;
                }
            } else {
                if (strings.get(index).charAt(1) == 'p') {
                    buffer = getPercentArgument();
                    if (buffer == -1) return -1;
                } else {
                    System.out.println(Colors.ANSI_RED + "Error not key: " + strings.get(index).charAt(2) + Colors.ANSI_RESET);
                    return -1;
                }
            } ++index;

            if (buffer < 1 || buffer > 100) {
                System.out.println(Colors.ANSI_RED + "Error percent < 1 or percent > 100" + Colors.ANSI_RESET);
                return -1;
            } return buffer;
        } return 100;
    }

    private Long getDate() {
        ++index;
        try {
            return General.simpleDateFormat.parse(strings.get(index)).getTime();
        } catch (ParseException e) {
            System.out.println(Colors.ANSI_RED + "Error unconvert date" + Colors.ANSI_RESET);
            return null;
        }
    }

    public Long getTime() {
        Long buffer = 0L;
        if (strings.size() - index > 0) {
            if (strings.get(index).charAt(0) == '-') {
                if (strings.get(index).length() == 1) {
                    System.out.println(errorLengthArgument);
                    return null;
                }

                if (strings.get(index).charAt(1) == '-') {
                    if (strings.get(index).substring(2).equals("date")) {
                        buffer = getDate();
                    } else {
                        System.out.println(Colors.ANSI_RED + "Error not argument: " + strings.get(index).substring(2) + Colors.ANSI_RESET);
                        return null;
                    }
                } else {
                    if (strings.get(index).charAt(1) == 'd') {
                        buffer = getDate();
                    } else {
                        System.out.println(Colors.ANSI_RED + "Error not key: " + strings.get(index).charAt(1) + Colors.ANSI_RESET);
                        return null;
                    }
                } ++index;
            }
        } return buffer;
    }

    private Integer getLevel() {
        ++index;
        try {
            int buffer = Integer.parseInt(strings.get(index));
            if (buffer < 1) {
                System.out.println(Colors.ANSI_RED + "Error level < 1" + Colors.ANSI_RESET);
                return null;
            } return buffer;
        } catch (NumberFormatException e) {
            System.out.println(Colors.ANSI_RED + "Error unconvert level" + Colors.ANSI_RESET);
            return null;
        }
    }

    public GenerateAndLevel getGenerateAndLevel() {
        boolean generate = false;
        int level = 2;

        while (strings.size() - index > 0) {
            if (strings.get(index).charAt(0) == '-') {
                if (strings.get(index).length() == 1) {
                    System.out.println(errorLengthArgument);
                    return null;
                }

                if (strings.get(index).charAt(1) == '-') {
                    switch (strings.get(index).substring(2)) {
                        case "generate" -> { generate = true; }
                        case "level" -> {
                            Integer temp = getLevel();
                            if (temp == null) return null;
                            level = temp;
                        }
                        default -> {
                            System.out.println(Colors.ANSI_RED + "Error not argument: " + strings.get(index).substring(2) + Colors.ANSI_RESET);
                            return null;
                        }
                    }
                } else {
                    for (char element : strings.get(index).substring(1).toCharArray()) {
                        switch (element) {
                            case 'g' -> { generate = true; }
                            case 'l' -> {
                                Integer temp = getLevel();
                                if (temp == null) return null;
                                level = temp;
                            }
                            default -> {
                                System.out.println(Colors.ANSI_RED + "Error not key: " + element + Colors.ANSI_RESET);
                                return null;
                            }
                        }
                    }
                } ++index;
            } else break;
        } return new GenerateAndLevel(generate, level);
    }

    public Boolean getGenerate() {
        boolean buffer = false;
        if (strings.get(index).charAt(0) == '-') {
            if (strings.get(index).length() == 1) {
                System.out.println(errorLengthArgument);
                return null;
            }

            if (strings.get(index).charAt(1) == '-') {
                if (strings.get(index).substring(2).equals("generate")) {
                    buffer = true;
                } else {
                    System.out.println(Colors.ANSI_RED + "Error not argument: " + strings.get(index).substring(2) + Colors.ANSI_RESET);
                    return null;
                }
            } else {
                if (strings.get(index).charAt(1) == 'g') {
                    buffer = true;
                } else {
                    System.out.println(Colors.ANSI_RED + "Error not key: " + strings.get(index).charAt(1) + Colors.ANSI_RESET);
                    return null;
                }
            } ++index;
        } return buffer;
    }

    private TwoDate getDate0() {
        if (strings.size() - index < 2) {
            System.out.println("Error not date argument");
            return null;
        } index += 2;

        try {
            return new TwoDate(General.simpleDateFormat.parse(strings.get(index - 1)).getTime());
        } catch (ParseException e) {
            System.out.println(e.toString());
            return null;
        }
    }

    private TwoDate getDate1() {
        if (strings.size() - index < 3) {
            System.out.println("Error not dateRange argument");
            return null;
        } index += 3;

        try {
            return new TwoDate(General.simpleDateFormat.parse(strings.get(index - 2)).getTime(), General.simpleDateFormat.parse(strings.get(index - 1)).getTime());
        } catch (ParseException e) {
            System.out.println(e.toString());
            return null;
        }
    }

    public TwoDate getTwoDate() {
        if (strings.get(index).charAt(0) == '-') {
            if (strings.get(index).length() > 2) {
                switch (strings.get(index).substring(2)) {
                    case "date" -> { return getDate0(); }
                    case "dateRange" -> { return getDate1(); }
                    default -> {
                        System.out.println(Colors.ANSI_RED + "Error not argument: " + strings.get(index).substring(2) + Colors.ANSI_RESET);
                        return null;
                    }
                }
            } else if (strings.get(index).length() == 2) {
                switch (strings.get(index).charAt(1)) {
                    case 'd' -> { return getDate0(); }
                    case 'D' -> { return getDate1(); }
                    default -> {
                        System.out.println(Colors.ANSI_RED + "Error not key: " + strings.get(index).charAt(1) + Colors.ANSI_RESET);
                        return null;
                    }
                }
            } else {
                System.out.println(Colors.ANSI_RED + "Error length argument's" + Colors.ANSI_RESET);
                return null;
            }
        } return new TwoDate(0);
    }



    public ScanNode getScan() {
        int rerty = 1;
        long rertyTime = 60000;
        ArrayList<Integer> tokens = null;
        int level = 1;

        if (strings.size() - index > 2) {
            if (strings.get(index).charAt(0) == '-') {
                if (strings.get(index).length() == 1) {
                    System.out.println("Error not length argument -");
                    return null;
                }

                if (strings.size() - index < 2) {
                    System.out.println("Error not arguments element");
                    return null;
                }

                if (strings.get(index).charAt(1) == '-') {
                    ++index;
                    switch (strings.get(index - 1).substring(2)) {
                        case "rerty" -> {
                            try {
                                rerty = Integer.parseInt(strings.get(index));
                            } catch (NumberFormatException e) {
                                System.out.println("Error unconvert rertyCount: " + strings.get(index));
                                return null;
                            }
                        }
                        case "level" -> {
                            try {
                                level = Integer.parseInt(strings.get(index));
                            } catch (NumberFormatException e) {
                                System.out.println("Error unconvert level: " + strings.get(index));
                                return null;
                            }
                        }
                        case "rertyTime" -> {
                            try {
                                rertyTime = Long.parseLong(strings.get(index));
                            } catch (NumberFormatException e) {
                                System.out.println("Error unconvert rertyTime: " + strings.get(index));
                                return null;
                            }
                        }
                        case "tokens" -> {
                            tokens = getIntegers();
                            if (tokens == null) {
                                System.out.println("Error not tokens ids");
                                return null;
                            } --index;
                        }
                        default -> {
                            System.out.println("Error not argument: " + strings.get(index - 1).substring(2));
                            return null;
                        }
                    }
                } else {
                    for (char element : strings.get(index).substring(1).toCharArray()) {
                        if (strings.size() - index < 3) {
                            System.out.println("Error not arguments element");
                            return null;
                        } ++index;
                        switch (element) {
                            case 'r' -> {
                                try {
                                    rerty = Integer.parseInt(strings.get(index));
                                } catch (NumberFormatException e) {
                                    System.out.println("Error unconvert rertyCount: " + strings.get(index));
                                    return null;
                                }
                            }
                            case 't' -> {
                                try {
                                    rertyTime = Long.parseLong(strings.get(index));
                                } catch (NumberFormatException e) {
                                    System.out.println("Error unconvert rertyTime: " + strings.get(index));
                                    return null;
                                }
                            }
                            case 'T' -> {
                                tokens = getIntegers();
                                if (tokens == null) {
                                    System.out.println("Error not tokens ids");
                                    return null;
                                } --index;
                            }
                            case 'l' -> {
                                try {
                                    level = Integer.parseInt(strings.get(index));
                                } catch (NumberFormatException e) {
                                    System.out.println("Error uncovert level: " + strings.get(index));
                                    return null;
                                }
                            }
                        }
                    }
                } ++index;
            }
        }

        int type = switch (strings.get(index)) {
            case "friends" -> 0;
            default -> -1;
        };

        if (type == -1) {
            System.out.println("Error not scan type: " + strings.get(index));
            return null;
        } ++index;

        if (rerty < 1) {
            System.out.println("Error rerty < 1");
            return null;
        }

        if (rertyTime < 200) {
            System.out.println("Error rertyTime < 200 ms");
            return null;
        }

        if (level < 1) {
            System.out.println("Error level < 1");
            return null;
        }

        return new ScanNode(type, tokens, rerty, rertyTime, level);
    }
}
