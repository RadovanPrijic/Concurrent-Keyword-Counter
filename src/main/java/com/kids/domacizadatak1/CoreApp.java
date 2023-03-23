package com.kids.domacizadatak1;

import com.kids.domacizadatak1.components.FileScanner;
import com.kids.domacizadatak1.components.ResultRetriever;
import com.kids.domacizadatak1.components.WebScanner;
import com.kids.domacizadatak1.components.DirectoryCrawler;
import com.kids.domacizadatak1.components.JobDispatcher;
import com.kids.domacizadatak1.jobs.FileJob;
import com.kids.domacizadatak1.jobs.ScanningJob;
import com.kids.domacizadatak1.jobs.WebJob;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
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
    private static ResultRetriever resultRetriever;

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

        resultRetriever = new ResultRetriever();

        //ScanningJob webJob = new WebJob("https://www.gatesnotes.com/2019-Annual-Letter", hopCount);
        //jobQueue.put(webJob);
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
            command = userInput.split(" ")[0];
            parameter = userInput.split(" ")[1];
        } else
            command = userInput;

        if(command.equals("ad")){

        }

        else if (command.equals("aw")){

        }

        else if (command.equals("get")){

        }

        else if (command.equals("query")){

        }

        else if (command.equals("cfs")){

        }

        else if (command.equals("cws")){

        }

        else if (command.equals("stop")){

            System.exit(0);
        }

        else
            System.out.println("Uneli ste nepostojecu komandu.");
    }

    public static ResultRetriever getResultRetriever() {
        return resultRetriever;
    }
}
