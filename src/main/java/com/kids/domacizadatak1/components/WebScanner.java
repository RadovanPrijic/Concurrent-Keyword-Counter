package com.kids.domacizadatak1.components;

import com.kids.domacizadatak1.CoreApp;
import com.kids.domacizadatak1.jobs.ScanningJob;
import com.kids.domacizadatak1.jobs.WebJob;

import java.util.Map;
import java.util.concurrent.*;

public class WebScanner implements Runnable {

    private BlockingQueue<WebJob> webScannerJobQueue;
    private BlockingQueue<ScanningJob> jobQueue;
    private final ExecutorService webScannerThreadPool;
    private final ConcurrentHashMap<String, Long> urlCache;

    public WebScanner(BlockingQueue<WebJob> webScannerJobQueue, BlockingQueue<ScanningJob> jobQueue) {
        this.jobQueue = jobQueue;
        this.webScannerJobQueue = webScannerJobQueue;
        this.webScannerThreadPool = Executors.newCachedThreadPool();
        this.urlCache = new ConcurrentHashMap<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                WebJob webJob = this.webScannerJobQueue.take();
                webJob.setJobQueue(jobQueue);
                webJob.setCachedThreadPool(webScannerThreadPool);
                webJob.setUrlCache(urlCache);
                System.err.println("HELLO THERE " + webJob.getUrl());
                Future<Map<String, Integer>> webJobResult = webJob.initiate();
                CoreApp.getResultRetriever().addCorpusResult(webJob, webJobResult);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public ConcurrentHashMap<String, Long> getUrlCache() {
        return urlCache;
    }
}
