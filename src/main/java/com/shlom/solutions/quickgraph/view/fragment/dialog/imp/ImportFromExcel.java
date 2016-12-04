package com.shlom.solutions.quickgraph.view.fragment.dialog.imp;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.shlom.solutions.quickgraph.R;
import com.shlom.solutions.quickgraph.model.database.dbmodel.CoordinateModel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ImportFromExcel extends BaseImportHandler {

    @NonNull
    @Override
    public String getMimeType() {
        return "application/vnd.ms-excel";
    }

    @NonNull
    @Override
    public String[] getMimeTypes() {
        return new String[]{
                "application/vnd.ms-excel",
//                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        };
    }

    @Override
    public int getNameResource() {
        return R.string.import_from_excel;
    }

    @Override
    public List<CoordinateModel> readFromUri(Context context, Uri uri) throws Exception {
        return readFromUri(context, uri, inputStream -> read(new HSSFWorkbook(inputStream)));
    }

    private List<CoordinateModel> read(Workbook workbook) {
        List<CoordinateModel> dataPoints = new ArrayList<>();
        if (workbook.getNumberOfSheets() > 0) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                short i = 0;
                CoordinateModel coordinateModel = new CoordinateModel();
                Iterator<Cell> cellIterator = row.cellIterator();
                if (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        coordinateModel.setX((float) cell.getNumericCellValue());
                        i++;
                    }
                }
                if (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        coordinateModel.setY((float) cell.getNumericCellValue());
                        i++;
                    }
                }
                if (i == 2) {
                    dataPoints.add(coordinateModel);
                }
            }
        }
        return dataPoints;
    }
}
