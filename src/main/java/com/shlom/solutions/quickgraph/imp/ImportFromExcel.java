package com.shlom.solutions.quickgraph.imp;

import java.io.File;
import java.util.List;

public class ImportFromExcel implements ImportManager.ImportHandler {

    @Override
    public String getMimeType() {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    @Override
    public String getName() {
        return "Из xlsx";
    }

    @Override
    public List<Coordinate> readFromFile(File file) {
        return null;
    }
}
