package result;

import job.ScanType;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ResultRetrieverPool implements ResultRetriever{

    private List<Result> results;
    private ExecutorService pool;


    public ResultRetrieverPool() {
        this.results = new CopyOnWriteArrayList<>();
        this.pool = Executors.newCachedThreadPool();
    }

    @Override
    public void executeQuery(ResultType resultType, ScanType scanType, String name) {

        switch (resultType){
            case GET:
                executeGet(scanType, name);
                break;
            case QUERY:
                executeQuery(scanType, name);
                break;
            default:
                break;

        }
    }

    @Override
    public void addResult(Result result) {
        results.remove(result);
        results.add(result);
    }

    private void executeGet(ScanType scanType, String name){


        Future<Map<String, Integer>> futureResult = pool.submit(() -> {
             return sumCountsForCorpus(scanType, name);
        });

        try {
            Map<String,Integer> result = futureResult.get();
            System.out.println(result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


    }

    private void executeQuery(ScanType scanType, String name){

    }

//ad /Users/ilija/Desktop/Word-Counter/Word-Counter/test/example
//get file|corpus_mcfly
//get file|corpus_riker

    private Map<String, Integer> sumCountsForCorpus(ScanType scanType, String corpusName) {
        Map<String, Integer> totalCounts = new HashMap<>();
        int processedFiles = 0;
        File corpus = null;

        while (true) {
            for (Result result : this.results) {
                File file = new File(result.getPath());
                if (result.getScanType().equals(scanType) && file.getParentFile().getName().equals(corpusName)) {
                    if (corpus == null) {
                        corpus = new File(file.getParentFile().getAbsolutePath());
                    }
                    result.getCounts().forEach((key, value) -> totalCounts.merge(key, value, Integer::sum));
                    processedFiles++;
                }
            }

            if (corpus != null) {
                int totalFiles = (corpus.listFiles() != null && corpus.isDirectory()) ? corpus.listFiles().length : 0;

                if (processedFiles == totalFiles) {
                    break;
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        return totalCounts;
    }




}
