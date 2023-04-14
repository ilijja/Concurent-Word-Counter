package result;

import job.ScanType;

import java.util.Map;

public class Result {

    private String path;
    private Map<String, Integer> counts;
    private ScanType scanType;


    public Result(String path, Map<String, Integer> counts, ScanType scanType) {
        this.path = path;
        this.counts = counts;
        this.scanType = scanType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, Integer> getCounts() {
        return counts;
    }

    public void setCounts(Map<String, Integer> counts) {
        this.counts = counts;
    }

    public ScanType getScanType() {
        return scanType;
    }

    public boolean isDone(){
        return counts != null;
    }
}
