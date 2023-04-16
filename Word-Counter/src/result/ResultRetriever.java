package result;

import job.Job;
import job.ScanType;

import java.util.Map;
import java.util.concurrent.Future;

public interface ResultRetriever {

    void executeQuery(ResultType resultType, ScanType scanType, String name);

    void addResult(Result result);

    void setResult(String path, Map<String, Integer> counts);

    void stop();

    void clearSummary(ScanType scanType);


}
