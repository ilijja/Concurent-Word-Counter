package job;

public class JobDisparcher implements Runnable{

    private JobQueue jobs;

    public JobDisparcher(JobQueue jobs) {
        this.jobs = jobs;
    }

    @Override
    public void run() {

        while(true) {

            Job job = jobs.dequeue();
            ScanType scanType = job.getScanType();
            System.out.println(job.getPath());

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
