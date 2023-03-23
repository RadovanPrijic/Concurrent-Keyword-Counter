package com.kids.domacizadatak1;

import com.kids.domacizadatak1.components.FileScanner;
import com.kids.domacizadatak1.components.ResultRetriever;
import com.kids.domacizadatak1.components.WebScanner;
import com.kids.domacizadatak1.components.DirectoryCrawler;
import com.kids.domacizadatak1.components.JobDispatcher;
import com.kids.domacizadatak1.jobs.FileJob;
import com.kids.domacizadatak1.jobs.ScanType;
import com.kids.domacizadatak1.jobs.ScanningJob;
import com.kids.domacizadatak1.jobs.WebJob;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

@SpringBootApplication
public class CoreApp {

    private static DirectoryCrawler directoryCrawler;
    private static JobDispatcher jobDispatcher;
    private static FileScanner fileScanner;
    private static WebScanner webScanner;
    private static final ResultRetriever resultRetriever = new ResultRetriever();

    public static String keywords;
    public static String fileCorpusPrefix;
    public static Integer dirCrawlerSleepTime;
    public static Integer fileScanningSizeLimit;
    public static Integer hopCount;
    public static Integer urlRefreshTime;

    public static final CopyOnWriteArrayList<String> directoriesToCrawl = new CopyOnWriteArrayList<>();
    public static final BlockingQueue<ScanningJob> jobQueue = new LinkedBlockingQueue<>();
    public static final BlockingQueue<FileJob> fileScannerJobQueue = new LinkedBlockingQueue<>();
    public static final BlockingQueue<WebJob> webScannerJobQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        SpringApplication.run(CoreApp.class, args);

        setPropertyVariables();
        initializeComponents();

        while(true) {
            Scanner scanner = new Scanner(System.in);
            String userInput = scanner.nextLine();
            writeCommand(userInput);
        }
    }

    public static void initializeComponents() throws InterruptedException {
        directoryCrawler = new DirectoryCrawler(directoriesToCrawl, jobQueue);
        Thread directoryCrawlerThread = new Thread(directoryCrawler);
        directoryCrawlerThread.start();

        jobDispatcher = new JobDispatcher(jobQueue, fileScannerJobQueue, webScannerJobQueue);
        Thread jobDispatcherThread = new Thread(jobDispatcher);
        jobDispatcherThread.start();

        fileScanner = new FileScanner(fileScannerJobQueue);
        Thread fileScannerThread = new Thread(fileScanner);
        fileScannerThread.start();

        webScanner = new WebScanner(webScannerJobQueue, jobQueue);
        Thread webScannerThread = new Thread(webScanner);
        webScannerThread.start();

        //resultRetriever = new ResultRetriever();
    }

    public static void setPropertyVariables() throws IOException {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath + "application.properties";

        Properties appProperties = new Properties();
        appProperties.load(new FileInputStream(appConfigPath));

        keywords = appProperties.getProperty("keywords");
        fileCorpusPrefix = appProperties.getProperty("file_corpus_prefix");
        dirCrawlerSleepTime = Integer.parseInt(appProperties.getProperty("dir_crawler_sleep_time"));
        fileScanningSizeLimit = Integer.parseInt(appProperties.getProperty("file_scanning_size_limit"));
        hopCount = Integer.parseInt(appProperties.getProperty("hop_count"));
        urlRefreshTime = Integer.parseInt(appProperties.getProperty("url_refresh_time"));
    }

    public static void writeCommand(String userInput){
        String command = null;
        String parameter = null;

        if(userInput.split(" ").length > 1 ){
            command = userInput.split(" ")[0].trim();
            parameter = userInput.split(" ")[1].trim();
        } else
            command = userInput;

        switch (command) {
            case "ad" -> {
                try {
                    File file = new File(parameter);
                    System.out.println(file.getName());
                    directoriesToCrawl.add(parameter);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            case "aw" -> {
                try {
                    if (!(webScanner.getUrlCache().contains(parameter) &&
                            System.currentTimeMillis() - webScanner.getUrlCache().get(parameter) < CoreApp.urlRefreshTime))
                        jobQueue.add(new WebJob(parameter, hopCount));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            case "get" -> {
                // Get single result
                //Map<String, Integer> resultMap = resultRetriever.retrieveResult(command, parameter);
                //System.out.println(resultMap.entrySet());

                // Get summary
                Map<String, Map<String, Integer>> resultMap = resultRetriever.retrieveSummary(command, ScanType.WEB);
                System.out.println(resultMap.entrySet());
            }
            case "query" -> {
                if(!(parameter.split("\\|")[1].equals("summary"))){
                    Map<String, Integer> resultMap = resultRetriever.retrieveResult(command, parameter);
                    System.out.println(resultMap.entrySet());
                } else if(parameter.split("\\|")[0].equals("file") && parameter.split("\\|")[1].equals("summary")) {
                    Map<String, Map<String, Integer>> resultMap = resultRetriever.retrieveSummary(command, ScanType.FILE);
                    System.out.println(resultMap.entrySet());
                } else if (parameter.split("\\|")[0].equals("web") && parameter.split("\\|")[1].equals("summary")){
                    Map<String, Map<String, Integer>> resultMap = resultRetriever.retrieveSummary(command, ScanType.WEB);
                    System.out.println(resultMap.entrySet());
                }
            }
            case "cfs" -> {
                try {
                    resultRetriever.clearSummary(ScanType.FILE);
                    System.out.println("Clearing file corpus summary ...");
                    //System.err.println(resultRetriever.getFileSummaryResultsMap());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            case "cws" -> {
                try {
                    resultRetriever.clearSummary(ScanType.WEB);
                    System.out.println("Clearing web corpus summary ...");
                    //System.err.println(resultRetriever.getWebSummaryResultsMap().entrySet());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            case "stop" -> {
                try {
                    System.out.println("Stopping the system ...");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            default -> {
                System.out.println("You have entered an invalid command.");
            }
        }
    }

    public static ResultRetriever getResultRetriever() {
        return resultRetriever;
    }
}
