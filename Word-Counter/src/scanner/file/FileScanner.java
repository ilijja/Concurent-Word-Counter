package scanner.file;

import app.Properties;
import job.Job;
import scanner.Scanner;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class FileScanner implements Scanner {

    private long FILE_SCANNING_SIZE_LIMIT;
    private AtomicLong size;

    private final ExecutorService pool;

    private Map<String, Job> jobs;

    public FileScanner() {
        this.size = new AtomicLong(0);
        this.pool = Executors.newCachedThreadPool();
        this.jobs = new ConcurrentHashMap<>();
        this.FILE_SCANNING_SIZE_LIMIT = Long.valueOf(Properties.FILE_SCANNING_SIZE_LIMIT.get());
    }

    @Override
    public void submitTask(Job job) {
        this.processCorpus(job);
    }


    private void processCorpus(Job job) {
        File corpus = new File(job.getPath());

        jobs.put(job.getPath(), job);


        List<File> filesToProcess = new ArrayList<>();

        long currentSize;

        List<Future<List<Map<String, Map<String, Integer>>>>> futures = new ArrayList<>();

        for (File file : corpus.listFiles()) {

            currentSize = size.addAndGet(file.length());
            filesToProcess.add(file);

            if (currentSize >= this.FILE_SCANNING_SIZE_LIMIT) {

                Future<List<Map<String, Map<String, Integer>>>> future = pool.submit(new FileProcessingTask(filesToProcess));
                futures.add(future);
                filesToProcess = new ArrayList<>();
                size.set(0);
            }
        }


        finishCorpusProcessing(corpus.getName(), futures);

    }


    private void finishCorpusProcessing(String corpusName, List<Future<List<Map<String, Map<String, Integer>>>>> futures) {

        Map<String, Integer> corpusResults = new HashMap<>();

        for (Future<List<Map<String, Map<String, Integer>>>> future : futures) {
            try {
                List<Map<String, Map<String, Integer>>> result = future.get();
                mergeResults(result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // Ovde možete vratiti rezultate ili ih čuvati za kasniju upotrebu
//        System.out.println("Finished processing corpus: " + corpusName);
//        System.out.println("Results: " + corpusResults);
    }

    private void mergeResults(List<Map<String, Map<String, Integer>>> result) {

        for (Map<String, Map<String, Integer>> set : result) {
            for (String key : set.keySet()) {
                File file = new File(key);
                Map<String, Integer> value = set.get(key);

                jobs.get(file.getParentFile().getAbsolutePath()).setResult(value);

//                System.out.println(jobs.get(file.getParentFile().getAbsolutePath()).getResult());
            }
        }



    }



}
