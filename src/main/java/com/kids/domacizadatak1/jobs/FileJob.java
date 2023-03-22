package com.kids.domacizadatak1.jobs;

import com.kids.domacizadatak1.workers.FileScannerWorker;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

public class FileJob implements ScanningJob{

    private ScanType scanType;
    private String query;
    private File corpusDirectory;
    private ArrayList<File> textFiles;
    private Future<Map<String,Integer>> fileJobResult;

    public FileJob(File corpusDirectory) {
        this.scanType = ScanType.FILE;
        this.corpusDirectory = corpusDirectory;
        this.query = "file|" + corpusDirectory.getName();
        setFilesToAnalyse();
    }

    @Override
    public ScanType getType() {
        return this.scanType;
    }

    @Override
    public String getQuery() {
        return this.query;
    }

    @Override
    public Future<Map<String,Integer>> initiate(ExecutorCompletionService executorCompletionService) {
        fileJobResult = executorCompletionService.submit((Callable) new FileScannerWorker());
        return fileJobResult;
    }

    public void setFilesToAnalyse(){
        this.textFiles = new ArrayList<>();
        for (File file : Objects.requireNonNull(corpusDirectory.listFiles())) {
            if(file.isFile())
                textFiles.add(file);
        }
    }
}
