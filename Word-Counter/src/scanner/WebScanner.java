package scanner;

import job.Job;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebScanner implements Scanner{


    private final ExecutorService pool;


    public WebScanner() {
        this.pool = Executors.newCachedThreadPool();
    }

    @Override
    public void submitTask(Job job) {
        pool.submit(() -> {
            // Implementacija obrade FileScanner posla
            System.out.println("WebScanner: " + job.getPath());
        });
    }



}
