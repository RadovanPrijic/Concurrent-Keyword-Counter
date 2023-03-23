package com.kids.domacizadatak1.components;

import com.kids.domacizadatak1.jobs.ScanType;
import com.kids.domacizadatak1.jobs.ScanningJob;

import java.util.Map;
import java.util.concurrent.Future;

public interface ResultRetrieverInterface {
    public void addCorpusResult(ScanningJob job, Future<Map<String, Integer>> corpusResult);
    public Map<String, Integer> retrieveResult(String commandType, String query);
    public Map<String, Map<String, Integer>> retrieveSummary(String commandType, ScanType summaryType);
    public void clearSummary(ScanType summaryType);

}

