package com.kids.domacizadatak1.workers;

import com.kids.domacizadatak1.jobs.ScanType;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class SummaryResultWorker implements Callable<Map<String, Map<String, Integer>>> {

    private String queryType;
    private ScanType summaryType;
    private Map<String, Future<Map<String, Integer>>> fileJobResultsMap;
    private Map<String, Future<Map<String, Integer>>> webJobResultsMap;
    private Map<String, Map<String, Integer>> webDomainResultsMap;
    private Map<String, Map<String, Integer>> fileSummaryResultsMap;
    private Map<String, Map<String, Integer>> webSummaryResultsMap;

    public SummaryResultWorker(String queryType, ScanType summaryType,
                               Map<String, Future<Map<String, Integer>>> fileJobResultsMap,
                               Map<String, Future<Map<String, Integer>>> webJobResultsMap,
                               Map<String, Map<String, Integer>> webDomainResultsMap,
                               Map<String, Map<String, Integer>> fileSummaryResultsMap,
                               Map<String, Map<String, Integer>> webSummaryResultsMap) {
        this.queryType = queryType;
        this.summaryType = summaryType;
        this.fileJobResultsMap = fileJobResultsMap;
        this.webJobResultsMap = webJobResultsMap;
        this.webDomainResultsMap = webDomainResultsMap;
        this.fileSummaryResultsMap = fileSummaryResultsMap;
        this.webSummaryResultsMap = webSummaryResultsMap;
    }

    @Override
    public Map<String, Map<String, Integer>> call() throws Exception {
        if(summaryType == ScanType.FILE){
            if(!fileSummaryResultsMap.isEmpty())
                return fileSummaryResultsMap;

            if(queryType.equals("query")){
                for(String key : fileJobResultsMap.keySet()){
                    if (!fileJobResultsMap.get(key).isDone()) {
                        System.out.println("The requested summary could not be provided because some of the results are still being calculated.");
                        return null;
                    }
                    fileSummaryResultsMap.put(key, fileJobResultsMap.get(key).get());
                }
            } else if(queryType.equals("get")){
                for(String key : fileJobResultsMap.keySet()){
                    fileSummaryResultsMap.put(key, fileJobResultsMap.get(key).get());
                }
            }
            return fileSummaryResultsMap;

        } else if (summaryType == ScanType.WEB){
            if(!webSummaryResultsMap.isEmpty())
                return  webSummaryResultsMap;

            for (String key : webJobResultsMap.keySet()) {
                try {
                    URL url = new URL(key);
                    String host = url.getHost();
                    String extractedDomainName = host.startsWith("www.") ? host.substring(4) : host;

                    if (webSummaryResultsMap.get(extractedDomainName) == null) {
                        if (queryType.equals("query")) {
                            if (!webJobResultsMap.get(key).isDone()) {
                                System.out.println("The requested summary could not be provided because some of the results are still being calculated.");
                                return null;
                            }
                        }
                        webSummaryResultsMap.put(extractedDomainName, webJobResultsMap.get(key).get());
                    } else {
                        Map<String, Integer> domainKeywordMap = webSummaryResultsMap.get(extractedDomainName);
                        for (Map.Entry<String,Integer> entry : domainKeywordMap.entrySet()) {
                            Integer newCountValue = entry.getValue() + webJobResultsMap.get(key).get().get(entry.getKey());
                            domainKeywordMap.put(entry.getKey(), newCountValue);
                        }
                        webSummaryResultsMap.put(extractedDomainName, domainKeywordMap);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return webSummaryResultsMap;
        }
        return null;
    }
}
