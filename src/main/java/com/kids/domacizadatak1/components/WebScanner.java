package com.kids.domacizadatak1.components;

import com.kids.domacizadatak1.CoreApp;
import com.kids.domacizadatak1.jobs.FileJob;
import com.kids.domacizadatak1.jobs.ScanningJob;
import com.kids.domacizadatak1.jobs.WebJob;

import java.util.Map;
import java.util.concurrent.*;

public class WebScanner {

    private BlockingQueue<ScanningJob> jobQueue;
    private final ExecutorService webScannerThreadPool;
    private final ConcurrentHashMap<String, Long> urlCache;

    public WebScanner(BlockingQueue<ScanningJob> jobQueue) {
        this.jobQueue = jobQueue;
        this.webScannerThreadPool = Executors.newCachedThreadPool();
        this.urlCache = new ConcurrentHashMap<>();
    }

    public void submitJobToPool (WebJob webJob){
        webJob.setJobQueue(jobQueue);
        webJob.setCachedThreadPool(webScannerThreadPool);
        webJob.setUrlCache(urlCache);
        Future<Map<String, Integer>> webJobResult = webJob.initiate();
        CoreApp.getResultRetriever().addCorpusResult(webJob, webJobResult);
    }

    public ExecutorService getWebScannerThreadPool() {
        return webScannerThreadPool;
    }

    public ConcurrentHashMap<String, Long> getUrlCache() {
        return urlCache;
    }
}
