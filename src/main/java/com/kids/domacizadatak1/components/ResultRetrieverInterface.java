package com.kids.domacizadatak1.components;

import com.kids.domacizadatak1.jobs.ScanType;
import com.kids.domacizadatak1.jobs.ScanningJob;

import java.util.Map;
import java.util.concurrent.Future;

public interface ResultRetrieverInterface {
    public void addCorpusResult(ScanningJob job, Future<Map<String, Integer>> corpusResult);
    public Map<String, Integer> getResult(String query);
    public Map<String, Integer> queryResult(String query);
    public Map<String, Map<String, Integer>> getSummary(ScanType summaryType);
    public Map<String, Map<String, Integer>> querySummary(ScanType summaryType);
    public void clearSummary(ScanType summaryType);

}

