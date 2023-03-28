package app;

import crawler.Crawler;
import crawler.DirectoryCrawler;

public class TaskManager {

    private Crawler crawler;

    public TaskManager() {
        this.crawler = new DirectoryCrawler();
    }

    public void addPath(String path){
        crawler.addPath(path);
    }

}
