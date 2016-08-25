package com.shlom.solutions.quickgraph.fragment.dialog.imp;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImportFromExcel implements ImportDialogFragment.ImportHandler {

    @Override
    public String getMimeType() {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    @Override
    public String getName() {
        return "Из xlsx";
    }

    @Override
    public List<Coordinate> readFromStream(InputStream stream) {
        return new ArrayList<>();
    }
}
