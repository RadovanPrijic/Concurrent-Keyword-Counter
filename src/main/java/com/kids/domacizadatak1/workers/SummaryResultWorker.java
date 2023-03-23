package com.kids.domacizadatak1.workers;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class SummaryResultWorker implements Callable<Map<String, Map<String, Integer>>> {

    private Map<String, Future<Map<String, Integer>>> fileJobResultsMap;
    private Map<String, Future<Map<String, Integer>>> webJobResultsMap;
    private Map<String, Future<Map<String, Integer>>> webDomainResultsMap;
    private Map<String, Map<String, Integer>> fileSummaryResultsMap;
    private Map<String, Map<String, Integer>> webSummaryResultsMap;

    public SummaryResultWorker(Map<String, Future<Map<String, Integer>>> fileJobResultsMap,
                               Map<String, Future<Map<String, Integer>>> webJobResultsMap,
                               Map<String, Future<Map<String, Integer>>> webDomainResultsMap,
                               Map<String, Map<String, Integer>> fileSummaryResultsMap,
                               Map<String, Map<String, Integer>> webSummaryResultsMap) {
        this.fileJobResultsMap = fileJobResultsMap;
        this.webJobResultsMap = webJobResultsMap;
        this.webDomainResultsMap = webDomainResultsMap;
        this.fileSummaryResultsMap = fileSummaryResultsMap;
        this.webSummaryResultsMap = webSummaryResultsMap;
    }

    @Override
    public Map<String, Map<String, Integer>> call() throws Exception {

        return null;
    }
}
