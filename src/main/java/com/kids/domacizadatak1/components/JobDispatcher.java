package com.kids.domacizadatak1.components;

import com.kids.domacizadatak1.CoreApp;
import com.kids.domacizadatak1.jobs.FileJob;
import com.kids.domacizadatak1.jobs.ScanType;
import com.kids.domacizadatak1.jobs.ScanningJob;
import com.kids.domacizadatak1.jobs.WebJob;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class JobDispatcher implements Runnable {

    private BlockingQueue<ScanningJob> jobQueue;
    private boolean killDispatcher = false;

    public JobDispatcher(BlockingQueue<ScanningJob> jobQueue) {
        this.jobQueue = jobQueue;
    }

    @Override
    public void run() {
        while (!killDispatcher) {
            try {
                ScanningJob jobToDo = jobQueue.take();

                if (jobToDo.getType() == ScanType.FILE) {
                    FileJob fileJob = (FileJob) jobToDo;
                    CoreApp.getFileScanner().submitJobToPool(fileJob);
                } else if (jobToDo.getType() == ScanType.WEB) {
                    WebJob webJob = (WebJob) jobToDo;
                    CoreApp.getWebScanner().submitJobToPool(webJob);
                } else if (jobToDo.getType() == ScanType.POISON) {
                    CoreApp.getFileScanner().getFileScannerThreadPool().shutdown();
                    CoreApp.getWebScanner().getWebScannerThreadPool().shutdown();
                    CoreApp.getResultRetriever().getResultRetrieverThreadPool().shutdown();
                    killDispatcher = true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("All thread pools have been successfully shut down.");
        System.out.println("Job dispatcher has been successfully shut down.");
    }
}
