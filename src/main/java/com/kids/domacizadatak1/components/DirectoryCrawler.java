package com.kids.domacizadatak1.components;


import com.kids.domacizadatak1.jobs.FileJob;
import com.kids.domacizadatak1.jobs.ScanningJob;
import org.springframework.cglib.core.Block;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class DirectoryCrawler implements Runnable {

    private CopyOnWriteArrayList<String> directoriesToCrawl;
    private BlockingQueue<ScanningJob> jobQueue;
    private HashMap<File, Long> lastModifiedValues = new HashMap<>();
    private Integer dirCrawlerSleepTime;
    private String prefix;
    private boolean doTheJob = true;

    public DirectoryCrawler(CopyOnWriteArrayList<String> directoriesToCrawl, BlockingQueue<ScanningJob> jobQueue, Integer dirCrawlerSleepTime,
                            String prefix) {
        this.directoriesToCrawl = directoriesToCrawl;
        this.jobQueue = jobQueue;
        this.dirCrawlerSleepTime = dirCrawlerSleepTime;
        this.prefix = prefix;

        this.directoriesToCrawl.add("D:\\kids-domaci-zadatak-1\\example");
    }

    @Override
    public void run() {
        while(true) {
            for(String filename : directoriesToCrawl){
                File directory = null;
                boolean readyToCrawl = true;
                try {
                    directory  = new File(filename);
                    if (!directory.exists() || !directory.canRead()){
                        readyToCrawl = false;
                        //TODO Izuzetak za nepostojeci direktorijum
                        continue;
                    }
                } catch (Exception e){
                    continue;
                }
                if(readyToCrawl) {
                    try {
                        crawlDirectory(Objects.requireNonNull(directory.listFiles()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                readyToCrawl = false;
            }
            try {
                Thread.sleep(dirCrawlerSleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void crawlDirectory(File[] directoryFiles) throws InterruptedException {
        for (File filename : directoryFiles) {
            if (filename.isDirectory()) {
                System.out.println("Directory: " + filename.getName());

                if(filename.getName().startsWith(prefix)){
                    System.err.println("CORPUS: " + filename.getName());

                    doTheJob = checkIfCorpusModified(Objects.requireNonNull(filename.listFiles()));
                    if(doTheJob){
                        try {
                            jobQueue.put(new FileJob());
                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;
                        }
                    }
                    doTheJob = true;
                } else {
                    crawlDirectory(Objects.requireNonNull(filename.listFiles()));
                }
            }
        }
    }

    public boolean checkIfCorpusModified(File[] textFiles){
        boolean modified = false;
        for (File filename : textFiles) {
            if (filename.isFile()) {
                if(!lastModifiedValues.containsKey(filename)){
                    lastModifiedValues.put(filename, filename.lastModified());
                    modified = true;
                    System.err.println("Text file: " + filename.getName());
                } else {
                    if(lastModifiedValues.get(filename) != filename.lastModified()){
                        lastModifiedValues.put(filename, filename.lastModified());
                        System.err.println("Text file: " + filename.getName());
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
