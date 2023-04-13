package result;

import job.ScanType;

public interface ResultRetriever {

    void executeQuery(ResultType resultType, ScanType scanType, String name);

    void addResult(Result result);



}
