package job;

import java.util.Map;
import java.util.concurrent.Future;

public class Job {

    private String path;
    private ScanType scanType;
    private Map<String,Integer> result;

    private boolean scanned;

    public Job(String path, ScanType scanType) {
        this.path = path;
        this.scanType = scanType;
    }

    public Job(String path, ScanType scanType, boolean scanned) {
        this.path = path;
        this.scanType = scanType;
        this.scanned = scanned;
    }

    public String getPath() {
        return path;
    }

    public ScanType getScanType() {
        return scanType;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setScanType(ScanType scanType) {
        this.scanType = scanType;
    }

    public Map<String, Integer> getResult() {
        return result;
    }

    public void setResult(Map<String, Integer> result) {

        if (this.result == null) {
            this.result = result;
            return;
        }

        for (String key : result.keySet()) {
            Integer currentValue = this.result.get(key);
            Integer valueToAdd = result.get(key);

            if (currentValue != null) {
                this.result.put(key, currentValue + valueToAdd);
            } else {
                this.result.put(key, valueToAdd);
            }
        }

    }

    public boolean isScanned() {
        return scanned;
    }

    public void setScanned(boolean scanned) {
        this.scanned = scanned;
    }
}
