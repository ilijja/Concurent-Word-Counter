package scanner.web;

import app.Properties;
import job.Job;
import job.JobQueue;
import job.ScanType;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import result.Result;
import result.ResultRetriever;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebProcessingTask implements Callable<Map<String, Integer>> {

    Job job;
    JobQueue jobQueue;

    ResultRetriever resultRetriever;

    public WebProcessingTask(Job job, JobQueue jobQueue, ResultRetriever resultRetriever) {
        this.job = job;
        this.jobQueue = jobQueue;
        this.resultRetriever = resultRetriever;
    }

    @Override
    public Map<String, Integer> call() {

        System.out.println("Starting web scan for: " + job.getPath() );

        Map<String,Integer> counts = countWords(job);


        if(!job.isScanned()){
            findHops(job);
        }

        job.setScanned(true);

        return counts;
    }

    public Map<String, Integer> countWords(Job job) {
        Map<String, Integer> counts = new HashMap<>();
        Document doc;


        String[] keywords = Properties.KEYWORDS.get().split(",");
        for (String keyword : keywords) {
            counts.put(keyword, 0);
        }

        try {
            doc = Jsoup.connect(job.getPath()).get();
            String content = doc.body().text();

            for (String keyword : keywords) {
                counts.put(keyword, countOccurrences(content, keyword));
            }

        } catch (HttpStatusException e) {

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return counts;
    }


    public static int countOccurrences(String text, String word) {
        int count = 0;
        Pattern pattern = Pattern.compile("\\b" + Pattern.quote(word) + "\\b");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            count++;
        }

        return count;
    }

    private void findHops(Job job) {

        int hopCount = Integer.parseInt(Properties.HOP_COUNT.get());
        Map<Integer, Set<String>> hops = new HashMap<>();

        Set<String> base = new HashSet<>();
        base.add(job.getPath());
        hops.put(0, base);

        System.out.println(hopCount);
        for (int i = 0; i < hopCount; i++) {

            Set<String> currentHop = new HashSet<>();

            for (String url : hops.get(i)) {
                System.out.println("Processing URL: " + url);


                if ((url.startsWith("http://") || url.startsWith("https://"))) {
                    try {
                        Document content = Jsoup.connect(url).get();
                        Elements links = content.select("a[href]");

                        for (Element link : links) {
                            String childUrl = link.absUrl("href");
                            currentHop.add(childUrl);
                        }

                    } catch (IOException e) {
                        System.err.println("Error connecting to URL: " + url);
                    }
                } else {
                    System.err.println("Ignoring non-web URL: " + url);
                }
            }

            hops.put(i + 1, currentHop);
        }


        for (Integer i : hops.keySet()) {
            for (String item : hops.get(i)) {

                if (!(item.startsWith("http://") || item.startsWith("https://"))) {
                    continue;
                }

                this.resultRetriever.addResult(new Result(item, null, ScanType.WEB));

                if (i == 0) {
                    continue;
                }

                Job newJob = new Job(item, ScanType.WEB, true);

                jobQueue.enqueue(newJob);
            }
        }



    }


}
