package scanner.file;

import app.Properties;
import job.Job;
import job.ScanType;
import result.Result;
import result.ResultRetriever;
import scanner.Scanner;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class FileScanner implements Scanner {

    private final long FILE_SCANNING_SIZE_LIMIT;
    private final AtomicLong size;
    private ForkJoinPool pool;
    private Map<String, Job> jobs;
    List<File> filesToProcess;
    ResultRetriever resultRetriever;

    public FileScanner(ResultRetriever resultRetriever) {
        this.size = new AtomicLong(0);
        this.pool = new ForkJoinPool();
        this.jobs = new ConcurrentHashMap<>();
        this.FILE_SCANNING_SIZE_LIMIT = Long.valueOf(Properties.FILE_SCANNING_SIZE_LIMIT.get());
        this.filesToProcess = new CopyOnWriteArrayList<>();
        this.resultRetriever = resultRetriever;
    }

    //ad /Users/ilija/Desktop/Word-Counter/Word-Counter/test/example
//get file|corpus_mcfly
//query file|corpus_mcfly
//get file|corpus_riker
//query file|corpus_mcfly
//query file|summary

    @Override
    public void submitTask(Job job) {
        this.processCorpus(job);
    }

    @Override
    public void stop() {
        pool.shutdown();
    }

    private void processCorpus(Job job) {

        if (job.getScanType() == ScanType.POISON) {
            stop();
            return;
        }

        File corpus = new File(job.getPath());

        System.out.println("Starting file scan for file|" + corpus.getName());

        jobs.put(job.getPath(), job);

        long currentSize;

        List<FileProcessingTask> tasks = new ArrayList<>();

        for (File file : corpus.listFiles()) {

            resultRetriever.addResult(new Result(file.getPath(), null, ScanType.FILE));

            currentSize = size.addAndGet(file.length());
            filesToProcess.add(file);

            if (currentSize >= this.FILE_SCANNING_SIZE_LIMIT) {
                FileProcessingTask task = new FileProcessingTask(filesToProcess);
                tasks.add(task);
                filesToProcess = new ArrayList<>();
                size.set(0);
            }
        }

        if (!filesToProcess.isEmpty()) {
            FileProcessingTask task = new FileProcessingTask(filesToProcess);
            tasks.add(task);
        }

        finishCorpusProcessing(tasks);
    }

    private void finishCorpusProcessing(List<FileProcessingTask> tasks) {
        List<Map<String, Map<String, Integer>>> results = new ArrayList<>();

        for (FileProcessingTask task : tasks) {
            results.addAll(pool.invoke(task));
        }

        mergeResults(results);
    }

    private void mergeResults(List<Map<String, Map<String, Integer>>> result) {

        for (Map<String, Map<String, Integer>> set : result) {
            for (String key : set.keySet()) {
                Map<String, Integer> counts = set.get(key);
                resultRetriever.setResult(key, counts);
            }
        }
    }
}
