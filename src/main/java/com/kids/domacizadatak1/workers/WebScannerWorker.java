package com.kids.domacizadatak1.workers;

import com.kids.domacizadatak1.CoreApp;
import com.kids.domacizadatak1.jobs.ScanningJob;
import com.kids.domacizadatak1.jobs.WebJob;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class WebScannerWorker implements Callable<Map<String,Integer>> {

    private String url;
    private Integer hopCount;
    private Map<String,Integer> keywordsMap;
    private BlockingQueue<ScanningJob> jobQueue;
    private ConcurrentHashMap<String, Long> urlCache;

    public WebScannerWorker(String url, Integer hopCount, BlockingQueue<ScanningJob> jobQueue, ConcurrentHashMap<String, Long> urlCache) {
        this.url = url;
        this.hopCount = hopCount;
        this.jobQueue = jobQueue;
        this.urlCache = urlCache;
        makeKeywordsMap();
    }

    @Override
    public Map<String, Integer> call() throws Exception {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements links = doc.select("a[href]");;

            String words[] = doc.text().split("\\s+");
            for (String word : words) {
                if(keywordsMap.containsKey(word)){
                    keywordsMap.put(word, keywordsMap.get(word) + 1);
                }
            }

            if (hopCount > 0) {
                for (Element link : links) {
                    String extractedUrl = link.attr("abs:href").trim();
                    extractedUrl.replaceAll(" ", "%20");
                    System.err.println("This is the URL: " + extractedUrl);

                    if ((urlCache.contains(extractedUrl) && System.currentTimeMillis() - urlCache.get(extractedUrl) < CoreApp.urlRefreshTime) ||
                            !(extractedUrl.startsWith("http")) || !(extractedUrl.startsWith("https")) || extractedUrl.endsWith(".pdf") ||
                            extractedUrl.endsWith(".jpg") || extractedUrl.endsWith(".png") || extractedUrl.endsWith(".php") ||
                            extractedUrl.endsWith(".js") || extractedUrl.isBlank())
                        continue;
                    else {
                        urlCache.put(extractedUrl, System.currentTimeMillis());
                        jobQueue.add(new WebJob(extractedUrl, hopCount - 1));
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            //System.out.println(ex);
        }
        return keywordsMap;
    }

    public void makeKeywordsMap() {
        this.keywordsMap = new ConcurrentHashMap<>();
        String[] keywordsArr = CoreApp.keywords.split(",");
        for (String keyword : keywordsArr)
            keywordsMap.put(keyword, 0);
    }
}
