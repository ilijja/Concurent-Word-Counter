package scanner.file;

import app.Properties;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.RecursiveTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileProcessingTask extends RecursiveTask<List<Map<String, Map<String, Integer>>>> {

    private List<File> files;

    public FileProcessingTask(List<File> files) {
        this.files = files;
    }

    @Override
    protected List<Map<String, Map<String, Integer>>> compute() {
        List<Map<String, Map<String, Integer>>> results = new CopyOnWriteArrayList<>();

        for (File file : this.files) {
            Map<String, Map<String, Integer>> fileResult = new ConcurrentHashMap<>();
            Map<String, Integer> counts = new ConcurrentHashMap<>();

            fileResult.put(file.getAbsolutePath(), countWords(counts, file));
            results.add(fileResult);
        }

        return results;
    }

    public static String readFileToString(File file) {
        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
            return new String(fileBytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public Map<String, Integer> countWords(Map<String, Integer> counts, File file) {
        String[] keywords = Properties.KEYWORDS.get().split(",");

        for (String keyword : keywords) {
            counts.put(keyword, countOccurrences(readFileToString(file), keyword));
        }

        return counts;
    }

    public static int countOccurrences(String text, String word) {
        int count = 0;
        Pattern pattern = Pattern.compile("\\b" + Pattern.quote(word) + "\\b");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            count++;
        }

        return count;
    }
}
