package crawler;

import job.Job;
import job.JobQueue;
import job.ScanType;

public class WebCrawler implements  Crawler, Runnable{

    private JobQueue jobQueue;
    private String url;

    public WebCrawler(JobQueue jobQueue) {
        this.jobQueue = jobQueue;
    }

    @Override
    public void addPath(String url) {
        this.url = url;
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void stop() {

    }

    @Override
    public void run() {
        this.jobQueue.enqueue(new Job(url, ScanType.WEB));
    }


}
