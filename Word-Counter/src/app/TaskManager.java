package app;

import crawler.Crawler;
import crawler.DirectoryCrawler;
import crawler.WebCrawler;
import job.*;

import java.util.HashMap;
import java.util.Map;

public class TaskManager {

    private Crawler crawler;
    private JobQueue jobQueue;
    private JobDisparcher jobDisparcher;

    private Map<ScanType, Crawler> crawlers;

    public TaskManager() {
        this.crawlers = new HashMap<>();

        this.jobQueue = new ScanningJobQueue();

        this.crawlers.put(ScanType.FILE, new DirectoryCrawler(jobQueue));
        this.crawlers.put(ScanType.WEB, new WebCrawler(jobQueue));
        this.jobDisparcher = new JobDisparcher(jobQueue);

        this.startJobDispatcher();
    }

    public void addPath(String path){
        crawlers.get(ScanType.FILE).addPath(path);
    }

    public void addWeb(String url){
        crawlers.get(ScanType.WEB).addPath(url);
    }

    public void startJobDispatcher(){
        Thread thread = new Thread(jobDisparcher);
        thread.start();
    }

}
