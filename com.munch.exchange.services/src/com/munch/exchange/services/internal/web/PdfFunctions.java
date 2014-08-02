package com.munch.exchange.services.internal.web;

import java.io.IOException;
import java.util.LinkedList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class PdfFunctions {
	
	public String getPDFContentFromFile(String filename){
		try {
			PDDocument document = PDDocument.load(filename);
			PDFTextStripper s = new PDFTextStripper();
			String content = s.getText(document);
			
			return content;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return "Error by reading the file: "+filename;
		}
	}
	
	public static LinkedList<String> findPdfFiles(String input){
		LinkedList<String> pdfList=new LinkedList<String>();
		String searchStr=".pdf";
		String[] tockens=input.split("\n");
		for(int i=0;i<tockens.length;i++){
			if(!tockens[i].contains(searchStr))continue;
			
			String[] splits=tockens[i].split("\"");
			
			for(int j=0;j<splits.length;j++){
				if(splits[j].contains(searchStr)){
					//System.out.println(splits[j]);
					pdfList.add(splits[j]);
					
				}
			}
		}
		return pdfList;
		
	}
	

}
