package com.shlom.solutions.quickgraph.fragment.dialog.imp;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.database.model.CoordinateModel;
import com.shlom.solutions.quickgraph.etc.LogUtil;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class ImportFromCSV extends BaseImportHandler {

    @NonNull
    @Override
    public String getMimeType() {
        return "text/*";
    }

    @NonNull
    @Override
    public String[] getMimeTypes() {
        return new String[]{
                "text/csv",
                "text/comma-separated-values"
        };
    }

    @Override
    public int getNameResource() {
        return R.string.import_from_csv;
    }

    @Override
    public List<CoordinateModel> readFromUri(Context context, Uri uri) throws Exception {
        CSVFormat[] csvFormats = new CSVFormat[]{
                CSVFormat.DEFAULT.withDelimiter(','),
                CSVFormat.DEFAULT.withDelimiter(';'),
                CSVFormat.EXCEL.withDelimiter(','),
                CSVFormat.EXCEL.withDelimiter(';')
        };

        for (final CSVFormat csvFormat : csvFormats) {
            List<CoordinateModel> list = readFromUri(context, uri, inputStream -> read(inputStream, csvFormat));
            if (list != null && !list.isEmpty()) {
                return list;
            }
        }
        return null;
    }

    private List<CoordinateModel> read(InputStream inputStream, CSVFormat csvFormat) throws Exception {
        List<CoordinateModel> list = new ArrayList<>();
        Reader reader = new InputStreamReader(inputStream);
        CSVParser parser = new CSVParser(reader, csvFormat);
        try {
            for (CSVRecord record : parser) {
                if (record.size() > 1) {
                    String x = record.get(0);
                    String y = record.get(1);
                    try {
                        list.add(new CoordinateModel()
                                .setX(Float.valueOf(x))
                                .setY(Float.valueOf(y))
                        );
                    } catch (NumberFormatException ignore) {
                    }
                }
            }
        } finally {
            try {
                reader.close();
                parser.close();
            } catch (IOException e) {
                LogUtil.d(e);
            }
        }
        return list;
    }
}
