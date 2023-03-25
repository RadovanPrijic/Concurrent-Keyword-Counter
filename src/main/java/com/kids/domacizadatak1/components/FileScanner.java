package com.kids.domacizadatak1.components;

import com.kids.domacizadatak1.CoreApp;
import com.kids.domacizadatak1.jobs.FileJob;

import java.util.Map;
import java.util.concurrent.*;

public class FileScanner {

    private final ExecutorService fileScannerThreadPool;
    private ResultRetriever resultRetriever;

    public FileScanner(ResultRetriever resultRetriever) {
        this.fileScannerThreadPool = new ForkJoinPool();
        this.resultRetriever = resultRetriever;
    }

    public void submitJobToPool (FileJob fileJob){
        fileJob.setForkJoinPool((ForkJoinPool) fileScannerThreadPool);
        Future<Map<String, Integer>> fileJobResult = fileJob.initiate();
        resultRetriever.addCorpusResult(fileJob, fileJobResult);
    }

    public void stop(){
        fileScannerThreadPool.shutdown();
        System.out.println("File scanner thread pool has been successfully shut down.");
    }
}
