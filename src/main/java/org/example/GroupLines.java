package org.example;

import java.io.*;
import java.util.*;

public class GroupLines {
    static class DSU {
        int[] parent, size;
        DSU(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }
        int find(int x) {
            while (parent[x] != x) {
                parent[x] = parent[parent[x]];
                x = parent[x];
            }
            return x;
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
    }

    public static boolean isValidLine(String line) {
        return line.matches("^((^|;)(\"[^;\"]*\")|([^;\"]*)($|;))*$");
    }

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        if (args.length < 1) {
            System.err.println("Необходимо указать путь к файлу:\njava -jar {название проекта}.jar {Полный путь к входному файлу}");
            System.exit(1);
        }
        String filePath = args[0];
        List<String> validLines = new ArrayList<>();
        List<String[]> parsed = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (isValidLine(line)) {
                    String[] parts = line.split(";", -1);
                    validLines.add(line);
                    parsed.add(parts);
                }
            }
        }

        int n = parsed.size();
        DSU dsu = new DSU(n);
        List<Map<String, Integer>> columnMap = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String[] row = parsed.get(i);
            for (int col = 0; col < row.length; col++) {
                if (columnMap.size() <= col) columnMap.add(new HashMap<>());
                String val = row[col].trim();
                if (val.isEmpty()) continue;
                Map<String, Integer> map = columnMap.get(col);
                if (map.containsKey(val)) {
                    dsu.union(i, map.get(val));
                } else {
                    map.put(val, i);
                }
            }
        }

        Map<Integer, List<Integer>> groups = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int root = dsu.find(i);
            groups.computeIfAbsent(root, k -> new ArrayList<>()).add(i);
        }

        List<List<Integer>> groupList = new ArrayList<>();
        for (List<Integer> group : groups.values()) {
            if (group.size() > 1) groupList.add(group);
        }
        groupList.sort((a, b) -> Integer.compare(b.size(), a.size()));

        System.out.println("Количество групп с более чем одним элементом: " + groupList.size());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
            writer.write(String.valueOf(groupList.size()));
            writer.newLine();
            writer.newLine();
            int groupNumber = 1;
            for (List<Integer> group : groupList) {
                writer.write("Группа ");
                writer.write(String.valueOf(groupNumber++));
                writer.newLine();
                for (int idx : group) {
                    writer.write(validLines.get(idx));
                    writer.newLine();
                }
                writer.newLine();
            }
        }
        catch (IOException e) {
            System.err.println("Failed to write output to file");
        }

        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println("Время выполнения: " + elapsed + " ms");
    }
}
