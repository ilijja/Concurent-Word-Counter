package result;

import app.Assets;
import job.ScanType;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ResultRetrieverPool implements ResultRetriever{

    private List<Result> results;
    private ExecutorService pool;




    public ResultRetrieverPool() {
        this.results = new CopyOnWriteArrayList<>();
        this.pool = Executors.newCachedThreadPool();
    }

    @Override
    public void executeQuery(ResultType resultType, ScanType scanType, String name) {

        switch (resultType) {
            case GET -> executeGet(scanType, name);
            case QUERY -> executeQuery(scanType, name);
            default -> System.out.println("Wrong command");
        }
    }

    @Override
    public void addResult(Result result) {
        results.remove(result);
        results.add(result);
    }

    @Override
    public void setResult(String path, Map<String, Integer> counts){
        for(Result result: results){
            if(result.getPath().equals(path)){
                result.setCounts(counts);
            }
        }
    }



    @Override
    public void stop() {
        pool.shutdown();
    }

    @Override
    public void clearSummary(ScanType scanType) {
        System.out.println("Clearing " +  scanType + " summary");
        results = results.stream()
                .filter(item -> item.getScanType() != scanType)
                .collect(Collectors.toList());
    }

    private void executeGet(ScanType scanType, String name){

        if(name.equals(Assets.SUMMARY)){
            Future<Map<String, Map<String, Integer>>> futureResult = pool.submit(() -> {

                if(scanType.equals(ScanType.FILE)){
                    return this.summaryFileAsync();
                }

                if(scanType.equals(ScanType.WEB)){
                    return this.summaryWebAsync();
                }

                return null;
            });

            try {
                Map<String, Map<String, Integer>> result = futureResult.get();
                System.out.println(result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }else{
            Future<Map<String, Integer>> futureResult = pool.submit(() -> {
                if(scanType.equals(ScanType.FILE)){
                    return sumCountsForCorpus(name);
                }

                if(scanType.equals(ScanType.WEB)){
                    return sumCountsForWeb(name);
                }

                return null;
            });

            try {
                Map<String,Integer> result = futureResult.get();
                System.out.println(result + " " + name);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }


    }

    private void executeQuery(ScanType scanType, String name){


        if(name.equals(Assets.SUMMARY)){
            Future<Map<String, Map<String, Integer>>> futureResult = pool.submit(() -> {

                if(scanType.equals(ScanType.FILE)){
                    return summaryFileSync();
                }

                if(scanType.equals(ScanType.WEB)){
                    return summaryWebSync();
                }

                return null;

            });

            try {
                Map<String,Map<String,Integer>> result = futureResult.get();
                if(result==null){
                    System.out.println("Summary is not ready yet");
                }else{
                    System.out.println(result);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }


        }else {
            Future<Map<String, Integer>> futureResult = pool.submit(() -> {
                if(scanType.equals(ScanType.FILE)){
                    return getSyncCorpusCounts(name);
                }

                if(scanType.equals(ScanType.WEB)){
                    return getSyncWebCounts(name);
                }

                return null;
            });

            try {
                Map<String,Integer> result = futureResult.get();
                if(result==null){
                    System.out.println("Query is not ready yet");
                }else{
                    System.out.println(result);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

    }

//ad /Users/ilija/Desktop/Word-Counter/Word-Counter/test/example
//get file|corpus_mcfly
//query file|corpus_mcfly
//get file|corpus_riker
//query file|corpus_mcfly



    private Map<String, Integer> sumCountsForCorpus(String corpusName) {

        Map<String, Integer> totalCounts = new HashMap<>();
        int processedFiles = 0;
        File corpus = null;

        while (true) {

            for (Result result : this.results) {
                File file = new File(result.getPath());
                if (file.getParentFile().getName().equals(corpusName)) {
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

//aw https://www.swimuniversity.com/hot-tub-maintenance/
    //get web|summary
//query web|swimuniversity.com


//    aw https://www.gatesnotes.com/2019-Annual-Letter
//
//    get web|gatesnotes.com

    private Map<String, Integer> sumCountsForWeb(String name) {
        Set<Result> set = new HashSet<>();
        boolean allCountsAvailable;

        do {
            allCountsAvailable = true;
            for (Result result : this.results) {
                if(result.getScanType()!=ScanType.WEB || getParent(result)==null){
                    continue;
                }
                if (getParent(result)!=null && getParent(result).equals(name)) {
                    Map<String, Integer> resultCounts = result.getCounts();
                    if (resultCounts == null) {
                        allCountsAvailable = false;
                        continue;
                    }
                    set.add(result);
                }
            }

            if (!allCountsAvailable) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } while (!allCountsAvailable);

        Map<String, Integer> totalCounts = new HashMap<>();

        for (Result result : set) {
            result.getCounts().forEach((key, value) -> totalCounts.merge(key, value, Integer::sum));
        }

        return totalCounts;
    }



    private String getParent(Result result) {
        ScanType scanType = result.getScanType();
        String path = result.getPath();
        String parent;

        if(scanType == ScanType.FILE){
            File file = new File(result.getPath());
            return file.getParentFile().getName();
        }else if(scanType == ScanType.WEB) {
            try {
                parent = new URI(path).getHost();
            }catch(URISyntaxException e) {
                return null;
            }

            if (parent == null){
                return null;
            }

            return parent.startsWith("www.") ? parent.substring(4) : parent;
        }else {
            return null;
        }
    }

//    ad /Users/ilija/Desktop/Word-Counter/Word-Counter/test/example
//    query file|corpus_mcfly
//    query file|corpus_troll
    // get file|summary
    private Map<String , Integer> getSyncCorpusCounts(String corpusName){

        Map<String, Integer> totalCounts = new HashMap<>();
        File corpus = null;

        for (Result result : this.results) {
            File file = new File(result.getPath());

            if(result.getScanType()!=ScanType.FILE || !file.getParentFile().getName().equals(corpusName)){
                continue;
            }

            if (corpus == null) {
                corpus = new File(file.getParentFile().getAbsolutePath());
            }

            if (!result.isDone()){
                return null;
            }

            result.getCounts().forEach((key, value) -> totalCounts.merge(key, value, Integer::sum));

        }

        return totalCounts;
    }

//    aw https://www.gatesnotes.com/2019-Annual-Letter
//
//    query web|gatesnotes.com
//    query web|summary


    private Map<String, Integer> getSyncWebCounts(String domain){

        Map<String, Integer> totalCounts = new HashMap<>();

        for(Result result:results){
            if(!domain.equals(getParent(result))){
                continue;
            }

            if(getParent(result) == null || !result.isDone()){
                return null;
            }

            result.getCounts().forEach((key, value) -> totalCounts.merge(key, value, Integer::sum));

        }

        return totalCounts;

    }

//ad /Users/ilija/Desktop/Word-Counter/Word-Counter/test/example
//get file|summary
    private Map<String, Map<String, Integer>> summaryFileAsync(){
        Map<String, Map<String, Integer>> counts = new ConcurrentHashMap<>();

        while (true){
            boolean flag = true;
            for (Result result:results){
                if(!result.isDone()){
                    flag = false;
                    break;
                }
            }

            if(!flag){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }else {
                break;
            }

        }


        for(Result result: results){
            if(result.getScanType() == ScanType.FILE){
                Map<String, Integer> count = sumCountsForCorpus(this.getParent(result));
                counts.putIfAbsent(this.getParent(result), count);
            }
        }

        return counts;
    }

//    aw https://www.gatesnotes.com/2019-Annual-Letter
    //get web|summary

    private Map<String, Map<String, Integer>> summaryWebAsync(){
        Map<String, Map<String, Integer>> counts = new ConcurrentHashMap<>();


        while (true) {
            boolean flag = true;
            for (Result result : this.results) {
                if (result.getScanType() != ScanType.WEB) {
                    continue;
                }
                if (!result.isDone()) {
                    flag = false;
                }
            }

            if(flag){
                break;
            }else {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }


        for(Result result: results){
            if(result.getScanType() == ScanType.WEB){
                Map<String, Integer> count = sumCountsForWeb(this.getParent(result));
                String parent = this.getParent(result);
                if(parent == null){
                    continue;
                }

                counts.putIfAbsent(this.getParent(result), count);
            }
        }

        return counts;
    }

    private Map<String, Map<String, Integer>> summaryWebSync(){

        Map<String, Map<String, Integer>> counts = new HashMap<>();

        for(Result result:results){
            if(!result.isDone()){
                return null;
            }
        }

        for(Result result: results){
            if(result.getScanType() == ScanType.WEB){
                Map<String, Integer> count = sumCountsForWeb(this.getParent(result));
                String parent = this.getParent(result);
                if(parent == null){
                    continue;
                }

                counts.putIfAbsent(this.getParent(result), count);
            }
        }

        return counts;

    }

    private Map<String, Map<String, Integer>> summaryFileSync(){

        Map<String, Map<String, Integer>> counts = new HashMap<>();

        for(Result result: results){
            if(result.getScanType() == ScanType.FILE){
                if(!result.isDone()){
                    return null;
                }
                Map<String, Integer> count = getSyncCorpusCounts(this.getParent(result));
                counts.putIfAbsent(this.getParent(result), count);
            }
        }

        return counts;

    }



}
