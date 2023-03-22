package com.kids.domacizadatak1.jobs;

import java.util.Map;
import java.util.concurrent.Future;

public class WebJob implements ScanningJob {

    @Override
    public ScanType getType() {
        return null;
    }

    @Override
    public String getQuery() {
        return null;
    }

    @Override
    public Future<Map<String, Integer>> initiate() {
        return null;
    }
}
