package com;

import java.io.File;
import java.io.IOException;
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

import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class ReadPMMLtoSXFM {

	static String XSFMPath = "/Users/josepplloo/workspace/tamarindo/src/main/resources/xsfm/";
	static String nombre_archivo = "PingoModel";
	static ArrayList<String> key;
	static ArrayList<String> value;
	static String nombre_modelo = "";
	static ArrayList<String> restricciones_modelo = new ArrayList<String>();
	static int cont = 0;
	static boolean bandera = false;

	public static void main(String[] args) throws SAXException, IOException,
			ParserConfigurationException {

		// hacemos una fabrica de objetos parser
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// configuramos la fabrica
		factory.setValidating(false);
		factory.setNamespaceAware(false);
		// obtenemos el constructor del elemento parser
		DocumentBuilder builder = factory.newDocumentBuilder();
		// obtenemos el archivo XML
		Document document = builder.parse(new File(args[0]));

		key = new ArrayList<String>();
		value = new ArrayList<String>();
		key.add("description");
		value.add("a description");
		searchNode(document);
		key.add("creator");
		value.add("pingoz");
		key.add("address");
		value.add("udea");
		key.add("email");
		value.add("jose.garciag@udea.edu.co");
		key.add("phone");
		value.add("3053573300");
		key.add("website");
		value.add("a website");
		key.add("organization");
		value.add("a organization");
		key.add("date");
		value.add("a date");
		key.add("reference");
		value.add("a reference");

		// ///////////////////////// aca inici con el armado

		try {
			generate(nombre_archivo, key, value);
		} catch (Exception e) {
		}

	}

	public static void searchNode(Node node) {

		if (node == null) {
			System.out.println("::nothing to do, node is null::");
			return;
		}

		int type = node.getNodeType();

		switch (type) {
		case Node.DOCUMENT_NODE: {
			System.out.println("<?xml version=\"1.0\"?>");
			searchNode(((Document) node).getDocumentElement());
			break;
		}// se supone que este es para la raiz DOCUMENT_NODE

		case Node.ELEMENT_NODE: {
			System.out.println("<");
			System.out.println(node.getNodeName());

			// atributos
			Attr VAttrib[] = getAttrArray(node.getAttributes());
			int item = 0;
			if (node.getNodeName().trim().equalsIgnoreCase("DataField")) {
				bandera = true;
				restricciones_modelo.add("\n\t:m " + VAttrib[1].getNodeValue()
						+ "(_r_" + cont + ")");
				cont++;
				
			}
			if (node.getNodeName().trim().equalsIgnoreCase("Value")) {
				if (bandera == true) {
					restricciones_modelo.add("\n\t\t:g (_r_" + (cont - 1) + "_"
							+ cont + ")[1,1]");
					bandera = false;
					item = cont;
					cont++;
				}
				
				restricciones_modelo.add("\n\t\t\t: "
						+ VAttrib[0].getNodeValue()+"(_r_" + (item - 1) + "_"
								+ item +"_"+(cont++)+ ")");
			}
			if (node.getNodeName().trim().equalsIgnoreCase("TreeModel"))
				nombre_modelo = VAttrib[2].getNodeValue();
			for (int i = 0; i < VAttrib.length; i++) {
				Attr attrib = VAttrib[i];

				System.out.println(" " + attrib.getNodeName() + "=\"");
				System.out.println(strToXML(attrib.getNodeValue()) + "\"");

			}
			System.out.println(">");
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

			System.out.println('&');
			System.out.println(node.getNodeName());
			System.out.println(';');
			break;
		}// para las referencias

		case Node.CDATA_SECTION_NODE:
		case Node.TEXT_NODE: {
			// Eliminate <,>,& and quotation marks and
			// write to output file.
			System.out.println(strToXML(node.getNodeValue()));
			break;
		}// end case Node.TEXT_NODE

		// Handle processing instruction
		case Node.PROCESSING_INSTRUCTION_NODE: {
			System.out.println("<?");
			System.out.println(node.getNodeName());
			String data = node.getNodeValue();
			if (data != null && data.length() > 0) {
				System.out.println(' ');// write space
				System.out.println(data);
			}// end if
			System.out.println("?>");
			break;
		}// end Node.PROCESSING_INSTRUCTION_NODE

		default:
			break;
		}// fin del case

		if (type == Node.ELEMENT_NODE) {
			System.out.println("</" + node.getNodeName() + ">");
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
			System.out.println("ERROR empty ArrayList");
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
			raiz.setAttribute("name", "PingoModel");

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
			String featuretree = ":r " + nombre_modelo + "(_r)";

			for (int i = 0; i < restricciones_modelo.size(); i++) {
				featuretree += restricciones_modelo.get(i);

			}

			itemNode2.setTextContent(featuretree);
			raiz.appendChild(itemNode2);
			Element itemNode3 = document.createElement("constraints");
			itemNode3.setTextContent("constrait_1:_r_0 or _r_1");
			raiz.appendChild(itemNode3);
			// Generate XML
			Source source = new DOMSource(document);
			// Indicamos donde lo queremos almacenar
			Result result = new StreamResult(new java.io.File(XSFMPath + name
					+ ".xml")); // nombre del archivo
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.transform(source, result);
		}
	}

}
