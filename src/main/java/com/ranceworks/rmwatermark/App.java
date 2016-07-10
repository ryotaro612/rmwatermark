package com.ranceworks.rmwatermark;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;

/**
 * Hello world!
 *
 */
public class App {

	public static void main(String[] args) throws IOException, DocumentException {
		new App().operate();
	}

	private void operate() throws IOException {
		final PdfReader reader = new PdfReader(getClass().getResourceAsStream("test.pdf"));

		PdfDictionary dic = reader.getPageN(1);

		visit(dic);
	}

	private void visit(PdfDictionary dic) {

		dic.getKeys().stream().filter(k -> PdfName.ANNOTS.equals(k)).forEach((PdfName k) -> {
			visit(dic.get(k));
		});

	}

	private void visit(PdfObject pdfObj) {

		if (pdfObj.isDictionary()) {
			PdfDictionary dic = (PdfDictionary) pdfObj;
			visit(dic);
		} else if (pdfObj.isArray()) {
			PdfArray array = (PdfArray) pdfObj;
			visit(array);
		}
	}

	private void visit(final PdfArray array) {
		if (!array.isEmpty()) {
			IntStream.range(0, array.size()).forEach(i -> {
				visit(array.getDirectObject(i));
			});
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