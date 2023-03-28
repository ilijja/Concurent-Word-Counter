package job;

public class Job {

    private String path;
    private ScanType scanType;

    public Job(String path, ScanType scanType) {
        this.path = path;
        this.scanType = scanType;
    }

    public String getPath() {
        return path;
    }

    public ScanType getScanType() {
        return scanType;
    }


}
