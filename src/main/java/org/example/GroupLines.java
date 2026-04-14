package org.example;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupLines {
    static class DSU {
        private final int n;
        private final int[] parent;
        private final int[] size;
        DSU(int n) {
            this.n = n;
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }
        int find(int x) {
            int root = x;
            while (parent[root] != root) {
                root = parent[root];
            }

            while (x != root) {
                int next = parent[x];
                parent[x] = root;
                x = next;
            }
            return root;
        }
        void union(int a, int b) {
            int ra = find(a), rb = find(b);
            if (ra == rb) return;
            if (size[ra] < size[rb]) {
                int t = ra; ra = rb; rb = t;
            }
            parent[rb] = ra;
            size[ra] += size[rb];
        }

        int getNum() {return n;}

        int getGroupSize(int x) {
            return size[find(x)];
        }

        int getSizeByParent(int p) {
            return size[p];
        }
    }

    private static final Pattern VALID_LINE_PATTERN = Pattern.compile("^((\"[^;\"]*\")|([^;\"]*)($|;))*$");

    public static boolean isValidLine(String line) {
        Matcher matcher = VALID_LINE_PATTERN.matcher(line);
        return matcher.matches();
    }

    private static List<String> getValidLines(String filePath) {
        List<String> validLines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (isValidLine(line)) {
                    validLines.add(line);
                }
            }
        }
        catch (IOException e) {
            System.err.println("Failed to read file");
            return null;
        }
        return validLines;
    }

    public static DSU buildDSU(List<String> validLines) {
        if (validLines == null || validLines.isEmpty()) return null;
        int n = validLines.size();
        DSU dsu = new DSU(n);
        List<Map<String, Integer>> columnMap = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String[] row = validLines.get(i).split(";", -1);
            for (int col = 0; col < row.length; col++) {
                if (columnMap.size() <= col) columnMap.add(new HashMap<>());
                String val = row[col].trim();
                if (val.isEmpty() || val.equals("\"\"")) continue;
                Map<String, Integer> map = columnMap.get(col);
                if (map.containsKey(val)) {
                    dsu.union(i, map.get(val));
                } else {
                    map.put(val, i);
                }
            }
        }
        return dsu;
    }

    public static int[] getLinesOrder(DSU dsu) {
        if (dsu == null) return null;
        int groupsCount = 0, maxSize = 0, n = dsu.getNum(), size, i;
        // вычислить количество групп и размер наибольшей группы
        for (i=0; i<n; i++) {
            if (dsu.find(i) == i) {
                groupsCount++;
                size = dsu.getSizeByParent(i);
                if (maxSize < size) maxSize = size;
            }
        }
        maxSize++;
        int[] countBySize = new int[maxSize];
        int[] parents = new int[groupsCount];
        int parentCount = 0;
        // записать массив представителей, записать количество групп каждого размера
        for (i=0; i<n; i++) {
            if (dsu.find(i) == i) {
                parents[parentCount] = i;
                parentCount++;
                countBySize[dsu.getSizeByParent(i)]++;
            }
        }
        int[] positionBySize = new int[maxSize];
        int posSum = 0;
        // вычислить стартовую позицию для групп определенного размера
        for (i=maxSize-1; i>=0; i--) {
            positionBySize[i] = posSum;
            posSum += countBySize[i] * i;
        }
        countBySize = null;
        int[] parentPosition = new int[n];
        // вычислить стартовую позицию каждой группы
        for (i=0; i<groupsCount; i++) {
            size = dsu.getSizeByParent(parents[i]);
            parentPosition[parents[i]] = positionBySize[size];
            positionBySize[size] += size;
        }
        parents = null;
        positionBySize = null;
        int[] result = new int[n];
        int parent, pos;
        // записать каждую строку на свое место в массиве
        for (i=0; i<n; i++) {
            parent = dsu.find(i);
            pos = parentPosition[parent];
            result[pos] = i;
            parentPosition[parent]++;
        }
        return result;
    }

    private static int getBigGroupsCount(DSU dsu, int[] linesOrder) {
        if (dsu == null || linesOrder == null) return 0;
        int bigGroupsCount = 0;
        int i=0;
        while (i< dsu.getNum()) {
            int size = dsu.getGroupSize(linesOrder[i]);
            if (size > 1) bigGroupsCount++;
            else break;
            i += size;
        }
        return bigGroupsCount;
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        if (args.length < 1) {
            System.err.println("Необходимо указать путь к файлу:\njava -jar {название проекта}.jar {Полный путь к входному файлу}");
            System.exit(1);
        }
        String filePath = args[0];

        List<String> validLines = getValidLines(filePath);

        DSU dsu = buildDSU(validLines);

        int[] linesOrder = getLinesOrder(dsu);

        int bigGroupsNum = getBigGroupsCount(dsu, linesOrder);

        System.out.println("Количество групп с более чем одним элементом: " + bigGroupsNum);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
            writer.write(String.valueOf(bigGroupsNum));
            writer.newLine();
            if (dsu != null) {
                int groupNumber = 1;
                int currentParent = -1;
                for (int i = 0; i < dsu.getNum(); i++) {
                    if (currentParent != dsu.find(linesOrder[i])) {
                        writer.newLine();
                        writer.write("Группа ");
                        writer.write(String.valueOf(groupNumber++));
                        writer.newLine();
                    }
                    currentParent = dsu.find(linesOrder[i]);
                    writer.write(validLines.get(linesOrder[i]));
                    writer.newLine();
                }
            }
        }
        catch (IOException e) {
            System.err.println("Failed to write output to file");
        }

        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println("Время выполнения: " + elapsed + " ms");
    }
}
