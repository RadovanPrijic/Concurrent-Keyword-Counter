package com.kids.domacizadatak1.components;

import com.kids.domacizadatak1.CoreApp;
import com.kids.domacizadatak1.jobs.FileJob;
import com.kids.domacizadatak1.jobs.ScanningJob;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class DirectoryCrawler implements Runnable {

    private CopyOnWriteArrayList<String> directoriesToCrawl;
    private BlockingQueue<ScanningJob> jobQueue;
    private HashMap<File, Long> lastModifiedValues;
    private boolean killCrawler = false;

    public DirectoryCrawler(CopyOnWriteArrayList<String> directoriesToCrawl, BlockingQueue<ScanningJob> jobQueue) {
        this.directoriesToCrawl = directoriesToCrawl;
        this.jobQueue = jobQueue;
        this.lastModifiedValues = new HashMap<>();
    }

    @Override
    public void run() {
        while(!killCrawler) {
            for(String directoryName : directoriesToCrawl){
                File directory = new File(directoryName);
                try {
                    crawlDirectory(directory.listFiles());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(CoreApp.dirCrawlerSleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Directory crawler has been successfully shut down.");
    }

    public void crawlDirectory(File[] directoryFiles) throws InterruptedException {
        for (File fileName : directoryFiles) {
            if (fileName.isDirectory() && fileName.canRead()) {
                //System.out.println("Directory: " + filename.getName());
                if(fileName.getName().startsWith(CoreApp.fileCorpusPrefix)){
                    //System.out.println("Corpus: " + filename.getName());
                    boolean doTheJob = checkIfCorpusModified(fileName.listFiles());
                    if(doTheJob){
                        try {
                            jobQueue.put(new FileJob(fileName));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    crawlDirectory(fileName.listFiles());
                }
            }
        }
    }

    public boolean checkIfCorpusModified(File[] textFiles){
        boolean modified = false;
        for (File fileName : textFiles) {
            if (fileName.isFile()) {
                if(!lastModifiedValues.containsKey(fileName)){
                    lastModifiedValues.put(fileName, fileName.lastModified());
                    modified = true;
                    //System.out.println("Text file added: " + filename.getName());
                } else if (lastModifiedValues.get(fileName) != fileName.lastModified()) {
                    lastModifiedValues.put(fileName, fileName.lastModified());
                    modified = true;
                    //System.out.println("Text file modified: " + filename.getName());
                }
            }
        }
        return modified;
    }

    public void setKillCrawler(boolean killCrawler) {
        this.killCrawler = killCrawler;
    }
}
