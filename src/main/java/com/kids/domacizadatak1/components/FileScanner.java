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
    private String keywords;
    private Integer fileScanningSizeLimit;

    public FileScanner(BlockingQueue<FileJob> fileScannerJobQueue, BlockingQueue<Result> resultQueue, String keywords, Integer fileScanningSizeLimit) {
        this.fileScannerJobQueue = fileScannerJobQueue;
        this.resultQueue = resultQueue;
        this.fileScannerThreadPool = new ForkJoinPool();
        this.fileScannerResults = new ExecutorCompletionService<>(this.fileScannerThreadPool);
        this.keywords = keywords;
        this.fileScanningSizeLimit = fileScanningSizeLimit;
    }

    @Override
    public void run() {
        while (true) {
            try {
                FileJob fileJob = this.fileScannerJobQueue.take();
                fileJob.setKeywords(keywords);
                fileJob.setFileScanningSizeLimit(fileScanningSizeLimit);
                System.out.println(fileJob.getQuery());
                fileJob.initiate(this.fileScannerResults);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //TODO Thread pool shutdown
    }

}
