package crawler;

import job.ScanType;

public interface Crawler {

    void addPath(String path);
    void stop();

}
