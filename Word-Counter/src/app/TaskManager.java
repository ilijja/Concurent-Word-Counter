package app;

import crawler.Crawler;
import crawler.DirectoryCrawler;
import crawler.WebCrawler;
import job.*;
import result.ResultRetriever;
import result.ResultRetrieverPool;
import result.ResultType;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TaskManager {

    private Crawler crawler;
    private JobQueue jobQueue;
    private JobDisparcher jobDisparcher;

    private Map<ScanType, Crawler> crawlers;

    private ResultRetriever resultRetriever;

    public TaskManager() {
        this.crawlers = new HashMap<>();

        this.jobQueue = new ScanningJobQueue();
        this.resultRetriever = new ResultRetrieverPool();

        this.crawlers.put(ScanType.FILE, new DirectoryCrawler(jobQueue));
        this.crawlers.put(ScanType.WEB, new WebCrawler(jobQueue));
        this.jobDisparcher = new JobDisparcher(jobQueue, resultRetriever);


        this.startJobDispatcher();
    }

    public void addPath(String path){
        crawlers.get(ScanType.FILE).addPath(path);
    }

    public void addWeb(String url){
        crawlers.get(ScanType.WEB).addPath(url);
    }

    public void getResult(String param){
        String[] params = param.split("\\|");
        resultRetriever.executeQuery(ResultType.GET, ScanType.valueOf(params[0].toUpperCase()), params[1]);
    }

    public void queryResult(String param){
        String[] params = param.split("\\|");
        resultRetriever.executeQuery(ResultType.QUERY, ScanType.valueOf(params[0].toUpperCase()), params[1]);
    }

    public void startJobDispatcher(){
        Thread thread = new Thread(jobDisparcher);
        thread.start();
    }


}
