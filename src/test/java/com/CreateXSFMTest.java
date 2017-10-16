package com;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class CreateXSFMTest {
	static Path pathy = FileSystems.getDefault().getPath("");
	static String PMMLPath = pathy.toAbsolutePath().toString()+ "/src/main/resources/pmml/";
	static String XSFMLPath = pathy.toAbsolutePath().toString()+ "/src/main/resources/xsfm/";

	@Test
	public void testToCreateTheFile() throws SAXException, IOException, ParserConfigurationException {
		String args[] = {PMMLPath+"single_iris_dectree.xml"};
		ReadPMMLtoSXFM.main(args);
		File f = new File(XSFMLPath+"single_iris_dectree.xml.xsfm");
		System.out.println(XSFMLPath+"single_iris_dectree.xml.xsfm");
		assertTrue(f.exists());
	}

}
