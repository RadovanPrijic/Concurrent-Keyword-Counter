package com.kids.domacizadatak1.jobs;

import com.kids.domacizadatak1.workers.FileScannerWorker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

public class FileJob implements ScanningJob{

    private final ScanType scanType;
    private final String query;
    private final String corpusDirectoryName;
    private List<File> textFiles;
    private ForkJoinPool forkJoinPool;

    public FileJob(File corpusDirectory) {
        this.scanType = ScanType.FILE;
        this.query = "file|" + corpusDirectory.getName();
        this.corpusDirectoryName = corpusDirectory.getName();
        setFilesToAnalyse(corpusDirectory);
    }

    @Override
    public Future<Map<String, Integer>> initiate() {
        //System.out.println("Starting file scan for file|" + corpusDirectoryName);
        return forkJoinPool.submit(new FileScannerWorker(textFiles));
    }

    public void setFilesToAnalyse(File corpusDirectory) {
        this.textFiles = new CopyOnWriteArrayList<>();
        for (File file : corpusDirectory.listFiles()) {
            if (file.isFile() && file.canRead())
                textFiles.add(file);
            else
                System.err.println("File " + file + " could not be read.");
        }
    }

    @Override
    public ScanType getType() {
        return this.scanType;
    }

    @Override
    public String getQuery() {
        return this.query;
    }

    public String getCorpusDirectoryName() {
        return corpusDirectoryName;
    }

    public void setForkJoinPool(ForkJoinPool forkJoinPool) {
        this.forkJoinPool = forkJoinPool;
    }
}
