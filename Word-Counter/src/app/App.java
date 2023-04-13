package app;

import crawler.Crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class App {

    TaskManager taskManager;

    public void init(){
        this.load();
        this.readInput();
    }


    private void load(){
        Scanner scanner = null;

        try {
            scanner = new Scanner(new File(Assets.PATH_PROPERTIES));

            while(scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if(line.startsWith("#") || line.length() == 0) {
                    continue;
                }

                String[] keyValue = line.split("=");

                String key = keyValue[0].trim().toUpperCase();
                String value = keyValue[1].trim();

                Properties property = Properties.valueOf(key);
                property.set(value);

            }



        }catch(IOException e) {
            System.out.println("Error Loading Properties");
        }finally{
            if(scanner != null) {
                scanner.close();
            }
        }


    }

    public void readInput() {

        this.taskManager = new TaskManager();

        Scanner scanner = new Scanner(System.in);

        while(true) {
            String line = scanner.nextLine().trim();
            String[] tokens = line.split(" ");

            String command = tokens[0];
            String param = null;

            if(tokens.length == 2) {
                param = tokens[1];
            }

            int totalParams = tokens.length - 1;

            switch(command) {
                case "ad":
                    taskManager.addPath(param);
                    break;
                case "aw":
                    taskManager.addWeb(param);
                    break;
                case "get":
                    taskManager.getResult(param);
                    break;
                case "query":
                    taskManager.queryResult(param);
                    break;
                case "cfs":

                    break;
                case "cws":

                    break;
                case "stop":

                    scanner.close();
                    return;
                default:
                    System.out.println("Error command");
                    break;
            }

        }
    }


}
