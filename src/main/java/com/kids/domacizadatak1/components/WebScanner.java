package com.kids.domacizadatak1.components;

import com.kids.domacizadatak1.CoreApp;
import com.kids.domacizadatak1.jobs.FileJob;
import com.kids.domacizadatak1.jobs.ScanningJob;
import com.kids.domacizadatak1.jobs.WebJob;

import java.util.Map;
import java.util.concurrent.*;

public class WebScanner {

    private BlockingQueue<ScanningJob> jobQueue;
    private ResultRetriever resultRetriever;
    private final ExecutorService webScannerThreadPool;
    private final ConcurrentHashMap<String, Long> urlCache;

    public WebScanner(BlockingQueue<ScanningJob> jobQueue, ResultRetriever resultRetriever) {
        this.jobQueue = jobQueue;
        this.resultRetriever = resultRetriever;
        this.webScannerThreadPool = Executors.newCachedThreadPool();
        this.urlCache = new ConcurrentHashMap<>();
    }

    public void submitJobToPool (WebJob webJob){
        webJob.setJobQueue(jobQueue);
        webJob.setCachedThreadPool(webScannerThreadPool);
        webJob.setUrlCache(urlCache);
        Future<Map<String, Integer>> webJobResult = webJob.initiate();
        resultRetriever.addCorpusResult(webJob, webJobResult);
    }

    public void stop(){
        webScannerThreadPool.shutdown();
        System.out.println("Web scanner thread pool has been successfully shut down.");
    }

    public ConcurrentHashMap<String, Long> getUrlCache() {
        return urlCache;
    }
}
