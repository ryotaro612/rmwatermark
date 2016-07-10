package com.ranceworks.rmwatermark;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfString;

/**
 * Hello world!
 *
 */
public class App {

	public static void main(String[] args) throws IOException, DocumentException {
		new App().operate();
	}

	private void operate() throws IOException, DocumentException {
		final PdfReader reader = new PdfReader(getClass().getResourceAsStream("test.pdf"));
		HashMap<String, String> a = reader.getInfo();
		a.clear();

		for (int p = 1; p <= reader.getNumberOfPages(); p++) {
			PdfDictionary dic = reader.getPageN(p);
			System.out.println(p);
			cln(dic);
			// visit(dic);
		}

		Document document = new Document();
		PdfCopy copy = new PdfCopy(document, new FileOutputStream("hoge.pdf"));
		document.open();
		copy.addDocument(reader);
		document.close();
		reader.close();
	}

	private void cln(PdfDictionary dic) {
		PdfArray array = (PdfArray) dic.get(PdfName.CONTENTS);
		if (array.size() > 0) {
			array.remove(array.size() - 1);
		}
		PdfArray arrayA = (PdfArray) dic.getDirectObject(PdfName.ANNOTS);
		// PdfArray arrayA = (PdfArray) dic.get(PdfName.ANNOTS);
		if (arrayA.size() > 0) {
			PdfObject aObject = arrayA.getDirectObject(arrayA.size() - 1);
			arrayA.remove(arrayA.size() - 1);
		}

	}

	private void visit(final PdfDictionary dic) {
		boolean found = false;
		PdfName foundName = null;
		for (PdfName name : dic.getKeys()) {
			if (dic.getDirectObject(name).isString()) {
				PdfString str = (PdfString) dic.getDirectObject(name);
				found = true;
				foundName = name;
				System.out.println("");
			} else {
				visit(dic.getDirectObject(name));
			}
		}
		if (found) {
			dic.put(foundName, new PdfString(""));
		}

	}

	private void visit(PdfObject pdfObj) {

		if (pdfObj.isString()) {
			PdfString a = (PdfString) pdfObj;
			System.out.println("");
		} else if (pdfObj.isIndirect()) {
			System.out.println("");
		} else if (pdfObj.isDictionary()) {
			PdfDictionary dic = (PdfDictionary) pdfObj;
			visit(dic);
		} else if (pdfObj.isArray()) {
			PdfArray array = (PdfArray) pdfObj;
			visit(array);
		} else if (pdfObj.isStream()) {
			PdfDictionary stm = (PdfDictionary) pdfObj;
			visit(stm);
		}
	}

	private void visit(final PdfArray array) {
		for (int i = 0; i < array.size(); i++) {
			visit(array.getDirectObject(i));
		}

	}

}