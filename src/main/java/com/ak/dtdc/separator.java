package com.ak.dtdc;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class separator {
	
	private static String FILEPATH = "/home/ak/Downloads/ms.xls";
    private static int workingSheet = 0;    
    
    public separator(String filepath, int sheetNum) {
        FILEPATH = filepath;
        workingSheet = sheetNum;
    }
    
    public separator(String filepath) {
        FILEPATH = filepath;
        workingSheet = 0;
    }
    
    public static void separate(String FILEPATH) throws Exception {

    	final Workbook existingBook = Workbook.getWorkbook(new File(FILEPATH));
        final String[] sheets = existingBook.getSheetNames();
        final Sheet sheet = existingBook.getSheet(workingSheet);
        int baseRow = -1;
        final int pinCol = sheet.getColumns();
        final int phoneCol = pinCol + 1;
        int dataCol = -1;
        int row = -1;
        row = 0;
        while (true) {
            int count = 0;
            for (int i = 0; i < pinCol; ++i) {
                final String st = sheet.getCell(i, row).getContents();
                final char[] arr = st.toCharArray();
                count = 0;
                for (int j = 0; j < arr.length; ++j) {
                    if (arr[j] >= '0' && arr[j] <= '9') {
                        ++count;
                    }
                }
                if (count > 10) {
                    dataCol = i;
                    baseRow = row - 1;
                    break;
                }
            }
            if (count > 10) {
                break;
            }
            ++row;
        }
        
        
        WritableWorkbook writeBook = Workbook.createWorkbook(new File(FILEPATH), existingBook);
        WritableSheet wSheet = writeBook.getSheet(workingSheet);
        
        wSheet.addCell((WritableCell)new Label(pinCol, baseRow, "Pincode"));
        wSheet.addCell((WritableCell)new Label(phoneCol, baseRow, "Phone"));        
        
        // System.out.println("process initiated...");
        final int rows = sheet.getRows();
        for (int k = 0; k < pinCol; ++k) {
            wSheet.addCell((WritableCell)new Label(k, baseRow, sheet.getCell(k, baseRow).getContents()));
        }
        
        
        for (int l = row; l < rows; ++l) {
            final String str = sheet.getCell(dataCol, l).getContents();
            final String[] ans = getP(str);
            
            // set height for row
            // wSheet.setRowView(l, 16);
            wSheet.addCell((WritableCell)new Label(dataCol, l, sheet.getCell(dataCol, l).getContents()));
            
            wSheet.addCell((WritableCell)new Number(pinCol, l, (double)Integer.parseInt(ans[0])));
            wSheet.addCell((WritableCell)new Number(phoneCol, l, (double)Long.parseLong(ans[1])));
        }
        existingBook.close();
        writeBook.write();
        writeBook.close();
        // System.out.println("completed");
    }
    
    
    /*
     * Returns both pincode, phone after separating
     */
    public static String[] getP(String str) {
        String p = "";
        String pin = "0";
        String phn = "0";
        int count = 0;
        boolean pinStarted = false;
        for (int i = 0; i < str.length(); ++i) {
            final char c = str.charAt(i);
            if (c >= '0' && c <= '9') {
                p = String.valueOf(p) + c;
                ++count;
                pinStarted = true;
            }
            else if (pinStarted && (c == 'O' || c == 'o')) {
                p = String.valueOf(p) + '0';
                ++count;
            }
            else if (pinStarted) {
                pinStarted = false;
                p = "";
                count = 0;
            }
            if (count == 6) {
                if (i < str.length() - 1) {
                    final char next = str.charAt(i + 1);
                    if ((next < '0' || next > '9') && next != 'O') {
                        if (next != 'o') {
                            pin = p;
                            count = 0;
                            p = "";
                            pinStarted = false;
                        }
                    }
                }
                else {
                    pin = p;
                    p = "";
                    pinStarted = false;
                }
            }
            else if (count == 10) {
                if (i < str.length() - 1) {
                    final char next = str.charAt(i + 1);
                    if (next < '0' || next > '9') {
                        count = 0;
                        phn = p;
                        p = "";
                        pinStarted = false;
                    }
                }
                else {
                    phn = p;
                    p = "";
                    pinStarted = false;
                }
            }
        }
        return new String[] { pin, phn };
    }
}
