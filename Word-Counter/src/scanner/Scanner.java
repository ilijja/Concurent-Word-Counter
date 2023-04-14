package scanner;

import job.Job;

public interface Scanner {

    void submitTask(Job job);

    void stop();

}
