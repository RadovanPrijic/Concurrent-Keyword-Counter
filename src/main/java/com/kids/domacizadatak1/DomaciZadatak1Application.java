package com.kids.domacizadatak1;

import com.kids.domacizadatak1.components.FileScanner;
import com.kids.domacizadatak1.components.ResultRetriever;
import com.kids.domacizadatak1.components.WebScanner;
import com.kids.domacizadatak1.components.DirectoryCrawler;
import com.kids.domacizadatak1.components.JobDispatcher;
import com.kids.domacizadatak1.jobs.FileJob;
import com.kids.domacizadatak1.jobs.ScanningJob;
import com.kids.domacizadatak1.jobs.WebJob;
import com.kids.domacizadatak1.results.Result;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

@SpringBootApplication
public class DomaciZadatak1Application {

    private static DirectoryCrawler directoryCrawler;
    private static JobDispatcher jobDispatcher;
    private static FileScanner fileScanner;
    private static WebScanner webScanner;
    private static ResultRetriever resultRetriever;

    private static HashMap<String, Integer> keywordsMap = new HashMap<>();
    private static String fileCorpusPrefix;
    private static Integer dirCrawlerSleepTime;
    private static Integer fileScanningSizeLimit;
    private static Integer hopCount;
    private static Integer urlRefreshTime;

    public static final CopyOnWriteArrayList<String> directoriesToCrawl = new CopyOnWriteArrayList<>();
    public static final BlockingQueue<ScanningJob> jobQueue = new LinkedBlockingQueue<>();
    public static final BlockingQueue<FileJob> fileScannerJobQueue = new LinkedBlockingQueue<>();
    public static final BlockingQueue<WebJob> webScannerJobQueue = new LinkedBlockingQueue<>();
    public static final BlockingQueue<Result> resultQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) throws IOException {
        SpringApplication.run(DomaciZadatak1Application.class, args);

        setPropertyVariables("src/main/resources/application.properties");
        initalizeComponents();

        while(true) {
            Scanner scanner = new Scanner(System.in);
            String userInput = scanner.nextLine();
            writeCommand(userInput);
        }
    }

    public static void initalizeComponents(){
        directoryCrawler = new DirectoryCrawler(directoriesToCrawl, jobQueue, dirCrawlerSleepTime, fileCorpusPrefix);
        Thread directoryCrawlerThread = new Thread(directoryCrawler);
        directoryCrawlerThread.start();

        jobDispatcher = new JobDispatcher(jobQueue, fileScannerJobQueue, webScannerJobQueue);
        Thread jobDispatcherThread = new Thread(jobDispatcher);
        jobDispatcherThread.start();

        fileScanner = new FileScanner(fileScannerJobQueue, resultQueue);
        Thread fileScannerThread = new Thread(fileScanner);
        fileScannerThread.start();

        webScanner = new WebScanner(jobQueue, webScannerJobQueue, resultQueue);
        Thread webScannerThread = new Thread(webScanner);
        webScannerThread.start();

        resultRetriever = new ResultRetriever(resultQueue);
        Thread resultRetrieverThread = new Thread(resultRetriever);
        resultRetrieverThread.start();
    }

    public static void setPropertyVariables(String propertiesFilePath) throws IOException {
        File file = new File(propertiesFilePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null){
            if(line.startsWith("keywords=")){
                String keywordsArr = line.split("=")[1];
                String[] keywords = keywordsArr.split(",");
                for (String keyword : keywords)
                    keywordsMap.put(keyword, 0);
            } else if (line.startsWith("file.corpus.prefix=")){
                fileCorpusPrefix = line.split("=")[1];
            } else if (line.startsWith("dir.crawler.sleep.time=")){
                dirCrawlerSleepTime = Integer.parseInt(line.split("=")[1]);
            } else if (line.startsWith("file.scanning.size.limit=")){
                fileScanningSizeLimit = Integer.parseInt(line.split("=")[1]);
            } else if (line.startsWith("hop.count=")){
                hopCount = Integer.parseInt(line.split("=")[1]);
            } else if (line.startsWith("url.refresh.time="))
                urlRefreshTime = Integer.parseInt(line.split("=")[1]);
        }
    }

    public static void writeCommand(String userInput){
        String command = null;
        String parameter = null;

        if(userInput.split(" ").length > 1 ){
            command = userInput.split(" ")[0];
            parameter = userInput.split(" ")[1];
        }

        if(command.equals("ad")){

        } else if (command.equals("aw")){

        } else if (command.equals("get")){

        } else if (command.equals("query")){

        } else if (command.equals("cfs")){

        } else if (command.equals("cws")){

        } else if (command.equals("stop")){
            //TODO Pravilno obustavljanje programa (zatvaranje thread pool-ova, poison pills, itd.)
            System.exit(0);
        } else
            System.out.println("Uneli ste nepostojecu komandu.");
    }
}
