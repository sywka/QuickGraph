package com.shlom.solutions.quickgraph.fragment.dialog.imp;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.database.model.CoordinateModel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class ImportFromTXT extends BaseImportHandler {

    @NonNull
    @Override
    public String getMimeType() {
        return "text/plain";
    }

    @NonNull
    @Override
    public String[] getMimeTypes() {
        return new String[]{
                "text/plain"
        };
    }

    @Override
    public int getNameResource() {
        return R.string.import_from_txt;
    }

    @Override
    public List<CoordinateModel> readFromUri(Context context, Uri uri) throws Exception {
        Locale[] locales = new Locale[]{
                Locale.US,
                Locale.FRANCE,
                Locale.GERMANY
        };
        for (final Locale locale : locales) {
            List<CoordinateModel> list = readFromUri(context, uri, new InputStreamCallback() {
                @Override
                public List<CoordinateModel> readFromInputStream(InputStream inputStream) throws Exception {
                    return read(inputStream, locale);
                }
            });
            if (list != null && !list.isEmpty()) {
                return list;
            }
        }
        return null;
    }

    private List<CoordinateModel> read(InputStream inputStream, Locale locale) {
        List<CoordinateModel> dataPoints = new ArrayList<>();
        Scanner graphScanner = new Scanner(inputStream).useLocale(locale);
        try {
            while (graphScanner.hasNextFloat()) {
                dataPoints.add(new CoordinateModel()
                        .setX(graphScanner.nextFloat())
                        .setY(graphScanner.nextFloat())
                );
            }
        } finally {
            graphScanner.close();
        }
        return dataPoints;
    }
}
