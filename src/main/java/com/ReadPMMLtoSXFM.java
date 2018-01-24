package com;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.XMLFeatureModelParserSample;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;


public class ReadPMMLtoSXFM {

	
	static Logger LOGGER = LogManager.getLogger(ReadPMMLtoSXFM.class.getName());

	static final Path HOME_PATH = FileSystems.getDefault().getPath("");
	static final String SXFMPATH = HOME_PATH.toAbsolutePath().toString()+ "/src/main/resources/sxfm/";
	static final String FILE_NAME_STRING = "SXFMModel.sxfm";
	
		
	static String featureModelName = "featureModelName";
	static ArrayList<String> sxfmModelContent = new ArrayList<String>();
	static int cont = 0;
	static boolean bandera = false;

	private ReadPMMLtoSXFM() {
		// No-op; won't be called
		}

	public static void main(String[] args) throws SAXException, IOException,
			ParserConfigurationException {

				LOGGER.printf(Level.INFO,"Hi, I am " + ReadPMMLtoSXFM.class.getName());

				ArrayList<String> KEYHASH = new ArrayList<String>();
					KEYHASH.add("description");
					KEYHASH.add("creator");
					KEYHASH.add("address");
					KEYHASH.add("email");
					KEYHASH.add("phone");
					KEYHASH.add("website");
					KEYHASH.add("organization");
					KEYHASH.add("date");
					KEYHASH.add("reference");
					
			 ArrayList<String> VALUEHASH = new ArrayList<String>();
						VALUEHASH.add("a description");
						VALUEHASH.add("pingoz");
						VALUEHASH.add("home");
						VALUEHASH.add("josepplloo@gmail.com");
						VALUEHASH.add("3053573300");
						VALUEHASH.add("https://github.com/josepplloo/tamarindo");
						VALUEHASH.add("a organization");
						VALUEHASH.add("a date");
						VALUEHASH.add("a reference");
					

		// hacemos una fabrica de objetos parser
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// configuramos la fabrica
		factory.setValidating(false);
		factory.setNamespaceAware(false);
		// obtenemos el constructor del elemento parser
		DocumentBuilder builder = factory.newDocumentBuilder();
		// obtenemos el archivo XML quye se recibe como argumento
		Document document = builder.parse(new File(args[0]));

		String[] fileNamePath = document.getBaseURI().split(FileSystems.getDefault().getSeparator());
		String nameFileToUse =fileNamePath[fileNamePath.length-1];
		
		searchNode(document);
		try {
			generate(nameFileToUse, KEYHASH, VALUEHASH);
		} catch (Exception e){
			LOGGER.printf(Level.ERROR,"File exception or error generating the file "+ e);
		}

	}

	public static void searchNode(Node node) {

		if (node == null) {
			LOGGER.printf(Level.INFO,"::nothing to do, node is null::");
			return;
		}

		int type = node.getNodeType();

		switch (type) {
		case Node.DOCUMENT_NODE: {
			LOGGER.printf(Level.INFO,"<?xml version=\"1.0\"?>");
			searchNode(((Document) node).getDocumentElement());
			break;
		}// se supone que este es para la raiz DOCUMENT_NODE

		case Node.ELEMENT_NODE: {
			LOGGER.printf(Level.INFO,"<");
			LOGGER.printf(Level.INFO,node.getNodeName());

			// atributos
			Attr VAttrib[] = getAttrArray(node.getAttributes());
			int item = 0;
			if (node.getNodeName().trim().equalsIgnoreCase("DataField")) {
				bandera = true;
				sxfmModelContent.add("\n\t:m " + VAttrib[1].getNodeValue()
						+ "(_r_" + cont + ")");
				cont++;
				
			}
			if (node.getNodeName().trim().equalsIgnoreCase("Value")) {
				if (bandera == true) {
					sxfmModelContent.add("\n\t\t:g (_r_" + (cont - 1) + "_"
							+ cont + ")[1,1]");
					bandera = false;
					item = cont;
					cont++;
				}
				
				sxfmModelContent.add("\n\t\t\t: "
						+ VAttrib[0].getNodeValue()+"(_r_" + (item - 1) + "_"
								+ item +"_"+(cont++)+ ")");
			}
			if (node.getNodeName().trim().equalsIgnoreCase("TreeModel"))
				featureModelName = VAttrib[2].getNodeValue();
			for (int i = 0; i < VAttrib.length; i++) {
				Attr attrib = VAttrib[i];

				LOGGER.printf(Level.INFO," " + attrib.getNodeName() + "=\"");
				LOGGER.printf(Level.INFO,strToXML(attrib.getNodeValue()) + "\"");

			}
			LOGGER.printf(Level.INFO,">");
			// esto es para los hijos de los hijos

			NodeList childrens = node.getChildNodes();
			if (childrens != null) {

				for (int i = 0; i < childrens.getLength(); i++) {

					searchNode(childrens.item(i));
				}
			}
			break;
		}// este es para los hijos ELEMENT_NODE

		case Node.ENTITY_REFERENCE_NODE: {

			LOGGER.printf(Level.INFO,"&");
			LOGGER.printf(Level.INFO,node.getNodeName());
			LOGGER.printf(Level.INFO,";");
			break;
		}// para las referencias

		case Node.CDATA_SECTION_NODE:
		case Node.TEXT_NODE: {
			// Eliminate <,>,& and quotation marks and
			// write to output file.
			LOGGER.printf(Level.INFO,strToXML(node.getNodeValue()));
			break;
		}// end case Node.TEXT_NODE

		// Handle processing instruction
		case Node.PROCESSING_INSTRUCTION_NODE: {
			LOGGER.printf(Level.INFO,"<?");
			LOGGER.printf(Level.INFO,node.getNodeName());
			String data = node.getNodeValue();
			if (data != null && data.length() > 0) {
				LOGGER.printf(Level.INFO," ");// write space
				LOGGER.printf(Level.INFO,data);
			}// end if
			LOGGER.printf(Level.INFO,"?>");
			break;
		}// end Node.PROCESSING_INSTRUCTION_NODE

		default:
			break;
		}// fin del case

		if (type == Node.ELEMENT_NODE) {
			LOGGER.printf(Level.INFO,"</" + node.getNodeName() + ">");
		}// end if

	}

	// -----------

	private static String strToXML(String s) {
		StringBuffer str = new StringBuffer();

		int len = (s != null) ? s.length() : 0;

		for (int i = 0; i < len; i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '<': {
				str.append("&lt;");
				break;
			}// end case '<'
			case '>': {
				str.append("&gt;");
				break;
			}// end case '>'
			case '&': {
				str.append("&amp;");
				break;
			}// end case '&'
			case '"': {
				str.append("&quot;");
				break;
			}// end case '"'
			default: {
				str.append(ch);
			}// end default
			}// end switch
		}// end for loop

		return str.toString();

	}// end strToXML()
		// -------------------------------------------//

	// This method converts a NamedNodeMap into an
	// array of type Attr
	private static Attr[] getAttrArray(NamedNodeMap attrs) {
		int len = (attrs != null) ? attrs.getLength() : 0;
		Attr array[] = new Attr[len];
		for (int i = 0; i < len; i++) {
			array[i] = (Attr) attrs.item(i);
		}// end for loop

		return array;
	}// end getAttrArray()

	// -------------------------------------------//

	public static void generate(String name, ArrayList<String> key,
			ArrayList<String> value) throws Exception {

		if (key.isEmpty() || value.isEmpty() || key.size() != value.size()) {
			LOGGER.error("ERROR empty ArrayList");
			return;
		} else {

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			DOMImplementation implementation = builder.getDOMImplementation();
			Document document = implementation.createDocument(null,
					"feature_model", null);
			document.setXmlVersion("1.0");

			// Main Node
			Element raiz = document.getDocumentElement();
			raiz.setAttribute("name", name);

			// Por cada key creamos un item que contendra la key y el value
			Element itemNode = document.createElement("meta");
			// Item Node
			for (int i = 0; i < key.size(); i++) {

				// Key Node
				Element keyNode = document.createElement("data");
				Text nodeKeyValue = document.createTextNode(value.get(i));
				keyNode.appendChild(nodeKeyValue);
				keyNode.setAttribute("name", key.get(i));
				itemNode.appendChild(keyNode);

				// append itemNode to raiz
				raiz.appendChild(itemNode); // pegamos el elemento a la raiz
											// "Documento"
			}
			Element itemNode2 = document.createElement("feature_tree");
			String featuretree = ":r " + featureModelName + "(_r)";

			for (int i = 0; i < sxfmModelContent.size(); i++) {
				featuretree += sxfmModelContent.get(i);

			}

			itemNode2.setTextContent(featuretree);
			raiz.appendChild(itemNode2);
			Element itemNode3 = document.createElement("constraints"); //crear los demas elementos del modelo de caracteristicas
			//itemNode3.setTextContent("constrait_1:_r_0 or _r_1");
		    raiz.appendChild(itemNode3);
			// Generate XML
			Source source = new DOMSource(document);
			// Indicamos donde lo queremos almacenar
			Result result = new StreamResult(new java.io.File(SXFMPATH + name));
			LOGGER.printf(Level.INFO,"Creating file " +SXFMPATH + name);
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.transform(source, result);
			String args[] = {SXFMPATH + name};
			XMLFeatureModelParserSample.main(args);
		}
	}

}
