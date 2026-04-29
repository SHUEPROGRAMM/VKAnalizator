package org.example.Clients;

import org.example.General;

import java.util.ArrayList;
import java.util.TreeSet;

public class ScanClasses {
    public static abstract class ScanBase extends Thread {
        public final ArrayList<Integer> ids;
        public final ArrayList<Integer> vkTokens;
        public final int rertyCount;
        public final long rertyTime;
        public long backTime = -99999999;

        public ScanBase(ArrayList<Integer> ids, ArrayList<Integer> vkTokens, int rertyCount, long rertyTime) {
            this.ids = ids;
            this.vkTokens = vkTokens;
            this.rertyTime = rertyTime;
            this.rertyCount = rertyCount;
        }

        protected ArrayList<Integer> scanBase(ArrayList<Integer> data) { return null; }

        protected void removeVKTokens(ArrayList<Integer> data) {
            if (data.isEmpty()) return;
            vkTokens.removeAll(data);
            General.vkTokens.remove(data);
        }

        protected ArrayList<Integer>[] getDivide(ArrayList<Integer> data, int thread) {
            thread = Math.min(thread, data.size());
            int count = data.size() / thread;
            int fix = data.size() % thread;

            ArrayList<Integer>[] buffer = new ArrayList[thread];
            int begin = 0, end = 0;

            //Откладочка
            //System.out.println(count + ":" + fix + ":" + thread + ":" + data.size());

            for (int index = 0; index < thread; ++index) {
                end += count;
                if (index < fix) ++end;
                buffer[index] = new ArrayList<>(data.subList(begin, end));
                begin = end;
            } return buffer;
        }

        protected ArrayList<Integer>[] getDivide(ArrayList<Integer> data, int count, int thread) {
            int arraysCount = data.size() / count;
            if (data.size() % count != 0) ++arraysCount;
            thread = Math.min(thread, arraysCount);
            int fix = arraysCount % thread;
            int sizeCount = arraysCount / thread;

            ArrayList<Integer>[] buffer = new ArrayList[thread];
            int begin = 0, end = 0;

            for (int index = 0; index < thread; ++index) {
                end += sizeCount * 1000;
                if (index < fix) end += 1000;
                buffer[index] = new ArrayList<>(data.subList(begin, Math.min(end, data.size())));
                begin = end;
            } return buffer;
        }

        protected boolean scanTwo() throws InterruptedException {
            ArrayList<Integer> scan = new ArrayList<>(ids);
            while (true) {
                if (vkTokens.isEmpty()) return false;
                scan = scanBase(scan);
                if (scan.isEmpty()) return true;
            }
        }

        public void scan() throws InterruptedException {
            for (int index = 0; index < rertyCount; ++index) {
                long time = General.runtimeMXBean.getStartTime() - backTime;
                if (time < rertyTime) Thread.sleep(rertyTime - time);
                scanTwo();
            }
        }

        @Override
        public void run() {
            try { scan(); }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        protected abstract ScanLevel.ScanBaseDB scanBase();
    }

    public static class ScanLevel extends ScanBase {
        protected static class ScanBaseDB {
            public ArrayList<Integer> nonScan;
            public TreeSet<Integer> newScan;

            public ScanBaseDB(ArrayList<Integer> nonScan, TreeSet<Integer> newScan) {
                this.nonScan = nonScan;
                this.newScan = newScan;
            }
        }

        public final int level;
        public final ArrayList<Integer> newScan = new ArrayList<>();

        public ScanLevel(ArrayList<Integer> ids, ArrayList<Integer> vkTokens, int rertyCount, long rertyTime, int level) {
            super(ids, vkTokens, rertyCount, rertyTime);
            this.level = level;
        }

        @Override
        protected ScanBaseDB scanBase() { return null; }

        protected void scanBaseDB(ScanBaseDB data) throws InterruptedException {  }

        @Override
        protected boolean scanTwo() throws InterruptedException {
            ScanBaseDB buffer = new ScanBaseDB(ids, new TreeSet<>());
            TreeSet<Integer> scanned = new TreeSet<>();
            int level = 0;

            while (true) {
                if (level == this.level) return true;
                if (vkTokens.isEmpty()) return false;
                System.out.println("level: " + level);

                scanned.addAll(buffer.nonScan);
                scanBaseDB(buffer);
                buffer.nonScan.forEach(scanned::remove);

                if (buffer.nonScan.isEmpty()) {
                    if (buffer.newScan.isEmpty()) return false;
                    buffer.nonScan = new ArrayList<>(buffer.newScan);
                    buffer.newScan.clear();
                    ++level;
                }
            }
        }
    }

    public static class ScanFriends extends ScanLevel {
        public ScanFriends(ArrayList<Integer> ids, ArrayList<Integer> vkTokens, int rertyCount, long rertyTime, int level) {
            super(ids, vkTokens, rertyCount, rertyTime, level);
        }

        @Override
        protected void scanBaseDB(ScanBaseDB data) throws InterruptedException {
            ArrayList<Integer>[] buffer = getDivide(data.nonScan, vkTokens.size());
            ScanThreads.FriendsGetThread[] friendsGetThreads = new ScanThreads.FriendsGetThread[buffer.length];
            ArrayList<Integer> remove = new ArrayList<>();
            data.nonScan.clear();

            for (int index = 0; index < friendsGetThreads.length; ++index) {
                VKToken vkToken;
                synchronized (General.vkTokens) { vkToken = General.vkTokens.data.get(vkTokens.get(index)); }

                if (vkToken != null) {
                    friendsGetThreads[index] = new ScanThreads.FriendsGetThread(vkToken, buffer[index]);
                    friendsGetThreads[index].start();
                } else {
                    remove.add(vkTokens.get(index));
                    data.nonScan.addAll(buffer[index]);
                }
            }

            for (ScanThreads.FriendsGetThread element : friendsGetThreads) {
                if (element != null) {
                    element.join();
                    if (element.error) {
                        data.nonScan.addAll(element.ids);
                        remove.add(element.vkToken.id);
                    } data.newScan.addAll(element.newScan);
                }
            } removeVKTokens(remove);
        }
    }
}
