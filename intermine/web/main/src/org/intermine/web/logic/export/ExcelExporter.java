package org.intermine.web.logic.export;

/*
 * Copyright (C) 2002-2008 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */
import java.util.Date;
import java.util.List;

import org.intermine.web.logic.results.Column;
import org.intermine.web.logic.results.ResultElement;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


/**
 * Export results in excel format.
 * @author Jakub Kulaviak
 **/
public class ExcelExporter implements Exporter
{

    private OutputStream out;
    private int writtenResultsCount;

    /**
     * Constructor.
     * @param out stream where the output will be written
     */
    public ExcelExporter(OutputStream out) {
        this.out = out;
    }

    /**
     * {@inheritDoc}
     */
    public void export(List<List<ResultElement>> results,
                       @SuppressWarnings("unused") List<Column> columns) {
        try {
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("results");
            ResultElementConverter converter = new ResultElementConverter();

            for (int rowIndex = 0; rowIndex < results.size(); rowIndex++) {

                HSSFRow excelRow = sheet.createRow((short) rowIndex);
                List<Object> row = converter.convert(results.get(rowIndex));

                for (short colIndex = 0; colIndex < row.size(); colIndex++) {
                    Object obj = row.get(colIndex);
                    if (obj == null) {
                        excelRow.createCell(colIndex).setCellValue("");
                        continue;
                    }

                    if (obj instanceof Number) {
                        float objectAsFloat = ((Number) obj).floatValue();
                        excelRow.createCell(colIndex).setCellValue(objectAsFloat);
                        continue;
                    }

                    if (obj instanceof Date) {
                        Date objectAsDate = (Date) obj;
                        excelRow.createCell(colIndex).setCellValue(objectAsDate);
                        continue;
                    }

                    excelRow.createCell(colIndex).setCellValue("" + obj);

                }
                writtenResultsCount++;
            }
            wb.write(out);
            out.flush();
        } catch (IOException e) {
            throw new ExportException("Export failed.", e);
        } catch (RuntimeException e) {
            throw new ExportException("Export failed.", e);
        }
    }

    /**
     * This exporter is universal and that's why method returns always true.
     * {@inheritDoc}}
     * @return always true
     */
    public boolean canExport(List<Class> clazzes) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public int getWrittenResultsCount() {
        return writtenResultsCount;
    }
}


