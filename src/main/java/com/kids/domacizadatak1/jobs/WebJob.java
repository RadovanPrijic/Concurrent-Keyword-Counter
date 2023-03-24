package com.kids.domacizadatak1.jobs;

import com.kids.domacizadatak1.workers.WebScannerWorker;

import java.util.Map;
import java.util.concurrent.*;

public class WebJob implements ScanningJob {

    private final ScanType scanType;
    private final String query;
    private final String url;
    private final Integer hopCount;
    private BlockingQueue<ScanningJob> jobQueue;
    private ExecutorService cachedThreadPool;
    private ConcurrentHashMap<String, Long> urlCache;

    public WebJob(String url, Integer hopCount) {
        this.scanType = ScanType.WEB;
        this.query = "web|" + url;
        this.url = url;
        this.hopCount = hopCount;
    }

    @Override
    public Future<Map<String, Integer>> initiate() {
        return this.cachedThreadPool.submit(new WebScannerWorker(url, hopCount, jobQueue, urlCache));
    }

    @Override
    public ScanType getType() {
        return this.scanType;
    }

    @Override
    public String getQuery() {
        return this.query;
    }

    public String getUrl() {
        return url;
    }

    public void setJobQueue(BlockingQueue<ScanningJob> jobQueue) {
        this.jobQueue = jobQueue;
    }

    public void setCachedThreadPool(ExecutorService cachedThreadPool) {
        this.cachedThreadPool = cachedThreadPool;
    }

    public void setUrlCache(ConcurrentHashMap<String, Long> urlCache) {
        this.urlCache = urlCache;
    }
}
