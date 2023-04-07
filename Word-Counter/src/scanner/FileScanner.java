package scanner;

import app.Properties;
import job.Job;

import java.io.File;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;

public class FileScanner implements Scanner{

    private long FILE_SCANNING_SIZE_LIMIT;
    private AtomicLong size;
    private BlockingQueue<Job> jobs;

    private final ExecutorService pool;

    public FileScanner() {
        this.size = new AtomicLong(0);
        this.pool = Executors.newCachedThreadPool();
        this.jobs = new LinkedBlockingDeque<>();
        this.FILE_SCANNING_SIZE_LIMIT = Long.valueOf(Properties.FILE_SCANNING_SIZE_LIMIT.get());
    }

    @Override
    public void submitTask(Job job) {
        this.validate(job);
    }

    private void startScanning(){

        try {
            while (!this.jobs.isEmpty()){
                Job job = this.jobs.take();
                pool.submit(() -> {
                    System.out.println(job.getPath());
                });
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private void validate(Job job){

        File file = new File(job.getPath());

        System.out.println(Thread.currentThread().threadId());

        long currentSize = this.size.addAndGet(file.length());
        this.jobs.add(job);

        if(currentSize >= this.FILE_SCANNING_SIZE_LIMIT){
            this.startScanning();
            this.size.set(0);
        }

    }





}
