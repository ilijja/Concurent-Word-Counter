package scanner.web;

import app.Properties;
import job.Job;
import job.JobQueue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import scanner.Scanner;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WebScanner implements Scanner {


    private final ExecutorService pool;
    private JobQueue jobQueue;


    public WebScanner(JobQueue jobQueue) {
        this.pool = Executors.newCachedThreadPool();
        this.jobQueue = jobQueue;
    }

    @Override
    public void submitTask(Job job) {
        scanJobPath(job);
    }

    private void scanJobPath(Job job) {
        Future<Map<String, Map<String, Integer>>> future = this.pool.submit(new WebProcessingTask(job, jobQueue));

        try {
            System.out.println(future.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

    }}
