package com.plantpurple.emojidom.xml_base;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlBase {
	static final String XML_BASE_NAME = "emojidom.xml";
	
	static final String XML_ROOT  = "root";
	static final String XML_COOL = "cool";
	static final String XML_CLASSIC = "classic";
	static final String XML_MINE = "mine";
	static final String XML_EMOJI = "emoji";
	
	static final String EMOJI_ID = "id";
	static final String EMOJI_PATH = "path";
	
	public static final int COOL_EMOJI = 1;
	public static final int CLASSIC_EMOJI = 2;
	
	String mXmlFilePath;
	
	Document mDoc;
	
	Element root;
	Element cool;
	Element classic;
	Element mine;

	public XmlBase(String xmlFilePath) throws IOException {
		// full file name
		mXmlFilePath = xmlFilePath + XML_BASE_NAME;
		
		// create directory for file
		File file = new File(xmlFilePath);
		file.mkdirs();
		
		File xmlFile = new File(mXmlFilePath);
		if (xmlFile.exists()) {
			try {
				mDoc = readXml(mXmlFilePath);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}

			//Log.i("My", "File exist. Read.");
		} else {
			try {
				mDoc = createXmlFile(mXmlFilePath);
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
			
			//Log.i("My", "File not exist. Create.");
		}
		
		// get root element
		root = mDoc.getDocumentElement();
		
		// get cool element
		NodeList cools = root.getElementsByTagName(XML_COOL);
		cool = (Element) cools.item(0);
		
		// get classic element
		NodeList classics = root.getElementsByTagName(XML_CLASSIC);
		classic = (Element) classics.item(0);
		
		// get mine element
		NodeList mines = root.getElementsByTagName(XML_MINE);
		mine = (Element) mines.item(0);
	}
	
	public Element getCool() {return cool;}
	public Element getClassic() {return classic;}
	
	public void insertCool(int value) throws IOException {
		Element emoji = mDoc.createElement(XML_EMOJI);
		emoji.setAttribute(EMOJI_ID, String.valueOf(value));
		cool.appendChild(emoji);
		
		writeToFile(mDoc, mXmlFilePath);
	}
	
	public void insertClassic(int value) throws IOException {
		Element emoji = mDoc.createElement(XML_EMOJI);
		emoji.setAttribute(EMOJI_ID, String.valueOf(value));
		classic.appendChild(emoji);
		
		writeToFile(mDoc, mXmlFilePath);
	}
	
	public void insertMine(String value) throws IOException {
		Element emoji = mDoc.createElement(XML_EMOJI);
		emoji.setAttribute(EMOJI_PATH, value);
		mine.appendChild(emoji);
		
		writeToFile(mDoc, mXmlFilePath);
	}
	
	public int getCoolCount() {
		NodeList nodes = cool.getElementsByTagName(XML_EMOJI);
		
		if (nodes != null)
			return nodes.getLength();
		else 
			return 0;
	}
	
	public int getClassicCount() {
		NodeList nodes = classic.getElementsByTagName(XML_EMOJI);
		
		if (nodes != null)
			return nodes.getLength();
		else 
			return 0;
	}
	
	public int getMineCount() {
		NodeList nodes = mine.getElementsByTagName(XML_EMOJI);
		
		if (nodes != null)
			return nodes.getLength();
		else 
			return 0;
	}
	
	public ArrayList<Integer> getEmojiCool() {
		ArrayList<Integer> items = new ArrayList<Integer>();
		
		NodeList nodes = cool.getElementsByTagName(XML_EMOJI);
		if (nodes != null) {
			for (int i = 0; i != nodes.getLength(); ++i) {
				Node node = nodes.item(i);
				
				int id = Integer.parseInt(node.getAttributes().getNamedItem(EMOJI_ID).getNodeValue());
				items.add(id);
			}
		}
		
		return items;
	}
	
	public boolean isEmojiInCool(int emojiId) {
		boolean isExist = false;
		NodeList nodes = cool.getElementsByTagName(XML_EMOJI);
		if (nodes != null) {
			for (int i = 0; i != nodes.getLength(); ++i) {
				Node node = nodes.item(i);
				
				int id = Integer.parseInt(node.getAttributes().getNamedItem(EMOJI_ID).getNodeValue());
				if (id == emojiId) 
					isExist = true;
				else
					isExist = false;
			}
		}
		
		return isExist;
	}
	
	public ArrayList<Integer> getEmojiClassic() {
		ArrayList<Integer> items = new ArrayList<Integer>();
		
		NodeList nodes = classic.getElementsByTagName(XML_EMOJI);
		if (nodes != null) {
			for (int i = 0; i != nodes.getLength(); ++i) {
				Node node = nodes.item(i);
				
				int id = Integer.parseInt(node.getAttributes().getNamedItem(EMOJI_ID).getNodeValue());
				items.add(id);
			}
		}
		
		return items;
	}
	
	public boolean isEmojiInClassic(int emojiId) {
		boolean isExist = false;
		NodeList nodes = classic.getElementsByTagName(XML_EMOJI);
		if (nodes != null) {
			for (int i = 0; i != nodes.getLength(); ++i) {
				Node node = nodes.item(i);
				
				int id = Integer.parseInt(node.getAttributes().getNamedItem(EMOJI_ID).getNodeValue());
				if (id == emojiId) 
					isExist = true;
				else
					isExist = false;
			}
		}
		
		return isExist;
	}
	
	public ArrayList<String> getEmojiMine() {
		ArrayList<String> items = new ArrayList<String>();
		
		NodeList nodes = mine.getElementsByTagName(XML_EMOJI);
		if (nodes != null) {
			for (int i = 0; i != nodes.getLength(); ++i) {
				Node node = nodes.item(i);
				
				String path = node.getAttributes().getNamedItem(EMOJI_PATH).getNodeValue();
				items.add(path);
			}
		}
		
		return items;
	}
	
	public String getMineFilePath(int index) {
		NodeList nodes = mine.getElementsByTagName(XML_EMOJI);
		Node node = nodes.item(index);
		
		return node.getAttributes().getNamedItem(EMOJI_PATH).getTextContent();
	}
	
	public void deleteCool(int index) throws IOException {
		NodeList nodes = cool.getElementsByTagName(XML_EMOJI);
		
		if (nodes != null) {
			Node node = nodes.item(index);
			cool.removeChild(node);
			
			writeToFile(mDoc, mXmlFilePath);
		}
	}
	
	public void deleteClassic(int index) throws IOException {
		NodeList nodes = classic.getElementsByTagName(XML_EMOJI);
		
		if (nodes != null) {
			Node node = nodes.item(index);
			classic.removeChild(node);
			
			writeToFile(mDoc, mXmlFilePath);
		}
	}
	
	public void deleteMine(int index) throws IOException {
		NodeList nodes = mine.getElementsByTagName(XML_EMOJI);
		
		if (nodes != null) {
			Node node = nodes.item(index);
			mine.removeChild(node);
			
			writeToFile(mDoc, mXmlFilePath);
		}
	}
	
	public void deleteMine(String name) throws IOException {
		NodeList nodes = mine.getElementsByTagName(XML_EMOJI);
		
		for (int i = 0; i != nodes.getLength(); ++i) {
			Node node = nodes.item(i);
			
			if (node.getAttributes().getNamedItem(EMOJI_PATH).getNodeValue().equals(name)) {
				mine.removeChild(node);
				
				writeToFile(mDoc, mXmlFilePath);
				
				break;
			}
		}
	}
	
	private void writeToFile(Document doc, String filePath) throws IOException {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = factory.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		
		Properties outFormat = new Properties();
        outFormat.setProperty(OutputKeys.INDENT, "yes");
        outFormat.setProperty(OutputKeys.METHOD, "xml"); 
        outFormat.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        outFormat.setProperty(OutputKeys.VERSION, "1.0");
        outFormat.setProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperties(outFormat);
		
		DOMSource domSource = new DOMSource(doc.getDocumentElement());
		OutputStream output = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(output);
		
		try {
			transformer.transform(domSource, result);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
		Writer writer = new FileWriter(filePath);
		writer.write(output.toString());
		writer.close();
	}
	
	// Create xml file
	private Document createXmlFile(String xmlFilePath) throws ParserConfigurationException, 
															  TransformerConfigurationException,
															  TransformerException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.newDocument();
		
		Element root = document.createElement(XML_ROOT);
		document.appendChild(root);
		
		Element cool = document.createElement(XML_COOL);
		root.appendChild(cool);
		
		Element classic = document.createElement(XML_CLASSIC);
		root.appendChild(classic);
		
		Element mine = document.createElement(XML_MINE);
		root.appendChild(mine);
		
		writeToFile(document, mXmlFilePath);
		
		return document;
	}
	
	// If file create we just read existing file
	private Document readXml(String xmlFilePath) throws ParserConfigurationException, 
														SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
					
		FileInputStream f = new FileInputStream(xmlFilePath);
		Document document = builder.parse(f);
		
		f.close();
		
		return document;
	}

	
}
