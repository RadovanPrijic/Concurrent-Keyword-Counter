package com.kids.domacizadatak1.components;

import com.kids.domacizadatak1.jobs.FileJob;
import com.kids.domacizadatak1.jobs.ScanType;
import com.kids.domacizadatak1.jobs.ScanningJob;
import com.kids.domacizadatak1.jobs.WebJob;

import java.util.Map;
import java.util.concurrent.*;

public class ResultRetriever implements ResultRetrieverInterface {

    private final ExecutorService service = Executors.newCachedThreadPool();
    private final Map<String, Future<Map<String, Integer>>> fileJobResultsMap = new ConcurrentHashMap<>();
    private final Map<String, Future<Map<String, Integer>>> webJobResultsMap = new ConcurrentHashMap<>();
    private final Map<String, Future<Map<String, Integer>>> webDomainResultsMap = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Integer>> fileSummaryResultsMap = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Integer>> webSummaryResultsMap = new ConcurrentHashMap<>();

    @Override
    public Map<String, Integer> getResult(String query) {
        String queryArr[] = query.split("\\|");
        String type = queryArr[0];
        String parameter = queryArr[1];

        if(type.equals("file")){
            if(fileJobResultsMap.containsKey(parameter)){
                try {
                    return fileJobResultsMap.get(parameter).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else
                System.out.println("There are no results for corpus " + parameter + ".");
        } else if (type.equals("web")){

        }

        return null;
    }

    @Override
    public Map<String, Integer> queryResult(String query) {
        String queryArr[] = query.split("\\|");
        String type = queryArr[0];
        String parameter = queryArr[1];

        if(type.equals("file")){
            if(fileJobResultsMap.containsKey(parameter)){
                if (!fileJobResultsMap.get(parameter).isDone()) {
                    System.out.println("The result for corpus " + parameter + " is still being worked on.");
                    return null;
                }
                try {
                    return fileJobResultsMap.get(parameter).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else
                System.out.println("There are no results for corpus " + parameter + ".");
        } else if (type.equals("web")){

        }
        return null;
    }

    @Override
    public Map<String, Map<String, Integer>> getSummary(ScanType summaryType) {

        return null;
    }

    @Override
    public Map<String, Map<String, Integer>> querySummary(ScanType summaryType) {

        return null;
    }

    @Override
    public void clearSummary(ScanType summaryType) {
        if (summaryType == ScanType.FILE)
            fileSummaryResultsMap.clear();
        else if (summaryType == ScanType.WEB)
            webSummaryResultsMap.clear();
    }

    @Override
    public void addCorpusResult(ScanningJob job, Future<Map<String, Integer>> corpusResult) {
        if(job.getType() == ScanType.FILE)
            fileJobResultsMap.put(((FileJob)job).getCorpusDirectoryName(), corpusResult);
        else if (job.getType() == ScanType.WEB)
            webJobResultsMap.put(((WebJob)job).getUrl(), corpusResult);
    }
}
