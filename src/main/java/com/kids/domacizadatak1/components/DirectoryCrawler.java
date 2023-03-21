package com.kids.domacizadatak1.components;


import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

public class DirectoryCrawler implements Runnable{

    private CopyOnWriteArrayList<String> directoryPathsToCrawl;
    private Integer dirCrawlerSleepTime;
    private String prefix;

    public DirectoryCrawler(CopyOnWriteArrayList<String> directoryPathsToCrawl, Integer dirCrawlerSleepTime, String prefix) {
        this.directoryPathsToCrawl = directoryPathsToCrawl;
        this.dirCrawlerSleepTime = dirCrawlerSleepTime;
        this.prefix = prefix;
    }

    @Override
    public void run() {

    }
}
