package com.kids.domacizadatak1.jobs;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Future;

public class PoisonPill implements ScanningJob{
    private final ScanType scanType;

    public PoisonPill() {
        this.scanType = ScanType.POISON;
    }

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
