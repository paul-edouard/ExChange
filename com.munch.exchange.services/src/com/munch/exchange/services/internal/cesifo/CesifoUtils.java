package com.munch.exchange.services.internal.cesifo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import com.munch.exchange.model.tool.DateTool;

public class CesifoUtils {
	
	public static String httpPath="http://www.cesifo-group.de/de/dms/ifodoc/lr/";
	
	
	public static String downloadXlsFile(String docKey,String filePath){
		Calendar date=Calendar.getInstance();
		String urlString=httpPath+docKey+"/"+docKey+"-"+DateTool.dateToMonthString(date)+".xls";
		String filename=filePath+File.separator+docKey+"-"+DateTool.dateToMonthString(date)+".xls";
		
		
		try {
			saveUrl(filename,urlString);
			return filename;
		} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	public static void read(String inputFile) throws IOException  {
	    File inputWorkbook = new File(inputFile);
	    Workbook w;
	    try {
	      w = Workbook.getWorkbook(inputWorkbook);
	      // Get the first sheet
	      Sheet sheet = w.getSheet(0);
	      // Loop over first 10 column and lines
	      for (int i = 0; i < sheet.getRows(); i++) {
	    	  
	    	  String row="";
	    	  
	    	  for (int j = 0; j < sheet.getColumns(); j++) {
	    		  
	          Cell cell = sheet.getCell(j, i);
	          CellType type = cell.getType();
	          if (type == CellType.LABEL) {
	           // System.out.println("I got a label " + cell.getContents());
	        	  row+=cell.getContents()+";";
	          }

	          if (type == CellType.NUMBER) {
	           // System.out.println("I got a number "   + cell.getContents());
	            row+=cell.getContents()+";";
	          }

	        }
	        
	    	  System.out.println(row);
	        
	      }
	    } catch (BiffException e) {
	      e.printStackTrace();
	    }
	  }
	
	
	
	
	public static void saveUrl(final String filename, final String urlString)
	        throws MalformedURLException, IOException {
	    BufferedInputStream in = null;
	    FileOutputStream fout = null;
	    try {
	        in = new BufferedInputStream(new URL(urlString).openStream());
	        fout = new FileOutputStream(filename);

	        final byte data[] = new byte[1024];
	        int count;
	        while ((count = in.read(data, 0, 1024)) != -1) {
	            fout.write(data, 0, count);
	        }
	    } finally {
	        if (in != null) {
	            in.close();
	        }
	        if (fout != null) {
	            fout.close();
	        }
	    }
	}
	
	public static void main(String[] args) {
		//String docKey="gsk-d";
		String docKey="kred-d";
		
		String fileName=CesifoUtils.downloadXlsFile(docKey, "C:\\Users\\paul-edouard\\Desktop\\Programierung");
		try {
			CesifoUtils.read(fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
