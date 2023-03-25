package com.kids.domacizadatak1.components;

import com.kids.domacizadatak1.CoreApp;
import com.kids.domacizadatak1.jobs.FileJob;
import com.kids.domacizadatak1.jobs.ScanType;
import com.kids.domacizadatak1.jobs.ScanningJob;
import com.kids.domacizadatak1.jobs.WebJob;

import java.util.concurrent.BlockingQueue;

public class JobDispatcher implements Runnable {

    private BlockingQueue<ScanningJob> jobQueue;
    private FileScanner fileScanner;
    private WebScanner webScanner;
    private boolean killDispatcher = false;

    public JobDispatcher(BlockingQueue<ScanningJob> jobQueue, FileScanner fileScanner, WebScanner webScanner) {
        this.jobQueue = jobQueue;
        this.fileScanner = fileScanner;
        this.webScanner = webScanner;
    }

    @Override
    public void run() {
        while (!killDispatcher) {
            try {
                ScanningJob jobToDo = jobQueue.take();

                if (jobToDo.getType() == ScanType.FILE) {
                    FileJob fileJob = (FileJob) jobToDo;
                    fileScanner.submitJobToPool(fileJob);
                    //System.out.println("Submitting file job for corpus: " + fileJob.getCorpusDirectoryName());
                } else if (jobToDo.getType() == ScanType.WEB) {
                    WebJob webJob = (WebJob) jobToDo;
                    webScanner.submitJobToPool(webJob);
                    //System.out.println("Submitting web job for corpus: " + webJob.getUrl());
                } else if (jobToDo.getType() == ScanType.POISON) {
                    fileScanner.stop();
                    webScanner.stop();
                    killDispatcher = true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Job dispatcher has been successfully shut down.");
    }
}
