package com.kids.domacizadatak1.components;

import com.kids.domacizadatak1.CoreApp;
import com.kids.domacizadatak1.jobs.FileJob;

import java.util.Map;
import java.util.concurrent.*;

public class FileScanner {

    private final ExecutorService fileScannerThreadPool;

    public FileScanner() {
        this.fileScannerThreadPool = new ForkJoinPool();
    }

    public void submitJobToPool (FileJob fileJob){
        fileJob.setForkJoinPool((ForkJoinPool) fileScannerThreadPool);
        Future<Map<String, Integer>> fileJobResult = fileJob.initiate();
        CoreApp.getResultRetriever().addCorpusResult(fileJob, fileJobResult);
    }

    public ExecutorService getFileScannerThreadPool() {
        return fileScannerThreadPool;
    }
}
