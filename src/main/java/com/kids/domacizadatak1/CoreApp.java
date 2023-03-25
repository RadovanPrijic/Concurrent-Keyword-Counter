package com.kids.domacizadatak1;

import com.kids.domacizadatak1.components.FileScanner;
import com.kids.domacizadatak1.components.ResultRetriever;
import com.kids.domacizadatak1.components.WebScanner;
import com.kids.domacizadatak1.components.DirectoryCrawler;
import com.kids.domacizadatak1.components.JobDispatcher;
import com.kids.domacizadatak1.jobs.*;
import org.jsoup.Jsoup;

import java.io.*;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class CoreApp {

    private static DirectoryCrawler directoryCrawler;
    private static JobDispatcher jobDispatcher;
    private static FileScanner fileScanner;
    private static WebScanner webScanner;
    private static ResultRetriever resultRetriever;

    public static String keywords;
    public static String fileCorpusPrefix;
    public static Integer dirCrawlerSleepTime;
    public static Integer fileScanningSizeLimit;
    public static Integer hopCount;
    public static Integer urlRefreshTime;
    private static boolean killCore = false;

    private static Thread directoryCrawlerThread;
    private static Thread jobDispatcherThread;
    public static final CopyOnWriteArrayList<String> directoriesToCrawl = new CopyOnWriteArrayList<>();
    public static final BlockingQueue<ScanningJob> jobQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) throws IOException {

        setPropertyVariables();
        initializeComponents();

        Scanner scanner = new Scanner(System.in);
        String command, parameter = null, queryType = null;
        boolean isQuery, isSummary = false;
        Map<String, Integer> resultMap;
        Map<String, Map<String, Integer>> summaryResultMap;

        System.out.println("Welcome! Enter your first command:");

        while(!killCore) {
            String userInput = scanner.nextLine();
            String[] arguments = userInput.trim().split(" ");

            if(arguments.length == 0 || arguments.length > 2){
                System.err.println("You have entered an invalid number of arguments.");
                continue;
            }
            command = arguments[0];

            if(arguments.length == 2){
                parameter = arguments[1];
                isQuery = parameter.contains("|") && parameter.split("\\|").length == 2;
                if(isQuery){
                    String[] queryArguments = parameter.split("\\|");
                    if(queryArguments[0].equals("file")){
                        queryType = "file";
                        isSummary = queryArguments[1].equals("summary");
                    } else if (queryArguments[0].equals("web")){
                        queryType = "web";
                        isSummary = queryArguments[1].equals("summary");
                    } else {
                        System.err.println("You have entered an invalid query parameter.");
                        continue;
                    }
                } else
                    queryType = "invalid";
            }

            switch (command) {
                case "ad" -> {
                    File directory = new File(parameter);
                    if (!(directory.exists() && directory.canRead())){
                        System.err.println("The crawling process could not be started in directory " + parameter + ". " +
                                "Check again if this is a valid directory.");
                    } else {
                        if(directoriesToCrawl.contains(parameter))
                            continue;
                        System.out.println("Starting to crawl through directory " + parameter + " ...");
                        directoriesToCrawl.add(parameter);
                    }
                }
                case "aw" -> {
                    try {
                        Jsoup.connect(parameter).get();
                        if (webScanner.getUrlCache().contains(parameter) &&
                                        System.currentTimeMillis() - webScanner.getUrlCache().get(parameter) < CoreApp.urlRefreshTime)
                            continue;
                        System.out.println("Starting to scan URL " + parameter + " ...");
                        jobQueue.add(new WebJob(parameter, hopCount));
                    }
                    catch (Exception e) {
                        System.err.println("The URL you have provided is invalid. Check again if " + parameter + " is a valid URL address.");
                    }
                }
                case "get", "query" -> {
                    if(queryType.equals("file")){
                        if(!isSummary){
                            resultMap = resultRetriever.retrieveResult(command, parameter);
                            if (resultMap != null) {
                                resultMap.forEach((key, value) -> System.out.println((key + ":" + value)));
                            }
                        } else {
                            summaryResultMap = resultRetriever.retrieveSummary(command, ScanType.FILE);
                            if (summaryResultMap != null) {
                                summaryResultMap.forEach((key, value) -> System.out.println((key + ":" + value)));
                            }
                        }
                    } else if (queryType.equals("web")){
                        if(!isSummary){
                            resultMap = resultRetriever.retrieveResult(command, parameter);
                            if (resultMap != null) {
                                resultMap.forEach((key, value) -> System.out.println((key + ":" + value)));
                            }
                        } else {
                            summaryResultMap = resultRetriever.retrieveSummary(command, ScanType.WEB);
                            if (summaryResultMap != null) {
                                summaryResultMap.forEach((key, value) -> System.out.println((key + ":" + value)));
                            }
                        }
                    } else {
                        System.err.println("You have entered an invalid query parameter.");
                        continue;
                    }
                }
                case "cfs" -> {
                    resultRetriever.clearSummary(ScanType.FILE);
                    System.out.println("Clearing file corpus summary ...");
                }
                case "cws" -> {
                    resultRetriever.clearSummary(ScanType.WEB);
                    System.out.println("Clearing web corpus summary ...");
                }
                case "stop" -> {
                    try {
                        System.out.println("Stopping the system ...");

                        directoryCrawler.setKillCrawler(true);
                        jobQueue.add(new PoisonPill());
                        resultRetriever.stop();

                        directoryCrawlerThread.join();
                        jobDispatcherThread.join();

                        killCore = true;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                default -> {
                    System.err.println("You have entered an invalid command.");
                }
            }
        }
        scanner.close();
        System.out.println("Main/CLI has been successfully shut down.");
    }

    public static void initializeComponents() {
        directoryCrawler = new DirectoryCrawler(directoriesToCrawl, jobQueue);
        directoryCrawlerThread = new Thread(directoryCrawler);
        directoryCrawlerThread.start();

        resultRetriever = new ResultRetriever();

        fileScanner = new FileScanner(resultRetriever);

        webScanner = new WebScanner(jobQueue, resultRetriever);

        jobDispatcher = new JobDispatcher(jobQueue, fileScanner, webScanner);
        jobDispatcherThread = new Thread(jobDispatcher);
        jobDispatcherThread.start();
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
}
