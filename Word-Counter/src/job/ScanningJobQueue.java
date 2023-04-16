package job;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class ScanningJobQueue implements JobQueue{

    private BlockingQueue<Job> jobs;

    public ScanningJobQueue() {
        this.jobs = new LinkedBlockingDeque<>();
    }

    @Override
    public void enqueue(Job job) {
        try {
            this.jobs.put(job);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Job dequeue() {
        try {
            return this.jobs.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
