package com.munch.exchange.services.internal.onvista;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class PdfTest {
	
	
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
		
		
		String filename="C:\\Users\\paul-edouard\\Desktop\\Programierung\\MyPdf.pdf";
		String urlString="http://www.jenoptik.com/cms/jenoptik.nsf/res/Jenoptik-Zwischenbericht%202014-1.pdf/$file/Jenoptik-Zwischenbericht%202014-1.pdf";
		
		try {
			saveUrl(filename,urlString);
		} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ;
		}
		
		
		try {
			PDDocument document = PDDocument.load(filename);
			PDFTextStripper s = new PDFTextStripper();
			String content = s.getText(document);
			
			System.out.println(content);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
