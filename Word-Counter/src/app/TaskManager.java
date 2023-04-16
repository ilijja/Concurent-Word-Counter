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

public class TaskManager {

    private JobQueue jobQueue;
    private JobDisparcher jobDisparcher;

    private Map<ScanType, Crawler> crawlers;

    private ResultRetriever resultRetriever;

    public TaskManager() {
        this.crawlers = new HashMap<>();

        this.jobQueue = new ScanningJobQueue();

        this.crawlers.put(ScanType.WEB, new WebCrawler(jobQueue));
        this.crawlers.put(ScanType.FILE, new DirectoryCrawler(jobQueue));

        this.resultRetriever = new ResultRetrieverPool();
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

    public void clearSummary(ScanType scanType){
        resultRetriever.clearSummary(scanType);
    }

    public void startJobDispatcher(){
        Thread thread = new Thread(jobDisparcher);
        thread.start();
    }

    public void stopThreads(){

        System.out.println("Stopping threads");

        resultRetriever.stop();
        for(Crawler crawler:crawlers.values()){
            crawler.stop();
        }
        jobQueue.enqueue(new Job());

    }



}
