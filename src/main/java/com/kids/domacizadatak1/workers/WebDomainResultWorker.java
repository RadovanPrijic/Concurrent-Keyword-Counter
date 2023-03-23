package com.kids.domacizadatak1.workers;

import com.kids.domacizadatak1.CoreApp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public class WebDomainResultWorker implements Callable<Map<String, Map<String, Integer>>> {

    private String queryType;
    private String domainName;
    private Map<String, Future<Map<String, Integer>>> webJobResultsMap;
    private Map<String, Map<String, Integer>> webDomainResultsMap;
    private Map<String,Integer> keywordsMap;

    public WebDomainResultWorker(String queryType, String domainName, Map<String, Future<Map<String, Integer>>> webJobResultsMap,
                                    Map<String, Map<String, Integer>> webDomainResultsMap) {
        this.queryType = queryType;
        this.domainName = domainName;
        this.webJobResultsMap = webJobResultsMap;
        this.webDomainResultsMap = webDomainResultsMap;
    }

    @Override
    public Map<String, Map<String, Integer>> call() throws Exception {
        Map<String, Map<String, Integer>> resultMap = new ConcurrentHashMap<>();
        boolean flag = false;

        if(webDomainResultsMap.get(domainName) != null){
            resultMap.put(domainName, webDomainResultsMap.get(domainName));
            return resultMap;
        }

        for (String key : webJobResultsMap.keySet()) {
            try {
                URL url = new URL(key);
                String host = url.getHost();
                String extractedDomainName = host.startsWith("www.") ? host.substring(4) : host;

                if (domainName.equals(extractedDomainName)) {
                    flag = true;
                    if (queryType.equals("query")) {
                        if (!webJobResultsMap.get(key).isDone()) {
                            System.out.println("The result for corpus " + domainName + " is still being calculated.");
                            return null;
                        }
                    }
                    for (Map.Entry<String,Integer> entry : keywordsMap.entrySet()) {
                        Integer newCountValue = entry.getValue() + webJobResultsMap.get(key).get().get(entry.getKey());
                        keywordsMap.put(entry.getKey(), newCountValue);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!flag) {
            System.out.println("There is no result for corpus " + domainName + ".");
            return null;
        }
        webDomainResultsMap.put(domainName, keywordsMap);
        resultMap.put(domainName, keywordsMap);
        return resultMap;
    }

    public void makeKeywordsMap() {
        this.keywordsMap = new ConcurrentHashMap<>();
        String[] keywordsArr = CoreApp.keywords.split(",");
        for (String keyword : keywordsArr)
            keywordsMap.put(keyword, 0);
    }
}
