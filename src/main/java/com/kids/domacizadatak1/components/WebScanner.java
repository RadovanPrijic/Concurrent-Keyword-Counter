package com.kids.domacizadatak1.components;

import com.kids.domacizadatak1.jobs.ScanningJob;
import com.kids.domacizadatak1.jobs.WebJob;
import com.kids.domacizadatak1.results.Result;

import java.util.Map;
import java.util.concurrent.*;

public class WebScanner implements Runnable {

    private BlockingQueue<ScanningJob> jobQueue;
    private BlockingQueue<WebJob> webScannerJobQueue;
    private BlockingQueue<Result> resultQueue;
    private ExecutorService webScannerThreadPool;
    private ExecutorCompletionService<Map<String, Integer>> webScannerResults;
    private String keywords;
    private Integer hopCount;
    private Integer urlRefreshTime;

    public WebScanner(BlockingQueue<ScanningJob> jobQueue, BlockingQueue<WebJob> webScannerJobQueue, BlockingQueue<Result> resultQueue,
                        String keywords, Integer hopCount, Integer urlRefreshTime) {
        this.jobQueue = jobQueue;
        this.webScannerJobQueue = webScannerJobQueue;
        this.resultQueue = resultQueue;
        this.webScannerThreadPool = Executors.newCachedThreadPool();
        this.webScannerResults = new ExecutorCompletionService<>(this.webScannerThreadPool);
        this.keywords = keywords;
        this.hopCount = hopCount;
        this.urlRefreshTime = urlRefreshTime;
    }

    @Override
    public void run() {
        while (true) {
            try {
                WebJob webJob = this.webScannerJobQueue.take();
                webJob.setKeywords(keywords);
                webJob.setHopCount(hopCount);
                webJob.setUrlRefreshTime(urlRefreshTime);
                webJob.setCachedThreadPool(webScannerThreadPool);
                System.out.println(webJob.getQuery());
                //webJob.initiate();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //TODO Thread pool shutdown
    }
}
