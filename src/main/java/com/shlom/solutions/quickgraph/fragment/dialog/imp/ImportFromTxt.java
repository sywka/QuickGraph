package com.shlom.solutions.quickgraph.fragment.dialog.imp;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class ImportFromTxt implements ImportDialogFragment.ImportHandler {

    @Override
    public String getMimeType() {
        return "text/plain";
    }

    @Override
    public String getName() {
        return "Из txt";
    }

    @Override
    public List<Coordinate> readFromStream(InputStream stream) {
        Scanner graphScanner = new Scanner(stream).useLocale(Locale.US);

        List<Coordinate> dataPoints = new ArrayList<>();
        while (graphScanner.hasNextFloat()) {
            dataPoints.add(new Coordinate(graphScanner.nextFloat(), graphScanner.nextFloat()));
        }
        return dataPoints;
    }
}
