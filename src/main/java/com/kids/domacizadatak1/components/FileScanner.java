package com.kids.domacizadatak1.components;

import com.kids.domacizadatak1.jobs.FileJob;
import com.kids.domacizadatak1.results.Result;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

public class FileScanner implements Runnable {

    private BlockingQueue<FileJob> fileScannerJobQueue;
    private BlockingQueue<Result> resultQueue;
    private ExecutorService fileScannerThreadPool;
    private ExecutorCompletionService<Map<String, Integer>> fileScannerResults;

    public FileScanner(BlockingQueue<FileJob> fileScannerJobQueue, BlockingQueue<Result> resultQueue) {
        this.fileScannerJobQueue = fileScannerJobQueue;
        this.resultQueue = resultQueue;
        this.fileScannerThreadPool = new ForkJoinPool();
        this.fileScannerResults = new ExecutorCompletionService<>(this.fileScannerThreadPool);
    }

    @Override
    public void run() {
        while (true) {
            try {
                FileJob fileJob = this.fileScannerJobQueue.take();
                System.out.println(fileJob.getQuery());
                //fileJob.initiate(this.fileScannerResults);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //TODO Thread pool shutdown
    }

}
