package com.kids.domacizadatak1.workers;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveTask;

public class FileScannerWorker extends RecursiveTask<Map<String,Integer>> {

    private List<File> textFiles;
    private String keywords;
    private Integer fileScanningSizeLimit;
    private Map<String,Integer> keywordsMap;

    public FileScannerWorker(List<File> textFiles, String keywords, Integer fileScanningSizeLimit) {
        this.textFiles = textFiles;
        this.keywords = keywords;
        this.fileScanningSizeLimit = fileScanningSizeLimit;
        makeKeywordsMap();
    }

    @Override
    protected Map<String, Integer> compute() {
        List<File> breakawayFiles = divideFileList(textFiles);

        if(textFiles.size() == 0){
            countKeywords(breakawayFiles);
            return keywordsMap;
        } else {
            FileScannerWorker left = new FileScannerWorker(textFiles, keywords, fileScanningSizeLimit);
            FileScannerWorker right = new FileScannerWorker(breakawayFiles, keywords, fileScanningSizeLimit);

            left.fork();
            Map<String,Integer> leftKeywordCountMap = right.compute();
            Map<String,Integer> rightKeywordCountMap = left.join();

            for (Map.Entry<String,Integer> entry : keywordsMap.entrySet()) {
                Integer newCountValue = entry.getValue() + leftKeywordCountMap.get(entry.getKey()) + rightKeywordCountMap.get(entry.getKey());
                keywordsMap.put(entry.getKey(), newCountValue);
            }
            return keywordsMap;
        }
    }

    public void countKeywords(List<File> files){
        for (File filename : files) {
            //System.err.println("File to analyse: " + filename.getName());
            String line;
            FileReader file;
            try {
                file = new FileReader(filename.getAbsolutePath());
                BufferedReader br = new BufferedReader(file);

                while ((line = br.readLine()) != null) {
                    String words[] = line.split("\\s+");
                    for (String word : words) {
                        if (keywordsMap.containsKey(word))
                            keywordsMap.put(word, keywordsMap.get(word) + 1);
                    }
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<File> divideFileList(List<File> textFiles) {
        List<File> breakawayFiles = new ArrayList<>();
        int byteSum = 0;

        for (File textFile : textFiles) {
            breakawayFiles.add(textFile);
            byteSum += textFile.length();

            if (byteSum > fileScanningSizeLimit){
                break;
            }
        }
        textFiles.removeAll(breakawayFiles);
        return breakawayFiles;
    }

    public void makeKeywordsMap() {
        this.keywordsMap = new ConcurrentHashMap<>();
        String[] keywordsArr = keywords.split(",");
        for (String keyword : keywordsArr)
            keywordsMap.put(keyword, 0);
    }
}
