package com.shlom.solutions.quickgraph.imp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class ImportFromTxt implements ImportManager.ImportHandler {

    @Override
    public String getMimeType() {
        return "text/plain";
    }

    @Override
    public String getName() {
        return "Из txt";
    }

    @Override
    public List<Coordinate> readFromFile(File file) {
        try {
            Scanner graphScanner = new Scanner(file).useLocale(Locale.US);

            List<Coordinate> dataPoints = new ArrayList<>();
            while (graphScanner.hasNextFloat()) {
                dataPoints.add(new Coordinate(graphScanner.nextFloat(), graphScanner.nextFloat()));
            }
            return dataPoints;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
