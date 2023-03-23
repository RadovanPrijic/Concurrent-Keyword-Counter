package com.kids.domacizadatak1.components;

import com.kids.domacizadatak1.jobs.ScanningJob;
import com.kids.domacizadatak1.jobs.WebJob;
import com.kids.domacizadatak1.results.Result;

import java.util.Map;
import java.util.concurrent.*;

public class WebScanner implements Runnable {

    private BlockingQueue<WebJob> webScannerJobQueue;
    private BlockingQueue<ScanningJob> jobQueue;
    private BlockingQueue<Result> resultQueue;
    private final ExecutorService webScannerThreadPool;
    private final ConcurrentHashMap<String, Long> urlCache;
    private final ExecutorCompletionService<Map<String, Integer>> webScannerResults;

    public WebScanner(BlockingQueue<WebJob> webScannerJobQueue, BlockingQueue<ScanningJob> jobQueue, BlockingQueue<Result> resultQueue) {
        this.jobQueue = jobQueue;
        this.webScannerJobQueue = webScannerJobQueue;
        this.resultQueue = resultQueue;
        this.webScannerThreadPool = Executors.newCachedThreadPool();
        this.urlCache = new ConcurrentHashMap<>();
        this.webScannerResults = new ExecutorCompletionService<>(this.webScannerThreadPool);
    }

    @Override
    public void run() {
        while (true) {
            try {
                WebJob webJob = this.webScannerJobQueue.take();
                webJob.setJobQueue(jobQueue);
                webJob.setCachedThreadPool(webScannerThreadPool);
                webJob.setUrlCache(urlCache);
                webJob.initiate();
                //TODO Slanje ResultRetriever-u rezultata iz initiate metoda
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
