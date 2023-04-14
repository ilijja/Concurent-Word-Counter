package scanner.web;

import job.Job;
import job.JobQueue;
import result.ResultRetriever;
import scanner.Scanner;


import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WebScanner implements Scanner {


    private final ExecutorService pool;
    private JobQueue jobQueue;

    private ResultRetriever resultRetriever;

    public WebScanner(JobQueue jobQueue, ResultRetriever resultRetriever) {
        this.pool = Executors.newCachedThreadPool();
        this.jobQueue = jobQueue;
        this.resultRetriever = resultRetriever;
    }

    @Override
    public void submitTask(Job job) {
        scanJobPath(job);
    }

    @Override
    public void stop() {
        pool.shutdown();
    }

    private void scanJobPath(Job job) {


        Future<Map<String, Integer>> future = this.pool.submit(new WebProcessingTask(job, jobQueue, resultRetriever));

        try {
            resultRetriever.setResult(job.getPath(), future.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

    }}
