package com.kids.domacizadatak1.workers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class WebScannerWorker implements Callable<Map<String,Integer>> {

    private String keywords;
    private String url;
    private Integer hopCount;
    private Integer urlRefreshTime;
    private Map<String,Integer> keywordsMap;

    public WebScannerWorker(String keywords, String url, Integer hopCount, Integer urlRefreshTime) {
        this.keywords = keywords;
        this.url = url;
        this.hopCount = hopCount;
        this.urlRefreshTime = urlRefreshTime;
        makeKeywordsMap();
    }

    @Override
    public Map<String, Integer> call() throws Exception {

        return keywordsMap;
    }

    public void makeKeywordsMap() {
        this.keywordsMap = new ConcurrentHashMap<>();
        String[] keywordsArr = keywords.split(",");
        for (String keyword : keywordsArr)
            keywordsMap.put(keyword, 0);
    }
}
