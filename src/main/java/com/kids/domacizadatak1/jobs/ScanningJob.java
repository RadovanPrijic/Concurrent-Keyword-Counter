package com.kids.domacizadatak1.jobs;

import java.util.Map;
import java.util.concurrent.Future;

public interface ScanningJob {
    ScanType getType();
    String getQuery();
    Future<Map<String, Integer>> initiate();
}

