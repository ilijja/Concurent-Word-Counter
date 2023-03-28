package app;

import crawler.Crawler;
import crawler.DirectoryCrawler;
import job.*;

public class TaskManager {

    private Crawler crawler;
    private JobQueue jobQueue;
    private JobDisparcher jobDisparcher;

    public TaskManager() {
        this.jobQueue = new ScanningJobQueue();
        this.crawler = new DirectoryCrawler(jobQueue);
        this.jobDisparcher = new JobDisparcher(jobQueue);
        this.startJobDispatcher();
    }

    public void addPath(String path){
        crawler.addPath(path);
    }

    public void addWeb(String url){
        this.jobQueue.enqueue(new Job(url, ScanType.WEB));
    }

    public void startJobDispatcher(){
        Thread thread = new Thread(jobDisparcher);
        thread.start();
    }

}
