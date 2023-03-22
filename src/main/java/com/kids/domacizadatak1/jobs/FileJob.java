package com.kids.domacizadatak1.jobs;

import com.kids.domacizadatak1.workers.FileScannerWorker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

public class FileJob implements ScanningJob{

    private ScanType scanType;
    private String query;
    private File corpusDirectory;
    private String keywords;
    private Integer fileScanningSizeLimit;
    private List<File> textFiles;
    private Future<Map<String,Integer>> fileJobResult;
    private ForkJoinPool forkJoinPool;

    public FileJob(File corpusDirectory) {
        this.scanType = ScanType.FILE;
        this.corpusDirectory = corpusDirectory;
        this.query = "file|" + corpusDirectory.getName();
        setFilesToAnalyse();
    }

    @Override
    public Future<Map<String,Integer>> initiate() {
        fileJobResult = this.forkJoinPool.submit(new FileScannerWorker(textFiles, keywords, fileScanningSizeLimit));
        try {
            System.out.println(fileJobResult.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return fileJobResult;
    }

    public void setFilesToAnalyse() {
        this.textFiles = new ArrayList<>();
        for (File file : Objects.requireNonNull(corpusDirectory.listFiles())) {
            if (file.isFile() && file.canRead())
                textFiles.add(file);
            //TODO Greska ako je nesto pogresno kod citanja fajla
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

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public void setFileScanningSizeLimit(Integer fileScanningSizeLimit) {
        this.fileScanningSizeLimit = fileScanningSizeLimit;
    }

    public void setForkJoinPool(ForkJoinPool forkJoinPool) {
        this.forkJoinPool = forkJoinPool;
    }
}
