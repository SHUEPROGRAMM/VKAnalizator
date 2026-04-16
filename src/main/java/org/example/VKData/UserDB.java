package org.example.VKData;

import org.example.Colors;
import org.example.Data.IDHistory;
import org.example.Data.IDsHistory;
import org.example.Data.OnlineHistory;
import org.example.Enum.UserIDEnum;
import org.example.Enum.UserIDsEnum;
import org.example.General;
import org.example.Utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeSet;

public class UserDB {
    public long phoneNumberLong = -1;
    public String phoneNumberString = null;

    public IDHistory[] idHistories = null;
    public IDsHistory[] iDsHistories = null;
    public OnlineHistory onlineHistory = null;

    public static final char[] chars = { 'F', 'L', 'N', 'S', 'D', 'T', 'C', 'S', 'B' };
    public static final char[] charIds = { 'F', 'S', 'G' };

    public void addPhoneNumber(String str) {
        if (phoneNumberLong > 0) return;
        phoneNumberLong = 0;

        for (char element : str.toCharArray()) {
            if (element == '*') {
                phoneNumberString = str;
                phoneNumberLong = 0;
                return;
            } else if (Character.isDigit(element)) phoneNumberLong = (phoneNumberLong * 10) + (long)(element - '0');
        }

        if (phoneNumberLong == 0) phoneNumberLong = -1;
        phoneNumberString = null;
    }

    public void load(DataInputStream dataInputStream, int[][] arrays, int id) throws IOException {
        long tempPhoneNumberLong = dataInputStream.readLong();
        String tempPhoneNumberString = (tempPhoneNumberLong == 0) ? dataInputStream.readUTF() : null;

        if (phoneNumberLong < 1) {
            phoneNumberLong = tempPhoneNumberLong;
            phoneNumberString = (phoneNumberLong > 0) ? null : tempPhoneNumberString;
        }

        int count = dataInputStream.readInt();
        if (count != -1) {
            if (idHistories == null) idHistories = new IDHistory[UserIDEnum.values().length];
            for (int index = 0; index < General.userStringCount; ++index) {
                if (count != 0) {
                    if (idHistories[index] == null) idHistories[index] = new IDHistory();
                    TreeSet<IDHistory.Node> nodes = IDHistory.loadNodes(dataInputStream, count, arrays[index]);

                    for (IDHistory.Node element : nodes)
                        General.idGenerateUsers[index].computeIfAbsent(element.id, s -> new TreeSet<>()).add(id);
                    idHistories[index].load(nodes);
                } count = dataInputStream.readInt();
            }

            for (int index = General.userStringCount; index < idHistories.length; ++index) {
                if (count != 0) {
                    if (idHistories[index] == null) idHistories[index] = new IDHistory();
                    TreeSet<IDHistory.Node> nodes = IDHistory.loadNodes(dataInputStream, count);

                    for (IDHistory.Node element : nodes)
                        General.idGenerateUsers[index].computeIfAbsent(element.id, s -> new TreeSet<>()).add(id);
                    idHistories[index].load(nodes);
                } count = dataInputStream.readInt();
            }
        } else count = dataInputStream.readInt();

        if (count != -1) {
            if (iDsHistories == null) iDsHistories = new IDsHistory[UserIDsEnum.values().length];
            if (count != 0) {
                if (iDsHistories[0] == null) iDsHistories[0] = new IDsHistory();
                IDsHistory.LoadNodes loadNodes = new IDsHistory.LoadNodes(dataInputStream, count);
                TreeSet<Integer> set = General.generateIds[0].computeIfAbsent(id, s -> new TreeSet<>());

                for (Map.Entry<Long, TreeSet<Integer>> element : loadNodes.added.entrySet()) {
                    for (int element_id : element.getValue())
                        General.generateIds[0].computeIfAbsent(element_id, s -> new TreeSet<>()).add(id);
                    set.addAll(element.getValue());
                } iDsHistories[0].load(loadNodes);
                count = dataInputStream.readInt();
            } else count = dataInputStream.readInt();

            if (count != 0) {
                if (iDsHistories[1] == null) iDsHistories[1] = new IDsHistory();
                IDsHistory.LoadNodes loadNodes = new IDsHistory.LoadNodes(dataInputStream, count);
                TreeSet<Integer> set = General.generateIds[1].computeIfAbsent(id, s -> new TreeSet<>());

                for (Map.Entry<Long, TreeSet<Integer>> element : loadNodes.added.entrySet()) {
                    for (int element_id : element.getValue())
                        General.generateIds[3].computeIfAbsent(element_id, s -> new TreeSet<>()).add(id);
                    set.addAll(element.getValue());
                } iDsHistories[1].load(loadNodes);
                count = dataInputStream.readInt();
            } else count = dataInputStream.readInt();

            if (count != 0) {
                if (iDsHistories[2] == null) iDsHistories[2] = new IDsHistory();
                IDsHistory.LoadNodes loadNodes = new IDsHistory.LoadNodes(dataInputStream, count);
                TreeSet<Integer> set = General.generateIds[2].computeIfAbsent(id, s -> new TreeSet<>());

                for (Map.Entry<Long, TreeSet<Integer>> element : loadNodes.added.entrySet()) {
                    for (int element_id : element.getValue())
                        General.generateIds[4].computeIfAbsent(element_id, s -> new TreeSet<>()).add(id);
                    set.addAll(element.getValue());
                } iDsHistories[2].load(loadNodes);
                count = dataInputStream.readInt();
            } else count = dataInputStream.readInt();
        } else count = dataInputStream.readInt();

        if (count != -1) {
            if (onlineHistory == null) onlineHistory = new OnlineHistory();
            onlineHistory.load(dataInputStream, count);
        }
    }

    public void save(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeLong(phoneNumberLong);
        if (phoneNumberLong == 0) dataOutputStream.writeUTF(phoneNumberString);

        if (idHistories != null) {
            for (IDHistory element : idHistories)
                if (element != null)
                    element.save(dataOutputStream);
                else dataOutputStream.writeInt(0);
        } else dataOutputStream.writeInt(-1);

        if (iDsHistories != null) {
            for (IDsHistory element : iDsHistories)
                if (element != null)
                    element.save(dataOutputStream);
                else dataOutputStream.writeInt(0);
        } else dataOutputStream.writeInt(-1);

        if (onlineHistory != null) {
            onlineHistory.save(dataOutputStream);
        } else dataOutputStream.writeInt(-1);
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if (idHistories != null) {
            for (int index = 0; index < General.userStringCount; ++index) {
                if (idHistories[index] != null) {
                    buffer.append(chars[index]);
                    buffer.append(Colors.ANSI_GREEN).append(General.userStrings[index].strings.get(idHistories[index].data.getLast().id)).append(Colors.ANSI_RESET);
                }
            }

            for (int index = General.userStringCount; index < UserIDEnum.values().length - 1; ++index) {
                if (idHistories[index] != null) {
                    buffer.append(chars[index]);
                    buffer.append(Colors.ANSI_GREEN).append(idHistories[index].data.getLast().id).append(Colors.ANSI_RESET);
                }
            }

            if (idHistories[UserIDEnum.BDATE.ordinal()] != null) buffer.append("B").append(Colors.ANSI_GREEN).append(Utils.toBBate(idHistories[UserIDEnum.BDATE.ordinal()].data.getLast().id)).append(Colors.ANSI_RESET);
        } else buffer.append("NoneName");


        if (iDsHistories != null) {
            buffer.append(":");
            for (int index = 0; index < UserIDsEnum.values().length; ++index)
                if (iDsHistories[index] != null)
                    buffer.append(charIds[index]).append(Colors.ANSI_GREEN).append((iDsHistories[index].last.data == null) ? "0" : Integer.toString(iDsHistories[index].last.data.length)).append(Colors.ANSI_RESET);
        }

        if (phoneNumberLong != -1) buffer.append('P').append(Colors.ANSI_GREEN).append((phoneNumberLong > 0) ? Long.toString(phoneNumberLong) : phoneNumberString).append(Colors.ANSI_RESET);
        return buffer.toString();
    }

    public String toString(long date) {
        StringBuilder buffer = new StringBuilder();
        if (idHistories != null) {
            for (int index = 0; index < General.userStringCount; ++index) {
                if (idHistories[index] != null) {
                    IDHistory.Node node = idHistories[index].get(date);
                    if (node == null) continue;
                    buffer.append(chars[index]);
                    buffer.append(Colors.ANSI_GREEN).append(General.userStrings[index].strings.get(node.id)).append(Colors.ANSI_RESET);
                }
            }

            for (int index = General.userStringCount; index < UserIDEnum.values().length - 1; ++index) {
                if (idHistories[index] != null) {
                    IDHistory.Node node = idHistories[index].get(date);
                    if (node == null) continue;
                    buffer.append(chars[index]);
                    buffer.append(Colors.ANSI_GREEN).append(node.id).append(Colors.ANSI_RESET);
                }
            }

            if (idHistories[UserIDEnum.BDATE.ordinal()] != null) {
                IDHistory.Node node = idHistories[UserIDEnum.BDATE.ordinal()].get(date);
                if (node != null) buffer.append("B").append(Colors.ANSI_GREEN).append(Utils.toBBate(node.id)).append(Colors.ANSI_RESET);
            }
        } else buffer.append("NoneName");

        if (iDsHistories != null) {
            buffer.append(':');
            for (int index = 0; index < UserIDsEnum.values().length; ++index) {
                if (iDsHistories[index] != null) {
                    int count = iDsHistories[index].size(date);
                    if (count != -1) buffer.append(charIds[index]).append(Colors.ANSI_GREEN).append(count).append(Colors.ANSI_RESET);
                }
            }
        }

        if (phoneNumberLong != -1) buffer.append("P").append(Colors.ANSI_GREEN).append((phoneNumberLong > 0) ? Long.toString(phoneNumberLong) : phoneNumberString).append(Colors.ANSI_RESET);
        return buffer.toString();
    }

    public boolean isEmpty() {
        return (phoneNumberLong == -1 || phoneNumberString == null || onlineHistory == null || idHistories == null || iDsHistories == null);
    }

    public void out(DataOutputStream dataOutputStream) throws IOException {
        short db = 0;
        if (this.idHistories != null) {
            for (int a = 0; a < this.idHistories.length; ++a)
                if (idHistories[a] != null) db = Utils.writeByte(db, a);
        }

        if (this.iDsHistories != null) {
            for (int a = 0; a < UserIDsEnum.values().length; ++a)
                if (this.iDsHistories[a] != null) db = Utils.writeByte(db, 9 + a);
        }

        if (phoneNumberLong != -1) db = Utils.writeByte(db, 12);
        if (phoneNumberLong == 0) db = Utils.writeByte(db, 13);

        dataOutputStream.writeShort(db);
        if (this.idHistories != null) {
            for (int a = 0; a < General.userStringCount; ++a)
                if (this.idHistories[a] != null) dataOutputStream.writeUTF(General.userStrings[a].strings.get(this.idHistories[a].data.getLast().id));

            for (int a = General.userStringCount; a < UserIDEnum.values().length; ++a)
                if (this.idHistories[a] != null) dataOutputStream.writeInt(this.idHistories[a].data.getLast().id);
        }

        if (this.iDsHistories != null)
            for (int a = 0; a < UserIDsEnum.values().length; ++a)
                if (this.iDsHistories[a] != null) dataOutputStream.writeInt((iDsHistories[a].last.data == null) ? 0 : this.iDsHistories[a].last.data.length);

        if (phoneNumberLong != -1) {
            if (phoneNumberLong == 0) dataOutputStream.writeUTF(phoneNumberString);
            else dataOutputStream.writeLong(phoneNumberLong);
        }
    }

    public void out(DataOutputStream dataOutputStream, long date) throws IOException {
        short db = 0;
        IDHistory.Node[] idNodes = new IDHistory.Node[UserIDEnum.values().length];
        int[] idsCounts = new int[UserIDsEnum.values().length];

        if (this.idHistories != null) {
            for (int a = 0; a < UserIDEnum.values().length; ++a) {
                if (this.idHistories[a] == null) continue;
                idNodes[a] = this.idHistories[a].get(date);
                if (idNodes[a] != null) db = Utils.writeByte(db, a);
            }
        }

        if (this.iDsHistories != null) {
            for (int a = 0; a < UserIDsEnum.values().length; ++a) {
                if (this.iDsHistories[a] == null) {
                    idsCounts[a] = -1;
                    continue;
                }

                idsCounts[a] = this.iDsHistories[a].size(date);
                if (idsCounts[a] != -1) db = Utils.writeByte(db, 9 + a);
            }
        }

        if (this.phoneNumberLong != -1) {
            db = Utils.writeByte(db, 12);
            if (phoneNumberLong == 0) db = Utils.writeByte(db, 13);
        }

        dataOutputStream.writeShort(db);

        if ((db & 31) > 0)
            for (int a = 0; a < General.userStringCount; ++a)
                if (idNodes[a] != null)
                    dataOutputStream.writeUTF(General.userStrings[a].strings.get(idNodes[a].id));

        if ((db & 480) > 0)
            for (int a = General.userStringCount; a < UserIDEnum.values().length; ++a)
                if (idNodes[a] != null) dataOutputStream.writeInt(idNodes[a].id);

        if ((db & 3584) > 0)
            for (int a = 0; a < UserIDsEnum.values().length; ++a)
                if (idsCounts[a] != -1) dataOutputStream.writeInt(idsCounts[a]);

        if (phoneNumberLong != -1) {
            if (phoneNumberLong == 0) dataOutputStream.writeUTF(phoneNumberString);
            else dataOutputStream.writeLong(phoneNumberLong);
        }
    }

    public static String toString(DataInputStream dataInputStream) throws IOException {
        StringBuilder buffer = new StringBuilder();
        short db = dataInputStream.readShort();

        if ((db & 31) > 0) {
            for (int a = 0; a < General.userStringCount; ++a)
                if (Utils.readByte(db, a)) buffer.append(chars[a]).append(Colors.ANSI_GREEN).append(dataInputStream.readUTF()).append(Colors.ANSI_RESET);
        } else buffer.append("No name");

        if ((db & 480) > 0)
            for (int a = General.userStringCount; a < UserIDEnum.values().length - 1; ++a)
                if (Utils.readByte(db, a)) buffer.append(chars[a]).append(Colors.ANSI_GREEN).append(dataInputStream.readInt()).append(Colors.ANSI_RESET);

        if ((db & 128) > 0) buffer.append("B").append(Colors.ANSI_GREEN).append(Utils.toBBate(dataInputStream.readInt())).append(Colors.ANSI_RESET);

        if ((db & 3584) > 0)
            for (int a = 0; a < 3; ++a)
                if (Utils.readByte(db, a + 9)) buffer.append(charIds[a]).append(Colors.ANSI_GREEN).append(dataInputStream.readInt()).append(Colors.ANSI_RESET);

        if ((db & 4096) > 0) {
            buffer.append("P").append(Colors.ANSI_RESET).append(Colors.ANSI_GREEN);
            if ((db & 8192) > 0) buffer.append(dataInputStream.readUTF());
            else buffer.append(dataInputStream.readInt());
            buffer.append(Colors.ANSI_RESET);
        } return buffer.toString();
    }
}
