package crawler;

import java.util.concurrent.atomic.AtomicBoolean;
import job.Job;
import job.JobQueue;
import job.ScanType;

public class WebCrawler implements Crawler, Runnable {

    private JobQueue jobQueue;
    private String url;
    private Thread thread;
    private AtomicBoolean stopRequested;

    public WebCrawler(JobQueue jobQueue) {
        this.jobQueue = jobQueue;
        this.stopRequested = new AtomicBoolean(false);
    }

    @Override
    public void addPath(String url) {
        this.url = url;
        this.thread = new Thread(this);
        this.thread.start();
    }

    @Override
    public void stop() {
        stopRequested.set(true);
    }

    @Override
    public void run() {
        if (!stopRequested.get()) {
            this.jobQueue.enqueue(new Job(url, ScanType.WEB));
        }
    }

}
