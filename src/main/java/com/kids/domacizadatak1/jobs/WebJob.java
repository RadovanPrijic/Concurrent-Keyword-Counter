package com.kids.domacizadatak1.jobs;

import com.kids.domacizadatak1.workers.WebScannerWorker;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class WebJob implements ScanningJob {
    private ScanType scanType;
    private String query;
    private String url;
    private String keywords;
    private Integer hopCount;
    private Integer urlRefreshTime;
    private Future<Map<String,Integer>> webJobResult;
    private ExecutorService cachedThreadPool;

    public WebJob(String query, String url) {
        this.scanType = ScanType.WEB;
        this.query = "web|" + url.split("\\.")[1];
        this.url = url;
    }

    @Override
    public Future<Map<String,Integer>> initiate() {
        //webJobResult = executorCompletionService.submit((Callable) new WebScannerWorker()); //TODO Proslediti workeru relevantne parametre
        return webJobResult;
    }

    @Override
    public ScanType getType() {
        return this.scanType;
    }

    @Override
    public String getQuery() {
        return this.query;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public void setHopCount(Integer hopCount) {
        this.hopCount = hopCount;
    }

    public void setUrlRefreshTime(Integer urlRefreshTime) {
        this.urlRefreshTime = urlRefreshTime;
    }

    public void setCachedThreadPool(ExecutorService cachedThreadPool) {
        this.cachedThreadPool = cachedThreadPool;
    }
}
