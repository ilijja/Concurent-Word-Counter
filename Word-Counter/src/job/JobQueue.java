package job;

public interface JobQueue {

    void enqueue(Job job);
    Job dequeue();

}
