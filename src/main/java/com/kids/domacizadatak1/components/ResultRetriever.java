package com.kids.domacizadatak1.components;

import com.kids.domacizadatak1.jobs.FileJob;
import com.kids.domacizadatak1.jobs.ScanType;
import com.kids.domacizadatak1.jobs.ScanningJob;
import com.kids.domacizadatak1.jobs.WebJob;
import com.kids.domacizadatak1.workers.WebDomainResultWorker;
import com.kids.domacizadatak1.workers.WebScannerWorker;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.*;

public class ResultRetriever implements ResultRetrieverInterface {

    private final ExecutorService resultRetrieverThreadPool = Executors.newCachedThreadPool();
    private final Map<String, Future<Map<String, Integer>>> fileJobResultsMap = new ConcurrentHashMap<>();
    private final Map<String, Future<Map<String, Integer>>> webJobResultsMap = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Integer>> webDomainResultsMap = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Integer>> fileSummaryResultsMap = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Integer>> webSummaryResultsMap = new ConcurrentHashMap<>();

    @Override
    public Map<String, Integer> retrieveResult(String commandType, String query) {
        String[] queryArr = query.split("\\|");
        String queryType = queryArr[0];
        String parameter = queryArr[1];

        if(queryType.equals("file")){
            if(fileJobResultsMap.containsKey(parameter)){
                if (commandType.equals("query") && !fileJobResultsMap.get(parameter).isDone()) {
                    System.out.println("The result for corpus " + parameter + " is still being calculated.");
                    return null;
                }
                try {
                    return fileJobResultsMap.get(parameter).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else
                System.out.println("There is no result for corpus " + parameter + ".");
        } else if (queryType.equals("web")){
            Future<Map<String, Map<String, Integer>>> webJobResult =
                    this.resultRetrieverThreadPool.submit(new WebDomainResultWorker("get", parameter,
                                                                                            webJobResultsMap, webDomainResultsMap));
            try {
                return webJobResult.get().get(parameter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Map<String, Map<String, Integer>> retrieveSummary(String commandType, ScanType summaryType) {

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
        else if (job.getType() == ScanType.WEB) {
            String urlValue = ((WebJob) job).getUrl();
            webJobResultsMap.put(urlValue, corpusResult);

            URL url;
            String host = null;
            try {
                url = new URL(urlValue);
                host = url.getHost();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            String extractedDomainName = host.startsWith("www.") ? host.substring(4) : host;

            if(webDomainResultsMap.containsKey(extractedDomainName)){
                webDomainResultsMap.put(extractedDomainName, null);
            }
        }
    }
}
