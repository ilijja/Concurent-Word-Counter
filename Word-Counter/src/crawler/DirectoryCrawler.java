package crawler;

import app.Assets;
import app.Properties;
import job.Job;
import job.JobQueue;
import job.ScanType;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DirectoryCrawler implements Crawler, Runnable{

    String path;
    JobQueue jobQueue;

    private ConcurrentHashMap<String, FileInfo> fileInfos;

    public DirectoryCrawler(JobQueue jobQueue) {
        this.jobQueue = jobQueue;
        this.fileInfos = new ConcurrentHashMap<>();
    }

    @Override
    public void addPath(String path) {
        this.path = path;
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void stop() {

    }

    @Override
    public void run() {

        while(!Thread.currentThread().isInterrupted()) {

            List<File> files = new ArrayList<>();

            File root = new File(path);

            if(!root.exists()) {
                System.out.println("Does not exist");
                return;
            }

            for(File file: this.findDirectories(files,root)){
                if(file.getName().contains(Properties.FILE_CORPUS_PREFIX.get())){
                    this.addJob(file);
                }
            }

//     ad /Users/ilija/Desktop/Word-Counter/Word-Counter/test/example
//            aw facebook.com

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void addJob(File root){

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        for(File file : root.listFiles()) {
            String path = file.getAbsolutePath();
            String lastModified = df.format(file.lastModified());
            FileInfo fileInfo = new FileInfo(lastModified);

            if(fileInfo.equals(this.fileInfos.get(path))) {
                continue;
            }

            if(!fileInfo.equals(this.fileInfos.get(path))){
                this.fileInfos.replace(path, fileInfo);
            }
            this.fileInfos.putIfAbsent(path, fileInfo);


            this.jobQueue.enqueue(new Job(file.getAbsolutePath(), ScanType.FILE));
        }

    }




    private List<File> findDirectories(List<File> files, File root){
        for(File file: root.listFiles()){
            if(file.isDirectory()){
                files.add(file);
                findDirectories(files,file);
            }
        }

        return files;
    }


    private class FileInfo{

        String lastModified;

        public FileInfo(String lastModified) {
            this.lastModified = lastModified;
        }

        public String getLastModified() {
            return lastModified;
        }

        public void setLastModified(String lastModified) {
            this.lastModified = lastModified;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof FileInfo) {
                FileInfo fileInfo = (FileInfo) obj;
                return fileInfo.lastModified.equals(lastModified);
            }

            return false;
        }

    }



}
