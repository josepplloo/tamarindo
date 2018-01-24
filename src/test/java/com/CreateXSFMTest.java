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
	static final String PMMLPATH = pathy.toAbsolutePath().toString()+ "/src/main/resources/pmml/";
	static final String XSFMLPATH = pathy.toAbsolutePath().toString()+ "/src/main/resources/sxfm/";
    static final String EVALFILE = "single_iris_dectree.xml";
	@Test
	public void testToCreateTheFile() throws SAXException, IOException, ParserConfigurationException {
		String args[] = {PMMLPATH+EVALFILE};
		ReadPMMLtoSXFM.main(args);
		File f = new File(XSFMLPATH+EVALFILE);
		assertTrue(f.exists());
	}

}
