package com.kids.domacizadatak1.components;

import com.kids.domacizadatak1.CoreApp;
import com.kids.domacizadatak1.jobs.FileJob;

import java.util.Map;
import java.util.concurrent.*;

public class FileScanner implements Runnable {

    private BlockingQueue<FileJob> fileScannerJobQueue;
    private final ExecutorService fileScannerThreadPool;
    private final ExecutorCompletionService<Map<String, Integer>> fileScannerResults;

    public FileScanner(BlockingQueue<FileJob> fileScannerJobQueue) {
        this.fileScannerJobQueue = fileScannerJobQueue;
        this.fileScannerThreadPool = new ForkJoinPool();
        this.fileScannerResults = new ExecutorCompletionService<>(this.fileScannerThreadPool);
    }

    @Override
    public void run() {
        while (true) {
            try {
                FileJob fileJob = this.fileScannerJobQueue.take();
                fileJob.setForkJoinPool((ForkJoinPool) fileScannerThreadPool);
                Future<Map<String, Integer>> fileJobResult = fileJob.initiate();
                CoreApp.getResultRetriever().addCorpusResult(fileJob, fileJobResult);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
