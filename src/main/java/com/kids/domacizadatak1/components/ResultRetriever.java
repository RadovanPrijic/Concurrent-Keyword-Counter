package com.kids.domacizadatak1.components;

import com.kids.domacizadatak1.jobs.FileJob;
import com.kids.domacizadatak1.jobs.ScanType;
import com.kids.domacizadatak1.jobs.ScanningJob;
import com.kids.domacizadatak1.jobs.WebJob;
import com.kids.domacizadatak1.workers.SummaryResultWorker;
import com.kids.domacizadatak1.workers.WebDomainResultWorker;
import com.kids.domacizadatak1.workers.WebScannerWorker;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.*;

public class ResultRetriever implements ResultRetrieverInterface {

    private final ExecutorService resultRetrieverThreadPool = Executors.newCachedThreadPool();
    private final ExecutorCompletionService<Map<String, Map<String, Integer>>> resultRetrieverCompletionService = new ExecutorCompletionService(resultRetrieverThreadPool);
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
                    System.err.println("The result for corpus " + parameter + " is still being calculated.");
                    return null;
                }
                try {
                    return fileJobResultsMap.get(parameter).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else
                System.err.println("There is no result for corpus " + parameter + ".");
        } else if (queryType.equals("web")){
            if(webDomainResultsMap.get(parameter) != null)
                return webDomainResultsMap.get(parameter);

            Future<Map<String, Map<String, Integer>>> webJobResult =
                    this.resultRetrieverCompletionService.submit(new WebDomainResultWorker(commandType, parameter,
                                                                                                        webJobResultsMap, webDomainResultsMap));
            if(commandType.equals("get") || (commandType.equals("query")) && webJobResult.isDone()){
                try {
                    if(webJobResult.get().get(parameter) != null)
                        return webJobResult.get().get(parameter);
                    else {
                        System.err.println("The result for corpus " + parameter + " is still being calculated.");
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public Map<String, Map<String, Integer>> retrieveSummary(String commandType, ScanType summaryType) {
        if(summaryType == ScanType.FILE && !fileSummaryResultsMap.isEmpty())
            return fileSummaryResultsMap;
        if(summaryType == ScanType.WEB && !webSummaryResultsMap.isEmpty())
            return webSummaryResultsMap;

        Future<Map<String, Map<String, Integer>>> jobResult =
                this.resultRetrieverCompletionService.submit(new SummaryResultWorker(commandType, summaryType, fileJobResultsMap,
                        webJobResultsMap, webDomainResultsMap, fileSummaryResultsMap, webSummaryResultsMap));
        
        if(commandType.equals("get") || (commandType.equals("query")) && jobResult.isDone()){
            try {
                if(jobResult.get() != null)
                    return jobResult.get();
                else {
                    System.err.println("The requested summary could not be provided because some of the results are still being calculated.");
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    public void stop(){
        resultRetrieverThreadPool.shutdown();
        System.out.println("Result retriever thread pool has been successfully shut down.");
    }
}
