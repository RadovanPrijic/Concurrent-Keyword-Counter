package com.kids.domacizadatak1.components;

import com.kids.domacizadatak1.jobs.FileJob;
import com.kids.domacizadatak1.jobs.ScanType;
import com.kids.domacizadatak1.jobs.ScanningJob;
import com.kids.domacizadatak1.jobs.WebJob;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class JobDispatcher implements Runnable {

    private BlockingQueue<ScanningJob> jobQueue;
    private BlockingQueue<FileJob> fileScannerJobQueue;
    private BlockingQueue<WebJob> webScannerJobQueue;

    public JobDispatcher(BlockingQueue<ScanningJob> jobQueue, BlockingQueue<FileJob> fileScannerJobQueue, BlockingQueue<WebJob> webScannerJobQueue) {
        this.jobQueue = jobQueue;
        this.fileScannerJobQueue = fileScannerJobQueue;
        this.webScannerJobQueue = webScannerJobQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ScanningJob jobToDo = jobQueue.take();

                if (jobToDo.getType() == ScanType.FILE) {
                    FileJob fileJob = (FileJob) jobToDo;
                    fileScannerJobQueue.put(fileJob);
                } else if (jobToDo.getType() == ScanType.WEB) {
                    WebJob webJob = (WebJob) jobToDo;
                    webScannerJobQueue.add(webJob);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
