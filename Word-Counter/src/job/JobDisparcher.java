package job;

import scanner.Scanner;

import java.util.Map;

public class JobDisparcher implements Runnable{

    private JobQueue jobs;
    private Map<ScanType, Scanner> scanners;

    public JobDisparcher(JobQueue jobs) {
        this.jobs = jobs;
    }

    @Override
    public void run() {

        while(true) {

            Job job = jobs.dequeue();
            ScanType scanType = job.getScanType();

            scanners.get(scanType).scanJob(job);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
