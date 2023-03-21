package com.kids.domacizadatak1;

import com.kids.domacizadatak1.threads.DirectoryCrawler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

@SpringBootApplication
public class DomaciZadatak1Application {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(DomaciZadatak1Application.class, args);

        HashMap<String, Integer> keywordsMap = new HashMap<>();
        String fileCorpusPrefix = null;
        Integer dirCrawlerSleepTime = null;
        Integer fileScanningSizeLimit = null;
        Integer hopCount = null;
        Integer urlRefreshTime = null;

        File file = new File("D:\\kids-domaci-zadatak-1\\src\\main\\resources\\application.properties");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null){
            if(line.startsWith("keywords=")){
                String keywordsArr = line.split("=")[1];
                String[] keywords = keywordsArr.split(",");
                for (String keyword : keywords)
                    keywordsMap.put(keyword, 0);
            } else if (line.startsWith("file.corpus.prefix=")){
                fileCorpusPrefix = line.split("=")[1];
            } else if (line.startsWith("dir.crawler.sleep.time=")){
                dirCrawlerSleepTime = Integer.parseInt(line.split("=")[1]);
            } else if (line.startsWith("file.scanning.size.limit=")){
                fileScanningSizeLimit = Integer.parseInt(line.split("=")[1]);
            } else if (line.startsWith("hop.count=")){
                hopCount = Integer.parseInt(line.split("=")[1]);
            } else if (line.startsWith("url.refresh.time="))
                urlRefreshTime = Integer.parseInt(line.split("=")[1]);
        }
        System.out.println(keywordsMap);
        System.out.println(fileCorpusPrefix);
        System.out.println(dirCrawlerSleepTime);
        System.out.println(fileScanningSizeLimit);
        System.out.println(hopCount);
        System.out.println(urlRefreshTime);

        while(true) {
            Scanner scanner = new Scanner(System.in);
            String userInput = scanner.nextLine();

            if(userInput.startsWith("ad ")){

            } else if (userInput.startsWith("aw ")){

            } else if (userInput.startsWith("get file|") || userInput.startsWith("get web|")){

            } else if (userInput.startsWith("query file|") || userInput.startsWith("query web|")){

            } else if (userInput.equals("cfs")){

            } else if (userInput.equals("cws")){

            } else if (userInput.equals("stop")){
                //TODO Pravilno obustavljanje programa (zatvaranje thread pool-ova, itd.)
                System.exit(0);
            } else
                System.out.println("Uneli ste nepostojecu komandu.");
        }
    }
}
