package com.kids.domacizadatak1.jobs;

import com.kids.domacizadatak1.workers.WebScannerWorker;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

public class WebJob implements ScanningJob {
    private ScanType scanType;
    private String query;
    private String url;
    private Integer hopCount;
    private Future<Map<String,Integer>> webJobResult;

    public WebJob(String query, String url, Integer hopCount) {
        this.scanType = ScanType.WEB;
        this.query = "web|" + url.split("\\.")[1];
        this.url = url;
        this.hopCount = hopCount;
    }

    @Override
    public ScanType getType() {
        return this.scanType;
    }

    @Override
    public String getQuery() {
        return this.query;
    }

    @Override
    public Future<Map<String,Integer>> initiate(ExecutorCompletionService executorCompletionService) {
        webJobResult = executorCompletionService.submit((Callable) new WebScannerWorker());
        return webJobResult;
    }
}
