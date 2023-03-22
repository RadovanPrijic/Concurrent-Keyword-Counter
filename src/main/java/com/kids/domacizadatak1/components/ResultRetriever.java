package com.kids.domacizadatak1.components;

import com.kids.domacizadatak1.results.Result;

import java.util.concurrent.BlockingQueue;

public class ResultRetriever implements Runnable {

    private BlockingQueue<Result> resultQueue;

    public ResultRetriever(BlockingQueue<Result> resultQueue) {
        this.resultQueue = resultQueue;
    }

    @Override
    public void run() {

    }
}
