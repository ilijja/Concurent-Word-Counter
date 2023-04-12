package job;

import scanner.file.FileScanner;
import scanner.Scanner;
import scanner.web.WebScanner;

import java.util.HashMap;
import java.util.Map;

public class JobDisparcher implements Runnable{

    private JobQueue jobs;
    private Map<ScanType, Scanner> scanners;


    public JobDisparcher(JobQueue jobs) {
        this.jobs = jobs;

        scanners = new HashMap<>();

        scanners.put(ScanType.FILE, new FileScanner());
        scanners.put(ScanType.WEB, new WebScanner(jobs));

    }

    @Override
    public void run() {

        while(true) {

            Job job = jobs.dequeue();
            scanners.get(job.getScanType()).submitTask(job);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


}


