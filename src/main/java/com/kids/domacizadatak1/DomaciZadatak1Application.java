package com.kids.domacizadatak1;

import com.kids.domacizadatak1.components.FileScanner;
import com.kids.domacizadatak1.components.ResultRetriever;
import com.kids.domacizadatak1.components.WebScanner;
import com.kids.domacizadatak1.components.DirectoryCrawler;
import com.kids.domacizadatak1.components.JobDispatcher;
import com.kids.domacizadatak1.jobs.ScanningJob;
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

    private static final CopyOnWriteArrayList<String> directoriesToCrawl = new CopyOnWriteArrayList<>();
    private static final BlockingQueue<ScanningJob> jobQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) throws IOException {
        SpringApplication.run(DomaciZadatak1Application.class, args);

        setPropertyVariables("D:\\kids-domaci-zadatak-1\\src\\main\\resources\\application.properties");
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

        jobDispatcher = new JobDispatcher();
        Thread jobDispatcherThread = new Thread(jobDispatcher);
        jobDispatcherThread.start();

        fileScanner = new FileScanner();
        Thread fileScannerThread = new Thread(fileScanner);
        fileScannerThread.start();

        webScanner = new WebScanner();
        Thread webScannerThread = new Thread(webScanner);
        webScannerThread.start();

        resultRetriever = new ResultRetriever();
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

    public static void writeCommand(String command){
        if(command.startsWith("ad ")){

        } else if (command.startsWith("aw ")){

        } else if (command.startsWith("get file|") || command.startsWith("get web|")){

        } else if (command.startsWith("query file|") || command.startsWith("query web|")){

        } else if (command.equals("cfs")){

        } else if (command.equals("cws")){

        } else if (command.equals("stop")){
            //TODO Pravilno obustavljanje programa (zatvaranje thread pool-ova, itd.)
            System.exit(0);
        } else
            System.out.println("Uneli ste nepostojecu komandu.");
    }
}
