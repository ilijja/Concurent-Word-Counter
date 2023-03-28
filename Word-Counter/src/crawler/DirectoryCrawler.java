package crawler;

import app.Assets;
import app.Properties;
import job.ScanType;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DirectoryCrawler implements Crawler, Runnable{

    String path;

    public DirectoryCrawler() {

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
                    System.out.println(file.getName());
                }
            }

            System.out.println("\n");

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

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



}
