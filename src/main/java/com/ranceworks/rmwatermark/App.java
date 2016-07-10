package com.ranceworks.rmwatermark;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
		}

		// visit(dic);
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
			if (dic.get(name).isString()) {
				PdfString str = (PdfString) dic.get(name);
				found = true;
				foundName = name;
				System.out.println("");
			} else {
				visit(dic.get(name));
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
			if (array.getDirectObject(i).getBytes() != null) {
				if (new String(array.getDirectObject(i).getBytes()).contains("")) {
					System.out.println("");
				}
			}

			visit(array.getDirectObject(i));
		}

	}

	private List<PdfDictionary> flatDict(PdfDictionary dic) {

		List<PdfObject> objects = dic.getKeys().stream().map(a -> dic.get(a)).collect(Collectors.toList());

		List<PdfDictionary> dictionaries = objects.stream().filter(o -> o.isDictionary()).map(o -> (PdfDictionary) o)
				.collect(Collectors.toList());

		Stream.of(new File(".").listFiles())
				.flatMap(file -> file.listFiles() == null ? Stream.of(file) : Stream.of(file.listFiles()))
				.collect(Collectors.toList());

		List<PdfDictionary> dictionaries2 = Stream.of((PdfDictionary[]) dictionaries.toArray())
				.flatMap(m -> Stream.of((PdfDictionary[]) flatDict(m).toArray())).collect(Collectors.toList());
		/*
		 * 
		 * 
		 * List<PdfObject> dics = objects.stream().filter(o ->
		 * o.isDictionary()).map(a -> flatDict((PdfDictionary) a)) .flatMap(c ->
		 * Stream.of((PdfObject[]) c.toArray())).collect(Collectors.toList());
		 */

		List<PdfObject> notDicts = objects.stream().filter(o -> !o.isDictionary()).collect(Collectors.toList());

		return null;
	}
}